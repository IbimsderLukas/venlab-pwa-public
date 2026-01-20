package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Box;
import at.ac.tgm.backend.repository.BoxRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/box")
public class BoxController {

    private final BoxRepository repo;

    public BoxController(BoxRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Box> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Box> getOne(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}