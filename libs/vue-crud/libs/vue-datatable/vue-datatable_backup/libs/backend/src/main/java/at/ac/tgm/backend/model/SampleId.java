package at.ac.tgm.backend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SampleId implements Serializable {
    private String id;
    private LocalDateTime stamp;
}