package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.Sample;
import at.ac.tgm.backend.repository.SampleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
}