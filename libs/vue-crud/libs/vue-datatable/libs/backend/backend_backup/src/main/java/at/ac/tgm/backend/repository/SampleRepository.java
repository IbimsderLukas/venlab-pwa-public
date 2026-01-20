package at.ac.tgm.backend.repository;

import at.ac.tgm.backend.model.Sample;
import at.ac.tgm.backend.model.SampleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, SampleId> { }
