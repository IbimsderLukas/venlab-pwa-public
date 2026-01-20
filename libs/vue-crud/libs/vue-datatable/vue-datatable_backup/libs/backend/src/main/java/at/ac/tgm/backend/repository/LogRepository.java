package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry, Long> { }
