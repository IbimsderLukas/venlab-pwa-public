package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Box;
import at.ac.tgm.backend.repository.BoxRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping
    public Box create(@RequestBody Box box) {
        if (box.getId() == null || box.getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box ID ist ein Pflichtfeld");
        }
        if (repo.existsById(box.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Box mit dieser ID existiert bereits");
        }
        return repo.save(box);
    }

    @PutMapping("/{id}")
    public Box update(@PathVariable String id, @RequestBody Box box) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Box nicht gefunden");
        }
        box.setId(id);
        return repo.save(box);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Box nicht gefunden");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
