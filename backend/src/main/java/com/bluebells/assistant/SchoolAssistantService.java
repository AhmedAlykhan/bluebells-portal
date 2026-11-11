package com.bluebells.assistant;

import com.bluebells.assistant.dto.AssistantFaqDto;
import com.bluebells.assistant.dto.AssistantQueryRequest;
import com.bluebells.assistant.dto.AssistantQueryResponse;
import com.bluebells.assistant.dto.AssistantQueryResponse.SuggestedActionDto;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SchoolAssistantService {

    private final UserRepository userRepository;

    public SchoolAssistantService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AssistantQueryResponse answerQuery(AssistantQueryRequest req) {
        String q = req.getQuestion() != null ? req.getQuestion().trim().toLowerCase() : "";

        // 1. Security & Guardrail Checks (Prevent unauthorized data access / modification)
        if (isDataTamperingIntent(q)) {
            return new AssistantQueryResponse(
                    "Security Boundary Alert: As an automated school assistant, I am strictly prohibited from altering marks, modifying attendance logs, adjusting tuition fee balances, or modifying database records. Such operations require authoritative approval and must be processed by authorized school leadership or logged through the Support & Complaint Centre.",
                    "SECURITY_GUARDRAIL",
                    1.0,
                    "Bluebells Institutional Security Protocol",
                    List.of("How do I contact the Principal's Office?", "How do I open a support ticket?"),
                    List.of(new SuggestedActionDto("Open Support Ticket", "/support"), new SuggestedActionDto("View Staff Directory", "/staff"))
            );
        }

        if (isPrivacyViolationIntent(q)) {
            return new AssistantQueryResponse(
                    "Privacy Notice: Student contact numbers, home addresses, National CNIC details, and private family records cannot be disclosed through the virtual assistant. To update personal records or verify enrolled information, please visit the Administration desk at the Latifabad Campus.",
                    "PRIVACY_GUARDRAIL",
                    1.0,
                    "Student Data Privacy & Governance Policy",
                    List.of("Where is the school campus located?", "What are the administrative office hours?"),
                    List.of(new SuggestedActionDto("Campus Overview", "/"))
            );
        }

        // 2. Intent Routing & Verified Knowledge Retrieval
        if (containsAny(q, "timing", "time", "hours", "opening", "closing", "assembly", "friday", "schedule")) {
            return new AssistantQueryResponse(
                    "Bluebells Schooling System operates on the following official schedule:\n\n" +
                    "• Regular Days (Monday – Thursday): 7:45 AM – 1:30 PM (Morning Assembly starts promptly at 7:55 AM).\n" +
                    "• Friday Schedule: 7:45 AM – 12:00 PM (Dismissal before Jummah prayers).\n" +
                    "• Administrative & Admissions Office: Open Monday – Saturday, 8:00 AM – 2:00 PM (Saturdays: 9:00 AM – 1:00 PM).\n" +
                    "• Campus Address: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh.",
                    "TIMINGS_AND_SCHEDULE",
                    0.98,
                    "Official Campus Handbook 2026-2027",
                    List.of("What are the examination timings?", "How do I check my class timetable?"),
                    List.of(new SuggestedActionDto("View Class Timetable", "/timetable"))
            );
        }

        if (containsAny(q, "admission", "admit", "apply", "enroll", "eligibility", "documents", "b-form", "birth certificate", "criteria")) {
            return new AssistantQueryResponse(
                    "Admissions Procedure at Bluebells Schooling System:\n\n" +
                    "1. Eligibility Criteria: Nursery (Age 3+), Kindergarten (Age 4+), Grade 1 (Age 5+).\n" +
                    "2. Required Documentation:\n" +
                    "   • Nadra B-Form / Child Registration Certificate (Original & Copy)\n" +
                    "   • Father / Guardian CNIC Copy\n" +
                    "   • 4 Recent Passport Size Photographs (Blue Background)\n" +
                    "   • School Leaving Certificate (SLC) from previous recognized institution (Grade 2 upwards)\n" +
                    "3. Assessment: Short written evaluation in English, Mathematics, and Urdu followed by an informal parental meeting.\n" +
                    "4. Online Inquiries: You can submit admission forms directly via our portal or visit the Admissions Desk.",
                    "ADMISSIONS",
                    0.96,
                    "Admissions & Enrollment Prospectus",
                    List.of("What are the tuition fees for new admissions?", "Where can I submit an admission inquiry?"),
                    List.of(new SuggestedActionDto("Admissions Desk", "/admissions"))
            );
        }

        if (containsAny(q, "fee", "challan", "voucher", "payment", "bank", "due date", "surcharge", "scholarship", "discount")) {
            return new AssistantQueryResponse(
                    "Tuition Fees & Billing Information:\n\n" +
                    "• Fee Issuance: Monthly tuition challans are generated on the 1st of every calendar month.\n" +
                    "• Due Date: Due on or before the 10th of each month without surcharge.\n" +
                    "• Late Payment: A late fee surcharge of PKR 200 applies after the 15th of the billing month.\n" +
                    "• Payment Methods: Officially payable at designated partner bank branches across Hyderabad or online through 1Link 1Bill.\n" +
                    "• Concessions: 15% sibling concession applies automatically for families with 2+ children enrolled.\n" +
                    "• Download Vouchers: Parents can download and print official fee vouchers directly from their Parent Portal under the Fees module.",
                    "FINANCE_AND_FEES",
                    0.97,
                    "Institutional Finance & Fee Policy Manual",
                    List.of("How can I download my child's fee voucher?", "What should I do if my payment is not reflecting?"),
                    List.of(new SuggestedActionDto("Go to Fee Vouchers", "/fees"), new SuggestedActionDto("Report Fee Query", "/support"))
            );
        }

        if (containsAny(q, "report card", "result", "exam", "marks", "grade", "percentage", "assessment", "bise")) {
            return new AssistantQueryResponse(
                    "Examinations & Academic Results Guide:\n\n" +
                    "• Grading System: Standard BISE Hyderabad grading system (A+ 80%+, A 70-79%, B 60-69%, C 50-59%, D 40-49%, F Below 40%).\n" +
                    "• Official Report Cards: Printable A3/A4 comprehensive report cards featuring subject-wise theory and practical breakdowns, class teacher comments (Sir Ahmed Khan), and Principal's signature (Huma Arif) are accessible in the Examinations tab.\n" +
                    "• Board Classes: Class 9 and 10 follow the prescribed syllabus of the Board of Intermediate and Secondary Education (BISE) Hyderabad.",
                    "ACADEMICS_AND_EXAMS",
                    0.95,
                    "Academic Assessment & Examination Bylaws",
                    List.of("Where can I print my official report card?", "Who is the Academic Principal?"),
                    List.of(new SuggestedActionDto("View Examination Results", "/examinations"))
            );
        }

        if (containsAny(q, "id card", "student id", "card", "qr code", "reissue", "replacement")) {
            return new AssistantQueryResponse(
                    "Digital & Printable Student ID Cards:\n\n" +
                    "• Every enrolled student receives an official Bluebells Student ID card equipped with a secure verification QR code.\n" +
                    "• Safe QR Design: Our QR codes contain tamper-proof cryptographic tokens and do not expose sensitive personal phone numbers or home addresses to public scanners.\n" +
                    "• Lost or Damaged Cards: Parents and students can request card reissuance through the administrative office.",
                    "STUDENT_ID_CARDS",
                    0.96,
                    "Student ID Card Specifications & Guidelines",
                    List.of("How do I view my student ID card?", "How can I verify a student card?"),
                    List.of(new SuggestedActionDto("Digital ID Cards", "/id-cards"))
            );
        }

        if (containsAny(q, "certificate", "merit", "character", "completion", "sports award", "distinction")) {
            return new AssistantQueryResponse(
                    "Official Digital Certificates & Honors:\n\n" +
                    "• Bluebells issues authenticated digital certificates for Academic Excellence (Merit), Annual Sports Championship, Co-Curricular Competitions, Good Conduct, and Curricular Completion.\n" +
                    "• Online Verification: Every certificate contains a unique verification identifier (e.g., VRF-XXXX-BSS) allowing third parties and parents to authenticate its validity online.\n" +
                    "• Issuing Authorities: Signed by Academic Principal Huma Arif and Vice Principal Hana Khan.",
                    "CERTIFICATES",
                    0.95,
                    "Student Honors & Certificate Management Rules",
                    List.of("Where can I download my certificates?", "How do I verify a certificate code?"),
                    List.of(new SuggestedActionDto("View Certificates", "/certificates"))
            );
        }

        if (containsAny(q, "complaint", "support", "ticket", "issue", "help", "teacher concern", "transport")) {
            return new AssistantQueryResponse(
                    "Support & Complaint Centre:\n\n" +
                    "• You can open formal support tickets across seven departments: Academics, Teachers, Fee Queries, Transport, Administration, Technical Support, and Suggestions.\n" +
                    "• Tracking: Every ticket receives a unique tracking number (e.g. TKT-2026-0001) with live status updates (Open, In Progress, Awaiting Response, Resolved, Closed).\n" +
                    "• Privacy Guarantee: Parents only have access to their own tickets and communication threads.",
                    "SUPPORT_CENTRE",
                    0.97,
                    "Parent & Community Grievance Redressal Policy",
                    List.of("How do I submit a new support ticket?", "How long does ticket resolution take?"),
                    List.of(new SuggestedActionDto("Go to Support Centre", "/support"))
            );
        }

        if (containsAny(q, "lms", "course", "assignment", "homework", "video", "notes", "quiz")) {
            return new AssistantQueryResponse(
                    "Learning Management System (LMS) Features:\n\n" +
                    "• Subject Modules: Coursework is organized into interactive learning modules with video lessons, downloadable PDF lecture notes, and formula sheets.\n" +
                    "• Assignment Submission: Students can upload assignments directly in PDF, image, or document formats before the indicated deadline.\n" +
                    "• Video Playback: All integrated educational videos (including Khan Academy tutorials) support full resolution controls and interactive transcripts.",
                    "LMS_LEARNING",
                    0.94,
                    "Digital Pedagogy & LMS Student Guide",
                    List.of("Where are my pending assignments?", "How do I view class notes?"),
                    List.of(new SuggestedActionDto("Open LMS Portal", "/lms"))
            );
        }

        if (containsAny(q, "principal", "vice principal", "leadership", "administration", "huma arif", "hana khan", "head")) {
            return new AssistantQueryResponse(
                    "Institutional Leadership at Bluebells Schooling System:\n\n" +
                    "• Academic Principal: Mrs. Huma Arif — Directs academic curriculum, pedagogical quality, and institutional standards.\n" +
                    "• Vice Principal: Ms. Hana Khan — Manages school operations, student welfare, discipline, and campus coordination.\n" +
                    "• Class Teacher (Grade 10): Sir Ahmed Khan — Senior Mathematics & Science Instructor.\n" +
                    "• Official Inquiries: Contact the leadership desk through our Support Centre or reception at +92 316 9681932.",
                    "LEADERSHIP",
                    0.98,
                    "School Administrative Hierarchy Handbook",
                    List.of("How do I book an appointment with the Principal?", "Where can I find teacher contact details?"),
                    List.of(new SuggestedActionDto("View Staff Directory", "/staff"), new SuggestedActionDto("Contact Support", "/support"))
            );
        }

        // Default Fallback Response
        return new AssistantQueryResponse(
                "I am here to assist with information regarding Bluebells Schooling System! You can ask about:\n\n" +
                "• School timings, morning assembly, and Friday hours\n" +
                "• Admissions criteria, required documents, and age limits\n" +
                "• Monthly fee billing, bank challans, and due dates\n" +
                "• Examination results, report cards, and grading scales\n" +
                "• Student ID cards, digital certificates, and verification\n" +
                "• LMS courses, assignments, and study materials\n" +
                "• Opening tickets in the Support & Complaint Centre\n\n" +
                "How can I help you today?",
                "GENERAL_INFORMATION",
                0.85,
                "Bluebells School Information Directory",
                List.of("What are the school timings?", "How do I check fee status?", "Where is the campus located?"),
                List.of(new SuggestedActionDto("Dashboard Overview", "/"), new SuggestedActionDto("Support Centre", "/support"))
        );
    }

    public List<AssistantFaqDto> getFaqCategories() {
        return List.of(
                new AssistantFaqDto("TIMINGS", "School Timings & Schedules",
                        List.of("What are the school timings on regular days?", "What is the Friday schedule?", "When does morning assembly start?")),
                new AssistantFaqDto("ADMISSIONS", "Admissions & Enrollment",
                        List.of("What documents are required for admission?", "What is the age limit for Kindergarten?", "How do I apply for admission?")),
                new AssistantFaqDto("FEES", "Fees & Billing Queries",
                        List.of("What is the fee payment due date?", "Where can I download the fee challan?", "Is there a sibling discount?")),
                new AssistantFaqDto("EXAMS", "Examinations & Report Cards",
                        List.of("How can I download the official report card?", "What is the grading scale?", "Who signs the report card?")),
                new AssistantFaqDto("ID_CARDS", "Student ID Cards",
                        List.of("How do I view my student ID card?", "Is the ID card QR code secure?", "How can I get a replacement ID card?")),
                new AssistantFaqDto("SUPPORT", "Help & Grievance Centre",
                        List.of("How do I submit an inquiry about transport?", "How can I report a fee discrepancy?", "Who sees my support tickets?"))
        );
    }

    private boolean isDataTamperingIntent(String q) {
        return (q.contains("change") || q.contains("modify") || q.contains("edit") || q.contains("update") || q.contains("delete"))
                && (q.contains("marks") || q.contains("result") || q.contains("attendance") || q.contains("fee") || q.contains("grade") || q.contains("balance"));
    }

    private boolean isPrivacyViolationIntent(String q) {
        return (q.contains("phone number of") || q.contains("cnic of") || q.contains("address of") || q.contains("private data") || q.contains("contact of student"))
                && !q.contains("school") && !q.contains("office") && !q.contains("reception");
    }

    private boolean containsAny(String input, String... keywords) {
        for (String k : keywords) {
            if (input.contains(k)) return true;
        }
        return false;
    }
}
