package com.bluebells.finance;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.finance.dto.*;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private final FeeCategoryRepository categoryRepository;
    private final FeeStructureRepository structureRepository;
    private final FeeInvoiceRepository invoiceRepository;
    private final FeeInvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository allocationRepository;
    private final AcademicSessionRepository sessionRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public FeeService(
            FeeCategoryRepository categoryRepository,
            FeeStructureRepository structureRepository,
            FeeInvoiceRepository invoiceRepository,
            FeeInvoiceItemRepository invoiceItemRepository,
            PaymentRepository paymentRepository,
            PaymentAllocationRepository allocationRepository,
            AcademicSessionRepository sessionRepository,
            ClassRepository classRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.structureRepository = structureRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.paymentRepository = paymentRepository;
        this.allocationRepository = allocationRepository;
        this.sessionRepository = sessionRepository;
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    private synchronized String generateUniqueInvoiceNumber(String monthFormatted) {
        long count = invoiceRepository.count() + 1;
        String candidate = String.format("INV-%s-%04d", monthFormatted, count);
        long suffix = 1;
        while (invoiceRepository.existsByInvoiceNumber(candidate)) {
            candidate = String.format("INV-%s-%04d", monthFormatted, count + suffix);
            suffix++;
        }
        return candidate;
    }

    private synchronized String generateUniquePaymentReference(String dateFormatted) {
        long count = paymentRepository.count() + 1;
        String candidate = String.format("PAY-%s-%04d", dateFormatted, count);
        long suffix = 1;
        while (paymentRepository.existsByPaymentReference(candidate)) {
            candidate = String.format("PAY-%s-%04d", dateFormatted, count + suffix);
            suffix++;
        }
        return candidate;
    }

    @Transactional
    public FeeStructureDto createFeeStructure(CreateFeeStructureRequest request) {
        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        ClassEntity schoolClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getClassId()));

        FeeCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Fee category not found with id: " + request.getCategoryId()));

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);

        Optional<FeeStructure> existing = structureRepository.findByAcademicSessionIdAndClassEntityIdAndCategoryId(
                session.getId(), schoolClass.getId(), category.getId());

        FeeStructure fs;
        if (existing.isPresent()) {
            fs = existing.get();
            fs.setAmount(amount);
            fs.setFrequency(request.getFrequency() != null ? request.getFrequency() : "MONTHLY");
        } else {
            fs = new FeeStructure(session, schoolClass, category, amount, request.getFrequency());
        }

        FeeStructure saved = structureRepository.save(fs);
        return FeeStructureDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FeeStructureDto> getFeeStructures(Long sessionId, Long classId) {
        if (sessionId == null) {
            sessionId = sessionRepository.findByCurrentTrue()
                    .map(AcademicSession::getId)
                    .orElse(1L);
        }

        List<FeeStructure> structures;
        if (classId != null) {
            structures = structureRepository.findByAcademicSessionIdAndClassEntityId(sessionId, classId);
        } else {
            structures = structureRepository.findByAcademicSessionId(sessionId);
        }

        return structures.stream().map(FeeStructureDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public FeeInvoiceDto createInvoice(CreateInvoiceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<FeeInvoiceItem> items = new ArrayList<>();

        for (CreateInvoiceItemRequest itemReq : request.getItems()) {
            FeeCategory category = categoryRepository.findById(itemReq.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + itemReq.getCategoryId()));

            BigDecimal itemAmt = itemReq.getAmount().setScale(2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(itemAmt);

            FeeInvoiceItem item = new FeeInvoiceItem(null, category, itemAmt);
            items.add(item);
        }

        BigDecimal discount = (request.getDiscountAmount() != null) ?
                request.getDiscountAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        if (discount.compareTo(totalAmount) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Discount amount cannot exceed total invoice amount");
        }

        BigDecimal balanceDue = totalAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        String monthFormatted = request.getBillingMonth().replace("-", "");
        String invNumber = generateUniqueInvoiceNumber(monthFormatted);

        FeeInvoice invoice = new FeeInvoice(
                invNumber,
                student,
                session,
                request.getBillingMonth(),
                totalAmount,
                discount,
                balanceDue,
                request.getDueDate()
        );

        for (FeeInvoiceItem item : items) {
            invoice.addItem(item);
        }

        FeeInvoice saved = invoiceRepository.save(invoice);
        return FeeInvoiceDto.fromEntity(saved);
    }

    @Transactional
    public List<FeeInvoiceDto> generateMonthlyInvoices(GenerateMonthlyInvoicesRequest request) {
        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        List<Enrollment> enrollments;
        if (request.getClassId() != null) {
            enrollments = enrollmentRepository.findAll().stream()
                    .filter(e -> "ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus()) &&
                                 e.getSection().getClassEntity().getId().equals(request.getClassId()) &&
                                 e.getAcademicSession().getId().equals(session.getId()))
                    .collect(Collectors.toList());
        } else {
            enrollments = enrollmentRepository.findAll().stream()
                    .filter(e -> "ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus()) &&
                                 e.getAcademicSession().getId().equals(session.getId()))
                    .collect(Collectors.toList());
        }

        List<FeeInvoice> generated = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();
            Long classId = enrollment.getSection().getClassEntity().getId();

            // Skip if invoice already generated for this student and billing month
            Optional<FeeInvoice> existing = invoiceRepository.findByStudentIdAndAcademicSessionIdAndBillingMonth(
                    student.getId(), session.getId(), request.getBillingMonth());
            if (existing.isPresent()) {
                continue;
            }

            List<FeeStructure> classStructures = structureRepository.findByAcademicSessionIdAndClassEntityId(
                    session.getId(), classId);

            if (classStructures.isEmpty()) {
                continue;
            }

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<FeeInvoiceItem> items = new ArrayList<>();

            for (FeeStructure fs : classStructures) {
                totalAmount = totalAmount.add(fs.getAmount());
                FeeInvoiceItem item = new FeeInvoiceItem(null, fs.getCategory(), fs.getAmount());
                items.add(item);
            }

            String monthFormatted = request.getBillingMonth().replace("-", "");
            String invNumber = generateUniqueInvoiceNumber(monthFormatted);

            FeeInvoice invoice = new FeeInvoice(
                    invNumber,
                    student,
                    session,
                    request.getBillingMonth(),
                    totalAmount.setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    totalAmount.setScale(2, RoundingMode.HALF_UP),
                    request.getDueDate()
            );

            for (FeeInvoiceItem item : items) {
                invoice.addItem(item);
            }

            generated.add(invoiceRepository.save(invoice));
        }

        return generated.stream().map(FeeInvoiceDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeInvoiceDto> getInvoicesForStudent(Long studentId) {
        return invoiceRepository.findByStudentIdOrderByDueDateDesc(studentId).stream()
                .map(FeeInvoiceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeeInvoiceDto getInvoiceById(Long invoiceId) {
        FeeInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
        return FeeInvoiceDto.fromEntity(invoice);
    }

    @Transactional
    public PaymentDto recordPayment(RecordPaymentRequest request, UserPrincipal currentUser) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        User receiver = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        BigDecimal paymentAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Payment amount must be greater than zero");
        }

        LocalDate payDate = request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now();
        String dateFormatted = payDate.toString().replace("-", "");
        String ref = generateUniquePaymentReference(dateFormatted);

        Payment payment = new Payment(
                ref,
                student,
                paymentAmount,
                payDate,
                request.getPaymentMethod(),
                receiver,
                request.getNotes()
        );

        if (request.getInvoiceId() != null) {
            FeeInvoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + request.getInvoiceId()));

            if (!invoice.getStudent().getId().equals(student.getId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invoice does not belong to the specified student");
            }

            if ("PAID".equalsIgnoreCase(invoice.getStatus()) || invoice.getBalanceDue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invoice " + invoice.getInvoiceNumber() + " is already fully paid");
            }

            if (paymentAmount.compareTo(invoice.getBalanceDue()) > 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Payment amount (" + paymentAmount + ") exceeds invoice balance due (" + invoice.getBalanceDue() + ")");
            }

            BigDecimal allocAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
            BigDecimal newPaid = invoice.getPaidAmount().add(allocAmount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal newBalance = invoice.getBalanceDue().subtract(allocAmount).setScale(2, RoundingMode.HALF_UP);

            invoice.setPaidAmount(newPaid);
            invoice.setBalanceDue(newBalance);
            if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                invoice.setStatus("PAID");
            } else {
                invoice.setStatus("PARTIALLY_PAID");
            }

            PaymentAllocation allocation = new PaymentAllocation(payment, invoice, allocAmount);
            payment.addAllocation(allocation);
        } else {
            // FIFO allocation across student's unpaid invoices
            List<FeeInvoice> unpaidInvoices = invoiceRepository.findByStudentIdAndStatusInOrderByDueDateAsc(
                    student.getId(), List.of("UNPAID", "PARTIALLY_PAID"));

            BigDecimal totalOutstanding = unpaidInvoices.stream()
                    .map(FeeInvoice::getBalanceDue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            if (unpaidInvoices.isEmpty() || totalOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Student has no outstanding unpaid invoices");
            }

            if (paymentAmount.compareTo(totalOutstanding) > 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Payment amount (" + paymentAmount + ") exceeds total outstanding fees (" + totalOutstanding + ")");
            }

            BigDecimal remainingToAllocate = paymentAmount;

            for (FeeInvoice inv : unpaidInvoices) {
                if (remainingToAllocate.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal alloc = remainingToAllocate.min(inv.getBalanceDue()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal newPaid = inv.getPaidAmount().add(alloc).setScale(2, RoundingMode.HALF_UP);
                BigDecimal newBalance = inv.getBalanceDue().subtract(alloc).setScale(2, RoundingMode.HALF_UP);

                inv.setPaidAmount(newPaid);
                inv.setBalanceDue(newBalance);
                if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                    inv.setStatus("PAID");
                } else {
                    inv.setStatus("PARTIALLY_PAID");
                }

                PaymentAllocation allocation = new PaymentAllocation(payment, inv, alloc);
                payment.addAllocation(allocation);

                remainingToAllocate = remainingToAllocate.subtract(alloc);
            }
        }

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentDto.fromEntity(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsForStudent(Long studentId) {
        return paymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId).stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeeInvoiceDto> getAllInvoices(String status, String billingMonth) {
        List<FeeInvoice> invoices = invoiceRepository.findAll(Sort.by(Sort.Direction.DESC, "dueDate"));
        return invoices.stream()
                .filter(inv -> status == null || status.isBlank() || inv.getStatus().equalsIgnoreCase(status))
                .filter(inv -> billingMonth == null || billingMonth.isBlank() || inv.getBillingMonth().equalsIgnoreCase(billingMonth))
                .map(FeeInvoiceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "paymentDate")).stream()
                .map(PaymentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
