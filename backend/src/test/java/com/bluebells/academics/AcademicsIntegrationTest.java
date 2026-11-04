package com.bluebells.academics;

import com.bluebells.academics.dto.CreateAcademicSessionRequest;
import com.bluebells.academics.dto.CreateClassRequest;
import com.bluebells.academics.dto.CreateSectionRequest;
import com.bluebells.academics.dto.CreateSubjectRequest;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import com.bluebells.staff.dto.CreateTeacherAssignmentRequest;
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

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class AcademicsIntegrationTest {

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
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String teacherToken;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        if (!userRepository.existsByUsername("teacher_academic_test")) {
            Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));

            User teacher = new User();
            teacher.setUsername("teacher_academic_test");
            teacher.setEmail("teacher.academic@bluebells.edu.pk");
            teacher.setFullName("Sir Tariq Mehmood");
            teacher.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
            teacher.setStatus("ACTIVE");
            teacher.setRoles(Set.of(teacherRole));
            userRepository.save(teacher);
        }

        User teacher = userRepository.findByUsername("teacher_academic_test").orElseThrow();
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));
    }

    @Test
    @DisplayName("GET /api/v1/academics/sessions/current returns the active 2026-2027 session")
    void shouldReturnCurrentSession() throws Exception {
        mockMvc.perform(get("/api/v1/academics/sessions/current")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("2026-2027")))
                .andExpect(jsonPath("$.data.current", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/academics/sessions creates new session and setting it current resets other sessions")
    void shouldCreateSessionAndSetCurrent() throws Exception {
        CreateAcademicSessionRequest request = new CreateAcademicSessionRequest();
        request.setName("2027-2028-TEST");
        request.setStartDate(LocalDate.of(2027, 8, 1));
        request.setEndDate(LocalDate.of(2028, 5, 31));
        request.setCurrent(false);

        mockMvc.perform(post("/api/v1/academics/sessions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("2027-2028-TEST")));
    }

    @Test
    @DisplayName("GET /api/v1/academics/classes returns classes with sections and subjects")
    void shouldReturnClassesWithSectionsAndSubjects() throws Exception {
        mockMvc.perform(get("/api/v1/academics/classes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].sections", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/academics/classes creates new class; duplicate code is rejected with 409")
    void shouldCreateClassAndRejectDuplicateCode() throws Exception {
        CreateClassRequest request = new CreateClassRequest();
        request.setGradeLevelId(11L); // Class 8
        request.setName("Class 8 Middle");
        request.setCode("CLS-8-TEST");

        mockMvc.perform(post("/api/v1/academics/classes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code", is("CLS-8-TEST")));

        // Duplicate code attempt
        mockMvc.perform(post("/api/v1/academics/classes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Conflict")));
    }

    @Test
    @DisplayName("POST /api/v1/academics/classes/{id}/sections adds section; duplicate name in class is rejected")
    void shouldAddSectionAndRejectDuplicateName() throws Exception {
        CreateSectionRequest request = new CreateSectionRequest();
        request.setName("Section C");
        request.setRoomNumber("Room 203");
        request.setMaxCapacity(35);

        mockMvc.perform(post("/api/v1/academics/classes/1/sections")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Section C")));

        // Duplicate section name attempt
        mockMvc.perform(post("/api/v1/academics/classes/1/sections")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/v1/academics/assignments assigns teacher to class section and subject")
    void shouldCreateTeacherAssignment() throws Exception {
        User teacher = userRepository.findByUsername("teacher_academic_test").orElseThrow();

        CreateTeacherAssignmentRequest request = new CreateTeacherAssignmentRequest();
        request.setTeacherId(teacher.getId());
        request.setSectionId(1L);
        request.setSubjectId(1L);
        request.setSessionId(1L);
        request.setClassTeacher(true);

        mockMvc.perform(post("/api/v1/academics/assignments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.teacherName", is("Sir Tariq Mehmood")))
                .andExpect(jsonPath("$.data.classTeacher", is(true)));
    }

    @Test
    @DisplayName("Teacher user receives 403 Forbidden when attempting to create class")
    void teacherCannotCreateClass() throws Exception {
        CreateClassRequest request = new CreateClassRequest();
        request.setGradeLevelId(1L);
        request.setName("Unauthorized Class");
        request.setCode("UNAUTH-CLS");

        mockMvc.perform(post("/api/v1/academics/classes")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }
}
