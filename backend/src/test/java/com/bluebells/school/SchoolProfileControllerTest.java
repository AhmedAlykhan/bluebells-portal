package com.bluebells.school;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SchoolProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/school/profile returns 200 with verified Bluebells institutional profile")
    void shouldReturnSchoolProfile() throws Exception {
        mockMvc.perform(get("/api/v1/school/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", containsString("Bluebells Schooling System")))
                .andExpect(jsonPath("$.data.currency", is("PKR")))
                .andExpect(jsonPath("$.data.timezone", is("Asia/Karachi")))
                .andExpect(jsonPath("$.data.email", is("Bluebells.schoolingsystem@gmail.com")));
    }
}
