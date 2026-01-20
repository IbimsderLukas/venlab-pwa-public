package at.ac.tgm.backend.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class BoxPosId implements Serializable {
    private String boxId;
    private Integer posId;
}
