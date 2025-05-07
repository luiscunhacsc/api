package com.example.hotelapi.repository;

import com.example.hotelapi.model.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {
}
