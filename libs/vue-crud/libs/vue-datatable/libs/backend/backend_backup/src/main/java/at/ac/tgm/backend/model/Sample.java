package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;

@Entity
@Table(name = "sample", schema = "venlab")
@IdClass(SampleId.class)
@Data

public class Sample {

    @Id
    @Column(name = "s_id")
    private String id;

    @Id
    @Column(name = "s_stamp")
    private LocalDateTime stamp;

    private String name;

    @Column(name = "weight_net")
    private Double weightNet;

    @Column(name = "weight_bru")
    private Double weightBrutto;

    @Column(name = "weight_tar")
    private Double weightTara;

    private Integer quantity;

    private Double distance;

    @Column(name = "date_crumbled")
    private LocalDateTime dateCrumbled;

    @Column(name = "s_flags")
    private String flags;

    private Integer lane;

    private String comment;

    @Column(name = "date_exported")
    private LocalDateTime dateExported;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "s_id", referencedColumnName = "s_id", insertable = false, updatable = false),
            @JoinColumn(name = "s_stamp", referencedColumnName = "s_stamp", insertable = false, updatable = false)
    })
    private SampleBoxPos boxPosView;
}
