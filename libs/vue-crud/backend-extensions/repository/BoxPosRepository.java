package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.BoxPos;
import at.ac.tgm.backend.model.BoxPosId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoxPosRepository extends JpaRepository<BoxPos, BoxPosId> {
    List<BoxPos> findByBoxId(String boxId);
}
