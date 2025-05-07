package com.example.hotelapi.controller;

import com.example.hotelapi.model.Pagamento;
import com.example.hotelapi.repository.PagamentoRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    private final PagamentoRepository repository;

    public PagamentoController(PagamentoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Pagamento>> all() {
        List<EntityModel<Pagamento>> pagamentos = repository.findAll().stream()
            .map(pagamento -> EntityModel.of(pagamento,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).one(pagamento.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).all()).withRel("pagamentos")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(pagamentos,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Pagamento> one(@PathVariable Long id) {
        Pagamento pagamento = repository.findById(id).orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        return EntityModel.of(pagamento,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).all()).withRel("pagamentos"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Pagamento>> create(@RequestBody Pagamento pagamento) {
        Pagamento saved = repository.save(pagamento);
        EntityModel<Pagamento> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Pagamento>> update(@PathVariable Long id, @RequestBody Pagamento pagamento) {
        Pagamento updated = repository.findById(id)
            .map(p -> {
                p.setDataPagamento(pagamento.getDataPagamento());
                p.setValorTotal(pagamento.getValorTotal());
                p.setMetodoPagamento(pagamento.getMetodoPagamento());
                p.setReferenciaTransacao(pagamento.getReferenciaTransacao());
                p.setReserva(pagamento.getReserva());
                return repository.save(p);
            })
            .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        EntityModel<Pagamento> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PagamentoController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
