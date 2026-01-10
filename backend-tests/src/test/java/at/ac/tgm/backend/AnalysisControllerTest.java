package at.ac.tgm.backend;

import at.ac.tgm.backend.controller.AnalysisController;
import at.ac.tgm.backend.model.Analysis;
import at.ac.tgm.backend.repository.AnalysisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalysisRepository analysisRepository;

    @BeforeEach
    void setUp() {
        analysisRepository.deleteAll();
    }

    @Test
    void getAllAnalysis_ReturnsEmptyList_WhenNoData() throws Exception {
        mockMvc.perform(get("/api/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getAllAnalysis_ReturnsData_WhenDataExists() throws Exception {
        Analysis analysis = new Analysis();
        analysis.setPol(50.0);
        analysisRepository.save(analysis);

        mockMvc.perform(get("/api/analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getAnalysisById_ReturnsNotFound_WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/analysis/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAnalysisById_ReturnsAnalysis_WhenExists() throws Exception {
        Analysis analysis = new Analysis();
        analysis.setPol(75.5);
        Analysis saved = analysisRepository.save(analysis);

        mockMvc.perform(get("/api/analysis/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pol").value(75.5));
    }
}
