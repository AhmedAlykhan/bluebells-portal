package com.bluebells.timetable;

import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import com.bluebells.timetable.dto.CreateTimetableSlotRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TimetableClashDetectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private User teacher1;
    private User teacher2;
    private Section sectionA;
    private Section sectionB;
    private Subject mathSubject;
    private Subject englishSubject;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));

        teacher1 = userRepository.findByUsername("teacher_math_test")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("teacher_math_test");
                    u.setEmail("math.teacher@bluebells.edu.pk");
                    u.setFullName("Sir Asif Math Faculty");
                    u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(teacherRole));
                    return userRepository.save(u);
                });

        teacher2 = userRepository.findByUsername("teacher_eng_test")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("teacher_eng_test");
                    u.setEmail("eng.teacher@bluebells.edu.pk");
                    u.setFullName("Madam Nazia English Faculty");
                    u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(teacherRole));
                    return userRepository.save(u);
                });

        List<Section> sections = sectionRepository.findAll();
        sectionA = sections.get(0);
        sectionB = sections.size() > 1 ? sections.get(1) : sectionA;

        List<Subject> subjects = subjectRepository.findAll();
        mathSubject = subjects.get(0);
        englishSubject = subjects.size() > 1 ? subjects.get(1) : mathSubject;
    }

    @Test
    @DisplayName("Timetable clash prevention: Detects teacher, section, and room overlapping conflicts")
    void testTimetableClashDetection() throws Exception {
        // 1. Create initial valid slot: Teacher 1, Section A, Math, Monday 08:30 - 09:15, Room 201
        CreateTimetableSlotRequest baseReq = new CreateTimetableSlotRequest();
        baseReq.setSectionId(sectionA.getId());
        baseReq.setSubjectId(mathSubject.getId());
        baseReq.setTeacherId(teacher1.getId());
        baseReq.setDayOfWeek("MONDAY");
        baseReq.setStartTime(LocalTime.of(8, 30));
        baseReq.setEndTime(LocalTime.of(9, 15));
        baseReq.setRoomNumber("Room 201");

        mockMvc.perform(post("/api/v1/timetable/slots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roomNumber", is("Room 201")));

        // 2. Teacher Conflict: Schedule Teacher 1 in Section B on Monday 09:00 - 09:45 (overlaps 09:00-09:15)
        CreateTimetableSlotRequest teacherClashReq = new CreateTimetableSlotRequest();
        teacherClashReq.setSectionId(sectionB.getId());
        teacherClashReq.setSubjectId(englishSubject.getId());
        teacherClashReq.setTeacherId(teacher1.getId()); // Same teacher
        teacherClashReq.setDayOfWeek("MONDAY");
        teacherClashReq.setStartTime(LocalTime.of(9, 0));
        teacherClashReq.setEndTime(LocalTime.of(9, 45));
        teacherClashReq.setRoomNumber("Room 202");

        mockMvc.perform(post("/api/v1/timetable/slots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherClashReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", containsString("Teacher schedule conflict")));

        // 3. Section Conflict: Schedule Section A with Teacher 2 on Monday 08:45 - 09:30 (overlaps 08:45-09:15)
        CreateTimetableSlotRequest sectionClashReq = new CreateTimetableSlotRequest();
        sectionClashReq.setSectionId(sectionA.getId()); // Same section
        sectionClashReq.setSubjectId(englishSubject.getId());
        sectionClashReq.setTeacherId(teacher2.getId()); // Different teacher
        sectionClashReq.setDayOfWeek("MONDAY");
        sectionClashReq.setStartTime(LocalTime.of(8, 45));
        sectionClashReq.setEndTime(LocalTime.of(9, 30));
        sectionClashReq.setRoomNumber("Room 203");

        mockMvc.perform(post("/api/v1/timetable/slots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionClashReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", containsString("Section schedule conflict")));

        // 4. Room Conflict: Schedule Section B with Teacher 2 in Room 201 on Monday 08:45 - 09:30 (overlaps 08:45-09:15)
        CreateTimetableSlotRequest roomClashReq = new CreateTimetableSlotRequest();
        roomClashReq.setSectionId(sectionB.getId());
        roomClashReq.setSubjectId(englishSubject.getId());
        roomClashReq.setTeacherId(teacher2.getId());
        roomClashReq.setDayOfWeek("MONDAY");
        roomClashReq.setStartTime(LocalTime.of(8, 45));
        roomClashReq.setEndTime(LocalTime.of(9, 30));
        roomClashReq.setRoomNumber("Room 201"); // Same room

        mockMvc.perform(post("/api/v1/timetable/slots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomClashReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", containsString("Room conflict")));

        // 5. Non-overlapping slot: Monday 09:15 - 10:00 (back-to-back in same room) should SUCCEED
        CreateTimetableSlotRequest validNextSlot = new CreateTimetableSlotRequest();
        validNextSlot.setSectionId(sectionB.getId());
        validNextSlot.setSubjectId(englishSubject.getId());
        validNextSlot.setTeacherId(teacher2.getId());
        validNextSlot.setDayOfWeek("MONDAY");
        validNextSlot.setStartTime(LocalTime.of(9, 15));
        validNextSlot.setEndTime(LocalTime.of(10, 0));
        validNextSlot.setRoomNumber("Room 201");

        mockMvc.perform(post("/api/v1/timetable/slots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNextSlot)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
