package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Analysis;
import at.ac.tgm.backend.service.AnalysisService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService service;

    public AnalysisController(AnalysisService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Analysis> getAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Analysis getOne(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public Analysis create(@RequestBody Analysis a) { return service.save(a); }

    @PutMapping("/{id}")
    public Analysis update(@PathVariable Long id, @RequestBody Analysis a) {
        a.setId(id);
        return service.save(a);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
