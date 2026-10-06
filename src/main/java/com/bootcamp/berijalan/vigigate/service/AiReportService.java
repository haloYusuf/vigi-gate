package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.dto.response.AiSummaryResponse;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.google.genai.Client;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReportService {
    private final VisitRepository visitRepository;

    private final Client geminiClient;

    public AiSummaryResponse generateTodaySummary() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<Visit> visits = visitRepository.findByCheckInTimeBetweenOrderByCheckInTimeAsc(startOfDay, endOfDay);
        long totalGuestCount = visits.size();

        // Jika kosong, langsung kembalikan DTO dengan result default dan total 0
        if (visits.isEmpty()) {
            return AiSummaryResponse.builder()
                    .result("Tidak ada data kunjungan tamu untuk hari ini.")
                    .totalGuest(0)
                    .build();
        }

        String prompt = buildPrompt(visits);
        String aiSummaryText;

        try {
            aiSummaryText = geminiClient.models.generateContent("gemini-2.5-flash", prompt, null).text();
        } catch (Exception e) {
            log.error("Gagal mendapatkan summary dari AI", e);
            aiSummaryText = "Maaf, sistem gagal menghubungi AI untuk membuat ringkasan: " + e.getMessage();
        }

        // Kembalikan DTO yang sudah berisi response AI dan jumlah tamu
        return AiSummaryResponse.builder()
                .result(aiSummaryText)
                .totalGuest(totalGuestCount)
                .build();
    }

    String buildPrompt(List<Visit> visits) {
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("Buatkan paragraf ringkasan naratif secara langsung berbahasa Indonesia berdasarkan data pengunjung hari ini.\n");
        dataBuilder.append("Sebutkan total pengunjung, pola tujuan, dan rata-rata durasinya.\n");
        dataBuilder.append("Langsung hasilnya saja jangan pakai intro atau salam.\n");
        dataBuilder.append("Berikut adalah data mentahnya:\n");

        for (Visit visit : visits) {
            long durationMinutes = 0;
            if (visit.getCheckOutTime() != null) {
                durationMinutes = Duration.between(visit.getCheckInTime(), visit.getCheckOutTime()).toMinutes();
            }

            dataBuilder.append("- Nama: ").append(visit.getGuest().getNama())
                    .append(" | Tujuan: ").append(visit.getPurpose())
                    .append(" | Durasi: ").append(durationMinutes).append(" menit\n");
        }

        return dataBuilder.toString();
    }
}
