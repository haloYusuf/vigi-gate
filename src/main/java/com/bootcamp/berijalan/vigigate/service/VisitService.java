package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final SseNotificationService sseNotificationService;

    @Transactional
    public Visit checkoutVisitor(UUID visitId) {
        // Cari data kunjungan berdasarkan ID
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data kunjungan tidak ditemukan!"));

        // Validasi apakah tamu sudah pernah check-out
        if (visit.getVisitStatus() == VisitStatus.CHECKED_OUT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tamu ini sudah melakukan check-out sebelumnya.");
        }

        // Update status dan waktu check-out
        visit.setVisitStatus(VisitStatus.CHECKED_OUT);
        visit.setCheckOutTime(LocalDateTime.now());

        // Simpan perubahan ke database
        Visit updatedVisit = visitRepository.save(visit);

        sseNotificationService.broadcastDashboardUpdate();

        return updatedVisit;
    }
}
