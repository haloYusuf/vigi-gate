package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import com.bootcamp.berijalan.vigigate.dto.request.RegistrationRequest;
import com.bootcamp.berijalan.vigigate.dto.response.GuestCheckResponse;
import com.bootcamp.berijalan.vigigate.model.entity.Guest;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.GuestRepository;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final VisitRepository visitRepository;
    private final RiskScoringService riskScoringService;
    private final SseNotificationService sseNotificationService;

    public GuestCheckResponse checkGuestStatus(String nik) {
        Optional<Guest> guestOpt = guestRepository.findByNik(nik);

        if (guestOpt.isEmpty()) {
            return GuestCheckResponse.builder().isRegistered(false).hasActiveVisit(false).build();
        }

        Guest guest = guestOpt.get();
        boolean hasActive = visitRepository.existsByGuestIdAndVisitStatus(guest.getId(), VisitStatus.CHECKED_IN);

        return GuestCheckResponse.builder()
                .isRegistered(true)
                .nama(guest.getNama())
                .photo(guest.getPhoto())
                .hasActiveVisit(hasActive)
                .build();
    }

    @Transactional
    public Visit registerVisit(RegistrationRequest request) {
        // Cari atau Buat Guest Baru
        Guest guest = guestRepository.findByNik(request.getNik())
                .orElseGet(() -> {
                    if (request.getNama() == null || request.getNama().isBlank()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama wajib diisi untuk tamu baru!");
                    }
                    Guest newGuest = Guest.builder()
                            .nik(request.getNik())
                            .nama(request.getNama())
                            .photo(request.getPhoto())
                            .build();
                    return guestRepository.save(newGuest);
                });

        // Validasi: Jika sudah CHECKED_IN tapi belum CHECKED_OUT -> Tolak!
        if (visitRepository.existsByGuestIdAndVisitStatus(guest.getId(), VisitStatus.CHECKED_IN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tidak bisa Check In karena belum Check Out!");
        }

        // Ambil data frekuensi kunjungan hari ini untuk Risk Scoring
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        long visitsToday = visitRepository.countByGuestIdAndCheckInTimeBetween(guest.getId(), startOfDay, endOfDay);

        // Kalkulasi Risk Status
        var riskStatus = riskScoringService.calculateRisk(visitsToday, now);

        // Buat data Visit baru
        Visit visit = Visit.builder()
                .guest(guest)
                .purpose(request.getPurpose())
                .riskStatus(riskStatus)
                .visitStatus(VisitStatus.CHECKED_IN)
                .build();

        // Simpan ke database
        Visit savedVisit = visitRepository.save(visit);

        sseNotificationService.broadcastDashboardUpdate();

        return savedVisit;
    }
}