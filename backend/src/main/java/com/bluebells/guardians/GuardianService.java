package com.bluebells.guardians;

import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.guardians.dto.GuardianDto;
import com.bluebells.guardians.dto.LinkGuardianRequest;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.bluebells.students.dto.StudentDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuardianService {

    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository linkRepository;
    private final StudentRepository studentRepository;

    public GuardianService(
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository linkRepository,
            StudentRepository studentRepository) {
        this.guardianRepository = guardianRepository;
        this.linkRepository = linkRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getMyChildren(UserPrincipal principal) {
        if (principal == null) {
            return List.of();
        }
        List<GuardianStudentLink> links = linkRepository.findByGuardianUserId(principal.getId());
        return links.stream()
                .map(link -> StudentDto.fromEntity(link.getStudent()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isParentOfStudent(Long userId, Long studentId) {
        return linkRepository.existsByGuardianUserIdAndStudentId(userId, studentId);
    }

    @Transactional(readOnly = true)
    public GuardianDto getGuardianById(Long id) {
        Guardian guardian = guardianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + id));
        return GuardianDto.fromEntity(guardian);
    }

    @Transactional(readOnly = true)
    public List<GuardianDto> getGuardiansForStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        List<GuardianStudentLink> links = linkRepository.findByStudentId(studentId);
        return links.stream()
                .map(link -> GuardianDto.fromEntity(link.getGuardian()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkGuardian(Long studentId, LinkGuardianRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Guardian guardian = guardianRepository.findById(request.getGuardianId())
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + request.getGuardianId()));

        GuardianStudentLink link = new GuardianStudentLink(
                guardian,
                student,
                request.isPrimaryContact(),
                request.isFinancialSponsor()
        );
        linkRepository.save(link);
    }
}
