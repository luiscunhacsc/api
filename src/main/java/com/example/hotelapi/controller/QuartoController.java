package com.example.hotelapi.controller;

import com.example.hotelapi.model.Quarto;
import com.example.hotelapi.repository.QuartoRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {
    private final QuartoRepository repository;

    public QuartoController(QuartoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Quarto>> all() {
        List<EntityModel<Quarto>> quartos = repository.findAll().stream()
            .map(quarto -> EntityModel.of(quarto,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).one(quarto.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).all()).withRel("quartos")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(quartos,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Quarto> one(@PathVariable Long id) {
        Quarto quarto = repository.findById(id).orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        return EntityModel.of(quarto,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).all()).withRel("quartos"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Quarto>> create(@RequestBody Quarto quarto) {
        Quarto saved = repository.save(quarto);
        EntityModel<Quarto> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Quarto>> update(@PathVariable Long id, @RequestBody Quarto quarto) {
        Quarto updated = repository.findById(id)
            .map(q -> {
                q.setNumero(quarto.getNumero());
                q.setTipo(quarto.getTipo());
                q.setCapacidade(quarto.getCapacidade());
                q.setPrecoPorNoite(quarto.getPrecoPorNoite());
                q.setDescricao(quarto.getDescricao());
                q.setOcupado(quarto.getOcupado());
                q.setResponsavel(quarto.getResponsavel());
                return repository.save(q);
            })
            .orElseThrow(() -> new RuntimeException("Quarto não encontrado"));
        EntityModel<Quarto> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(QuartoController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
