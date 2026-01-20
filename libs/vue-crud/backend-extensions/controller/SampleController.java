package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Sample;
import at.ac.tgm.backend.model.SampleId;
import at.ac.tgm.backend.repository.SampleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    private final SampleRepository repo;

    public SampleController(SampleRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<Sample> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return repo.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{sId}/{sStamp}")
    public ResponseEntity<Sample> getOne(
            @PathVariable String sId,
            @PathVariable String sStamp
    ) {
        SampleId id = new SampleId();
        id.setId(sId);
        id.setStamp(LocalDateTime.parse(sStamp));
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Sample create(@RequestBody Sample sample) {
        if (sample.getId() == null || sample.getStamp() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sample ID und Timestamp sind Pflichtfelder");
        }
        SampleId id = new SampleId();
        id.setId(sample.getId());
        id.setStamp(sample.getStamp());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sample mit dieser ID und Timestamp existiert bereits");
        }
        return repo.save(sample);
    }

    @PutMapping("/{sId}/{sStamp}")
    public Sample update(
            @PathVariable String sId,
            @PathVariable String sStamp,
            @RequestBody Sample sample
    ) {
        SampleId id = new SampleId();
        id.setId(sId);
        id.setStamp(LocalDateTime.parse(sStamp));
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample nicht gefunden");
        }
        sample.setId(sId);
        sample.setStamp(LocalDateTime.parse(sStamp));
        return repo.save(sample);
    }

    @DeleteMapping("/{sId}/{sStamp}")
    public ResponseEntity<Void> delete(
            @PathVariable String sId,
            @PathVariable String sStamp
    ) {
        SampleId id = new SampleId();
        id.setId(sId);
        id.setStamp(LocalDateTime.parse(sStamp));
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sample nicht gefunden");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
