package com.example.hotelapi.controller;

import com.example.hotelapi.model.Funcionario;
import com.example.hotelapi.repository.FuncionarioRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
    private final FuncionarioRepository repository;

    public FuncionarioController(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Funcionario>> all() {
        List<EntityModel<Funcionario>> funcionarios = repository.findAll().stream()
            .map(funcionario -> EntityModel.of(funcionario,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).one(funcionario.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).all()).withRel("funcionarios")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(funcionarios,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Funcionario> one(@PathVariable Long id) {
        Funcionario funcionario = repository.findById(id).orElseThrow(() -> new RuntimeException("Funcionario não encontrado"));
        return EntityModel.of(funcionario,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).all()).withRel("funcionarios"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Funcionario>> create(@RequestBody Funcionario funcionario) {
        Funcionario saved = repository.save(funcionario);
        EntityModel<Funcionario> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Funcionario>> update(@PathVariable Long id, @RequestBody Funcionario funcionario) {
        Funcionario updated = repository.findById(id)
            .map(f -> {
                f.setNome(funcionario.getNome());
                f.setCargo(funcionario.getCargo());
                f.setEmail(funcionario.getEmail());
                f.setTelefone(funcionario.getTelefone());
                f.setDataContratacao(funcionario.getDataContratacao());
                return repository.save(f);
            })
            .orElseThrow(() -> new RuntimeException("Funcionario não encontrado"));
        EntityModel<Funcionario> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FuncionarioController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
