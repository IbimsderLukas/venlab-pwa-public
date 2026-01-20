package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "box", schema = "venlab")
@Data
public class Box {

    @Id
    @Column(name = "b_id")
    private String id;

    private String name;

    @Column(name = "num_max")
    private Integer maxNumber;

    private Integer type;

    private String comment;

    @Column(name = "date_exported")
    private java.time.LocalDateTime dateExported;

    // getters + setters
}
