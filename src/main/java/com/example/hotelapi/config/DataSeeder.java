package com.example.hotelapi.config;

import com.example.hotelapi.model.*;
import com.example.hotelapi.repository.*;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initData(HospedeRepository hospedeRepo,
                               QuartoRepository quartoRepo,
                               FuncionarioRepository funcionarioRepo,
                               ServicoRepository servicoRepo,
                               ReservaRepository reservaRepo,
                               PagamentoRepository pagamentoRepo) {
        return args -> {
            // Helper para selecionar N serviços aleatórios
            java.util.function.BiFunction<List<Servico>, Faker, Set<Servico>> randomServicos = (list, f) -> {
                int n = f.number().numberBetween(1, list.size() + 1);
                List<Servico> copy = new ArrayList<>(list);
                Collections.shuffle(copy, new Random());
                return new HashSet<>(copy.subList(0, n));
            };

            Faker faker = new Faker(new Locale("pt-PT"));

            // Funcionários
            List<Funcionario> funcionarios = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                funcionarios.add(funcionarioRepo.save(Funcionario.builder()
                        .nome(faker.name().fullName())
                        .cargo(faker.options().option("Recepcionista", "Limpeza", "Gerente"))
                        .email(faker.internet().emailAddress())
                        .telefone(faker.phoneNumber().cellPhone())
                        .dataContratacao(LocalDate.now().minusDays(faker.number().numberBetween(30, 1000)))
                        .build()));
            }

            // Quartos
            List<Quarto> quartos = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                quartos.add(quartoRepo.save(Quarto.builder()
                        .numero(i)
                        .tipo(faker.options().option("Single", "Double", "Suite"))
                        .capacidade(faker.number().numberBetween(1, 4))
                        .precoPorNoite(BigDecimal.valueOf(faker.number().randomDouble(2, 30, 150)))
                        .descricao(faker.lorem().sentence())
                        .ocupado(faker.bool().bool())
                        .responsavel(faker.options().nextElement(funcionarios))
                        .build()));
            }

            // Hóspedes
            List<Hospede> hospedes = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                hospedes.add(hospedeRepo.save(Hospede.builder()
                        .nome(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .telefone(faker.phoneNumber().cellPhone())
                        .documentoIdentificacao(faker.idNumber().valid())
                        .nacionalidade(faker.country().name())
                        .dataNascimento(LocalDate.now().minusYears(faker.number().numberBetween(18, 70)))
                        .build()));
            }

            // Serviços
            List<Servico> servicos = new ArrayList<>();
            for (String nome : Arrays.asList("Pequeno-almoço", "Lavandaria", "Spa", "Wi-Fi")) {
                servicos.add(servicoRepo.save(Servico.builder()
                        .nome(nome)
                        .descricao(faker.lorem().sentence())
                        .preco(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 50)))
                        .build()));
            }

            // Reservas
            List<Reserva> reservas = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                LocalDate checkIn = LocalDate.now().plusDays(faker.number().numberBetween(-10, 20));
                LocalDate checkOut = checkIn.plusDays(faker.number().numberBetween(1, 10));
                Reserva reserva = Reserva.builder()
                        .dataCheckIn(checkIn)
                        .dataCheckOut(checkOut)
                        .numeroPessoas(faker.number().numberBetween(1, 4))
                        .estadoReserva(faker.options().option(ReservationStatus.values()))
                        .hospede(faker.options().nextElement(hospedes))
                        .quarto(faker.options().nextElement(quartos))
                        .servicos(randomServicos.apply(servicos, faker))
                        .build();
                reservas.add(reservaRepo.save(reserva));
            }

            // Pagamentos
            for (Reserva reserva : reservas) {
                if (faker.bool().bool()) {
                    pagamentoRepo.save(Pagamento.builder()
                            .dataPagamento(Instant.now().minusSeconds(faker.number().numberBetween(0, 100000)))
                            .valorTotal(BigDecimal.valueOf(faker.number().randomDouble(2, 50, 1000)))
                            .metodoPagamento(faker.options().option(PaymentMethod.values()))
                            .referenciaTransacao(faker.idNumber().valid())
                            .reserva(reserva)
                            .build());
                }
            }
        };
    }
}
