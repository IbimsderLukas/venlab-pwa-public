package at.ac.tgm.backend.service;

import at.ac.tgm.backend.model.Analysis;
import at.ac.tgm.backend.model.SampleId;
import at.ac.tgm.backend.repository.AnalysisRepository;
import at.ac.tgm.backend.repository.SampleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class AnalysisService {

    private final AnalysisRepository repo;
    private final SampleRepository sampleRepo;

    public AnalysisService(AnalysisRepository repo, SampleRepository sampleRepo) {
        this.repo = repo;
        this.sampleRepo = sampleRepo;
    }

    public Page<Analysis> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Analysis findById(Long id) { return repo.findById(id).orElse(null); }

    public Analysis save(Analysis a) {
        if (a.getSampleId() != null) {
            SampleId checkId = new SampleId();
            checkId.setId(a.getSampleId());
            checkId.setStamp(a.getSampleStamp());

            if (!sampleRepo.existsById(checkId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Es existiert kein Sample mit der ID " + a.getSampleId()
                );
            }
        }
        return repo.save(a);
    }

    public void delete(Long id) { repo.deleteById(id); }
}