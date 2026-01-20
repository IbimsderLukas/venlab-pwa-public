package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
