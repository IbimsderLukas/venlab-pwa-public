package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.BoxPos;
import at.ac.tgm.backend.repository.BoxPosRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boxpos")
public class BoxPosController {

    private final BoxPosRepository repo;

    public BoxPosController(BoxPosRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<BoxPos> getAll() {
        return repo.findAll();
    }
}