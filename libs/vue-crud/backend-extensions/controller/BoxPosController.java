package at.ac.tgm.backend.controller;

import at.ac.tgm.backend.model.BoxPos;
import at.ac.tgm.backend.model.BoxPosId;
import at.ac.tgm.backend.repository.BoxPosRepository;
import at.ac.tgm.backend.repository.BoxRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/boxpos")
public class BoxPosController {

    private final BoxPosRepository repo;
    private final BoxRepository boxRepo;

    public BoxPosController(BoxPosRepository repo, BoxRepository boxRepo) {
        this.repo = repo;
        this.boxRepo = boxRepo;
    }

    @GetMapping
    public List<BoxPos> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{boxId}/{posId}")
    public ResponseEntity<BoxPos> getOne(
            @PathVariable String boxId,
            @PathVariable Integer posId
    ) {
        BoxPosId id = new BoxPosId();
        id.setBoxId(boxId);
        id.setPosId(posId);
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/box/{boxId}")
    public List<BoxPos> getByBox(@PathVariable String boxId) {
        return repo.findByBoxId(boxId);
    }

    @PostMapping
    public BoxPos create(@RequestBody BoxPos boxPos) {
        if (boxPos.getBoxId() == null || boxPos.getPosId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box ID und Position ID sind Pflichtfelder");
        }
        if (!boxRepo.existsById(boxPos.getBoxId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Box mit ID " + boxPos.getBoxId() + " existiert nicht");
        }
        BoxPosId id = new BoxPosId();
        id.setBoxId(boxPos.getBoxId());
        id.setPosId(boxPos.getPosId());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BoxPos mit dieser ID existiert bereits");
        }
        return repo.save(boxPos);
    }

    @PutMapping("/{boxId}/{posId}")
    public BoxPos update(
            @PathVariable String boxId,
            @PathVariable Integer posId,
            @RequestBody BoxPos boxPos
    ) {
        BoxPosId id = new BoxPosId();
        id.setBoxId(boxId);
        id.setPosId(posId);
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BoxPos nicht gefunden");
        }
        boxPos.setBoxId(boxId);
        boxPos.setPosId(posId);
        return repo.save(boxPos);
    }

    @DeleteMapping("/{boxId}/{posId}")
    public ResponseEntity<Void> delete(
            @PathVariable String boxId,
            @PathVariable Integer posId
    ) {
        BoxPosId id = new BoxPosId();
        id.setBoxId(boxId);
        id.setPosId(posId);
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BoxPos nicht gefunden");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
