package at.ac.tgm.backend;

import at.ac.tgm.backend.controller.*;
import at.ac.tgm.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationContextTest {

    @Autowired
    private AnalysisController analysisController;

    @Autowired
    private SampleController sampleController;

    @Autowired
    private BoxController boxController;

    @Autowired
    private LogController logController;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private SampleRepository sampleRepository;

    @Test
    void contextLoads() {
        assertThat(analysisController).isNotNull();
        assertThat(sampleController).isNotNull();
        assertThat(boxController).isNotNull();
        assertThat(logController).isNotNull();
    }

    @Test
    void repositoriesAreAvailable() {
        assertThat(analysisRepository).isNotNull();
        assertThat(sampleRepository).isNotNull();
    }
}
