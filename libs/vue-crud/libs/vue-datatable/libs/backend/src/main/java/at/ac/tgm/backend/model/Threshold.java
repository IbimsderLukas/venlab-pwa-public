package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "threshold", schema = "venlab")
@Data
public class Threshold {

    @Id
    @Column(name="th_id")
    private String id;

    @Column(name="value_min")
    private Double min;

    @Column(name="value_max")
    private Double max;

    @Column(name="date_changed")
    private java.time.LocalDateTime dateChanged;

    // getters + setters
}
