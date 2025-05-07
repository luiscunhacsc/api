package com.example.hotelapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    private BigDecimal precoPorNoite;

    private String descricao;

    private Boolean ocupado;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Funcionario responsavel;
}
