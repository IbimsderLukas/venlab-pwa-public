package at.ac.tgm.backend;

import at.ac.tgm.backend.model.LogEntry;
import at.ac.tgm.backend.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void setUp() {
        logRepository.deleteAll();
    }

    @Test
    void getAllLogs_ReturnsEmptyList_WhenNoData() throws Exception {
        mockMvc.perform(get("/api/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getAllLogs_ReturnsData_WhenDataExists() throws Exception {
        LogEntry log = new LogEntry();
        log.setLevel("INFO");
        log.setInfo("Test log entry");
        log.setDateCreated(LocalDateTime.now());
        logRepository.save(log);

        mockMvc.perform(get("/api/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getAllLogs_ReturnsMultipleLogs_WhenMultipleExist() throws Exception {
        for (int i = 0; i < 5; i++) {
            LogEntry log = new LogEntry();
            log.setLevel("INFO");
            log.setInfo("Log entry " + i);
            log.setDateCreated(LocalDateTime.now());
            logRepository.save(log);
        }

        mockMvc.perform(get("/api/log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)));
    }
}
