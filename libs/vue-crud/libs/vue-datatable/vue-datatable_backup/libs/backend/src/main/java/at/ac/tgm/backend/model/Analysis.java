package at.ac.tgm.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;

@Entity
@Table(name = "analysis", schema = "venlab")
@Data
public class Analysis {

    @Id
    @Column(name = "a_id")
    private Long id;

    @Column(name = "s_id")
    private String sampleId;

    @Column(name = "s_stamp")
    private LocalDateTime sampleStamp;

    private Double pol;
    private Double nat;
    private Double kal;
    private Double an;
    private Double glu;
    private Double dry;

    @Column(name = "date_in")
    private LocalDateTime dateIn;

    @Column(name = "date_out")
    private LocalDateTime dateOut;

    @Column(name = "weight_mea")
    private Double weightMeasured;

    @Column(name = "weight_nrm")
    private Double weightNormal;

    @Column(name = "weight_cur")
    private Double weightCurrent;

    @Column(name = "weight_dif")
    private Double weightDifference;

    private Double density;

    @Column(name = "a_flags")
    private String flags;

    private Integer lane;

    private String comment;

    @Column(name = "date_exported")
    private LocalDateTime dateExported;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "s_id", referencedColumnName = "s_id", insertable = false, updatable = false),
            @JoinColumn(name = "s_stamp", referencedColumnName = "s_stamp", insertable = false, updatable = false)
    })
    private Sample associatedSample;

}
