package at.ac.tgm.backend;

import at.ac.tgm.backend.model.Sample;
import at.ac.tgm.backend.model.SampleId;
import at.ac.tgm.backend.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SampleRepository sampleRepository;

    @BeforeEach
    void setUp() {
        sampleRepository.deleteAll();
    }

    @Test
    void getAllSamples_ReturnsEmptyList_WhenNoData() throws Exception {
        mockMvc.perform(get("/api/sample"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getAllSamples_ReturnsData_WhenDataExists() throws Exception {
        Sample sample = new Sample();
        SampleId id = new SampleId();
        id.setId("TEST001");
        id.setStamp(LocalDateTime.now());
        sample.setId(id);
        sample.setName("Test Sample");
        sampleRepository.save(sample);

        mockMvc.perform(get("/api/sample"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getSampleById_ReturnsNotFound_WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/sample/NOTEXIST/2024-01-01T00:00:00"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSampleById_ReturnsSample_WhenExists() throws Exception {
        Sample sample = new Sample();
        SampleId id = new SampleId();
        id.setId("SAMPLE123");
        id.setStamp(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        sample.setId(id);
        sample.setName("Integration Test Sample");
        sample.setWeightNet(150.5);
        sampleRepository.save(sample);

        mockMvc.perform(get("/api/sample/SAMPLE123/2024-01-15T10:30:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Sample"));
    }
}
