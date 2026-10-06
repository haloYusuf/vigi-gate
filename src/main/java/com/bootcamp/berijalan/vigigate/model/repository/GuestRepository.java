package com.bootcamp.berijalan.vigigate.model.repository;

import com.bootcamp.berijalan.vigigate.model.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
    //Cek tamu apakah sudah pernah daftar
    Optional<Guest> findByNik(String nik);
}