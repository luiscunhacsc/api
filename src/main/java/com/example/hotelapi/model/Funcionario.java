package com.example.hotelapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cargo;

    private String email;
    private String telefone;
    private LocalDate dataContratacao;

    @OneToMany(mappedBy = "responsavel")
    private List<Quarto> quartos;
}
