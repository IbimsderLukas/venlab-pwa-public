package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.BoxPos;
import at.ac.tgm.backend.model.BoxPosId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxPosRepository extends JpaRepository<BoxPos, BoxPosId> { }
