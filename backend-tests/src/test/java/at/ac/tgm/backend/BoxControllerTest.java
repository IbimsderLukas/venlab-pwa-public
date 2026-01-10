package at.ac.tgm.backend;

import at.ac.tgm.backend.model.Box;
import at.ac.tgm.backend.repository.BoxRepository;
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
public class BoxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoxRepository boxRepository;

    @BeforeEach
    void setUp() {
        boxRepository.deleteAll();
    }

    @Test
    void getAllBoxes_ReturnsEmptyList_WhenNoData() throws Exception {
        mockMvc.perform(get("/api/box"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getAllBoxes_ReturnsData_WhenDataExists() throws Exception {
        Box box = new Box();
        box.setId("B001");
        box.setName("Test Box");
        box.setNumMax(40);
        boxRepository.save(box);

        mockMvc.perform(get("/api/box"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getBoxById_ReturnsNotFound_WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/box/XXXX"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBoxById_ReturnsBox_WhenExists() throws Exception {
        Box box = new Box();
        box.setId("BOX1");
        box.setName("Storage Box");
        box.setNumMax(50);
        box.setType(1);
        boxRepository.save(box);

        mockMvc.perform(get("/api/box/BOX1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Storage Box"))
                .andExpect(jsonPath("$.numMax").value(50));
    }
}
