package com.example.hotelapi.controller;

import com.example.hotelapi.model.Hospede;
import com.example.hotelapi.repository.HospedeRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospedes")
public class HospedeController {
    private final HospedeRepository repository;

    public HospedeController(HospedeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Hospede>> all() {
        List<EntityModel<Hospede>> hospedes = repository.findAll().stream()
            .map(hospede -> EntityModel.of(hospede,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).one(hospede.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).all()).withRel("hospedes")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(hospedes,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Hospede> one(@PathVariable Long id) {
        Hospede hospede = repository.findById(id).orElseThrow(() -> new RuntimeException("Hospede não encontrado"));
        return EntityModel.of(hospede,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).all()).withRel("hospedes"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Hospede>> create(@RequestBody Hospede hospede) {
        Hospede saved = repository.save(hospede);
        EntityModel<Hospede> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Hospede>> update(@PathVariable Long id, @RequestBody Hospede hospede) {
        Hospede updated = repository.findById(id)
            .map(h -> {
                h.setNome(hospede.getNome());
                h.setEmail(hospede.getEmail());
                h.setTelefone(hospede.getTelefone());
                h.setDocumentoIdentificacao(hospede.getDocumentoIdentificacao());
                h.setNacionalidade(hospede.getNacionalidade());
                h.setDataNascimento(hospede.getDataNascimento());
                return repository.save(h);
            })
            .orElseThrow(() -> new RuntimeException("Hospede não encontrado"));
        EntityModel<Hospede> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HospedeController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
