package com.example.hotelapi.controller;

import com.example.hotelapi.model.Servico;
import com.example.hotelapi.repository.ServicoRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {
    private final ServicoRepository repository;

    public ServicoController(ServicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Servico>> all() {
        List<EntityModel<Servico>> servicos = repository.findAll().stream()
            .map(servico -> EntityModel.of(servico,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).one(servico.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).all()).withRel("servicos")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(servicos,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Servico> one(@PathVariable Long id) {
        Servico servico = repository.findById(id).orElseThrow(() -> new RuntimeException("Servico não encontrado"));
        return EntityModel.of(servico,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).all()).withRel("servicos"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Servico>> create(@RequestBody Servico servico) {
        Servico saved = repository.save(servico);
        EntityModel<Servico> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Servico>> update(@PathVariable Long id, @RequestBody Servico servico) {
        Servico updated = repository.findById(id)
            .map(s -> {
                s.setNome(servico.getNome());
                s.setDescricao(servico.getDescricao());
                s.setPreco(servico.getPreco());
                return repository.save(s);
            })
            .orElseThrow(() -> new RuntimeException("Servico não encontrado"));
        EntityModel<Servico> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
