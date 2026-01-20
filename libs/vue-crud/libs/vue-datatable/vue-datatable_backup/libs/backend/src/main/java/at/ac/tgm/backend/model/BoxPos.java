package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "boxpos", schema = "venlab")
@IdClass(BoxPosId.class)
@Data
public class BoxPos {

    @Id
    @Column(name="b_id")
    private String boxId;

    @Id
    @Column(name="bpos_id")
    private Integer posId;

    @Column(name="s_id")
    private String sampleId;

    @Column(name="s_stamp")
    private LocalDateTime sampleStamp;

    @Column(name="date_exported")
    private LocalDateTime dateExported;

    // getters + setters
}
