package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Threshold;
import at.ac.tgm.backend.repository.ThresholdRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/threshold")
public class ThresholdController {

    private final ThresholdRepository repo;

    public ThresholdController(ThresholdRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Threshold> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Threshold> getOne(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}