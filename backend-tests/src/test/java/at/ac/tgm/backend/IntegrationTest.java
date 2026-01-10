package at.ac.tgm.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiEndpoints_AreAccessible() throws Exception {
        mockMvc.perform(get("/api/analysis"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/sample"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/box"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/log"))
                .andExpect(status().isOk());
    }

    @Test
    void invalidEndpoint_Returns404() throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiReturnsJson_ContentType() throws Exception {
        mockMvc.perform(get("/api/analysis"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }
}
