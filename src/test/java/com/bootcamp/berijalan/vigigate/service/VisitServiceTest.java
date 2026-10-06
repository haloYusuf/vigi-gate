package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.RiskStatus;
import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private Visit mockVisit;
    private UUID visitId;

    @BeforeEach
    void setUp() {
        visitId = UUID.randomUUID();

        // Setup data mock kondisi tamu sedang berada di dalam gedung (CHECKED_IN)
        mockVisit = Visit.builder()
                .id(visitId)
                .purpose("Meeting dengan HRD")
                .checkInTime(LocalDateTime.now().minusHours(2)) // Anggap masuk 2 jam yang lalu
                .riskStatus(RiskStatus.GREEN)
                .visitStatus(VisitStatus.CHECKED_IN)
                .build();
    }

    @Test
    void checkoutVisitor_Success() {
        // Given
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(mockVisit));
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Visit result = visitService.checkoutVisitor(visitId);

        // Then
        assertNotNull(result);
        assertEquals(VisitStatus.CHECKED_OUT, result.getVisitStatus(), "Status harus berubah menjadi CHECKED_OUT");
        assertNotNull(result.getCheckOutTime(), "Waktu check-out tidak boleh null");

        // Verifikasi bahwa repository.save dipanggil tepat 1 kali
        verify(visitRepository, times(1)).save(mockVisit);
    }

    @Test
    void checkoutVisitor_ShouldThrowNotFound_WhenIdDoesNotExist() {
        // Given
        UUID randomId = UUID.randomUUID();
        when(visitRepository.findById(randomId)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            visitService.checkoutVisitor(randomId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assert exception.getReason() != null;
        assertTrue(exception.getReason().contains("tidak ditemukan"));

        // Verifikasi bahwa repository.save TIDAK PERNAH dipanggil karena proses gagal duluan
        verify(visitRepository, never()).save(any());
    }

    @Test
    void checkoutVisitor_ShouldThrowBadRequest_WhenAlreadyCheckedOut() {
        // Given
        // Kita ubah data mock seolah-olah tamu ini sudah pulang
        mockVisit.setVisitStatus(VisitStatus.CHECKED_OUT);
        mockVisit.setCheckOutTime(LocalDateTime.now().minusHours(1));

        when(visitRepository.findById(visitId)).thenReturn(Optional.of(mockVisit));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            visitService.checkoutVisitor(visitId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("sudah melakukan check-out"));

        // Verifikasi keamanan bahwa save tidak terpanggil agar data DB tidak tertimpa percuma
        verify(visitRepository, never()).save(any());
    }
}
