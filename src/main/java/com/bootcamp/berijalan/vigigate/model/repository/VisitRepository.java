package com.bootcamp.berijalan.vigigate.model.repository;

import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, UUID> {
    // Mengecek apakah tamu memiliki kunjungan yang belum checkout
    boolean existsByGuestIdAndVisitStatus(UUID guestId, VisitStatus visitStatus);

    // Menghitung jumlah check-in tamu pada hari ini
    long countByGuestIdAndCheckInTimeBetween(UUID guestId, LocalDateTime start, LocalDateTime end);

    // Mengambil semua tamu yang statusnya CHECKED_IN, diurutkan dari yang terbaru
    List<Visit> findByVisitStatusOrderByCheckInTimeDesc(VisitStatus visitStatus);

    // Untuk fitur AI Report
    List<Visit> findByCheckInTimeBetween(LocalDateTime start, LocalDateTime end);

    // Ambil data kunjungan hari ini
    List<Visit> findByCheckInTimeBetweenOrderByCheckInTimeAsc(LocalDateTime start, LocalDateTime end);

}
