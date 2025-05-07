package com.example.hotelapi.controller;

import com.example.hotelapi.model.Reserva;
import com.example.hotelapi.repository.ReservaRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaRepository repository;

    public ReservaController(ReservaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public CollectionModel<EntityModel<Reserva>> all() {
        List<EntityModel<Reserva>> reservas = repository.findAll().stream()
            .map(reserva -> EntityModel.of(reserva,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).one(reserva.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).all()).withRel("reservas")
            ))
            .collect(Collectors.toList());
        return CollectionModel.of(reservas,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Reserva> one(@PathVariable Long id) {
        Reserva reserva = repository.findById(id).orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        return EntityModel.of(reserva,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).one(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).all()).withRel("reservas"));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Reserva>> create(@RequestBody Reserva reserva) {
        Reserva saved = repository.save(reserva);
        EntityModel<Reserva> resource = EntityModel.of(saved,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).one(saved.getId())).withSelfRel());
        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).one(saved.getId())).toUri()).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Reserva>> update(@PathVariable Long id, @RequestBody Reserva reserva) {
        Reserva updated = repository.findById(id)
            .map(r -> {
                r.setDataCheckIn(reserva.getDataCheckIn());
                r.setDataCheckOut(reserva.getDataCheckOut());
                r.setNumeroPessoas(reserva.getNumeroPessoas());
                r.setEstadoReserva(reserva.getEstadoReserva());
                r.setHospede(reserva.getHospede());
                r.setQuarto(reserva.getQuarto());
                r.setServicos(reserva.getServicos());
                return repository.save(r);
            })
            .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        EntityModel<Reserva> resource = EntityModel.of(updated,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReservaController.class).one(updated.getId())).withSelfRel());
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
