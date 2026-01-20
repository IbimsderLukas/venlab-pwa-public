package at.ac.tgm.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "sample_boxpos", schema = "venlab")
@IdClass(SampleId.class)
@Data
public class SampleBoxPos {
    @Id
    @Column(name = "s_id")
    private String id;

    @Id
    @Column(name = "s_stamp")
    private LocalDateTime stamp;

    @Column(name = "boxpos")
    private String boxPos;
}