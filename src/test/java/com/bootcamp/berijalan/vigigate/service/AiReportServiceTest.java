package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.model.entity.Guest;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AiReportServiceTest {

    @InjectMocks
    private AiReportService aiReportService;

    private Visit visitCompleted;
    private Visit visitActive;

    @BeforeEach
    void setUp() {
        Guest guest1 = Guest.builder().nama("Budi").build();
        Guest guest2 = Guest.builder().nama("Andi").build();

        // Kunjungan yang sudah check-out (Durasi = 30 menit)
        visitCompleted = Visit.builder()
                .guest(guest1)
                .purpose("Meeting HR")
                .checkInTime(LocalDateTime.of(2023, 10, 10, 9, 0))
                .checkOutTime(LocalDateTime.of(2023, 10, 10, 9, 30))
                .build();

        // Kunjungan yang belum check-out (Durasi = 0)
        visitActive = Visit.builder()
                .guest(guest2)
                .purpose("Interview")
                .checkInTime(LocalDateTime.of(2023, 10, 10, 10, 0))
                .checkOutTime(null)
                .build();
    }

    @Test
    void buildPrompt_ShouldCalculateDurationCorrectly() {
        // When: Memanggil method package-private
        String prompt = aiReportService.buildPrompt(List.of(visitCompleted, visitActive));

        // Then: Verifikasi hasil teksnya
        assertNotNull(prompt);
        assertTrue(prompt.contains("- Nama: Budi | Tujuan: Meeting HR | Durasi: 30 menit"));
        assertTrue(prompt.contains("- Nama: Andi | Tujuan: Interview | Durasi: 0 menit"));
    }
}
