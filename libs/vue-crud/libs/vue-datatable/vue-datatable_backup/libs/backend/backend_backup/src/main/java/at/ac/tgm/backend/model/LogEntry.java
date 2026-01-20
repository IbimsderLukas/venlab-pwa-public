package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "log", schema = "venlab")
@Data
public class LogEntry {

    @Id
    @Column(name = "log_id")
    private Long id;

    @Column(name="date_created")
    private LocalDateTime created;

    private String level;

    private String info;

    @Column(name="s_id")
    private String sampleId;

    @Column(name="s_stamp")
    private LocalDateTime sampleStamp;

    @Column(name="a_id")
    private Long analysisId;

    @Column(name="date_exported")
    private LocalDateTime dateExported;

    // getters + setters
}
