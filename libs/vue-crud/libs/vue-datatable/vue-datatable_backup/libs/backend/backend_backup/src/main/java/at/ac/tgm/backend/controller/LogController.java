package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.LogEntry;
import at.ac.tgm.backend.repository.LogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {

    private final LogRepository repo;

    public LogController(LogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<LogEntry> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> getOne(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}