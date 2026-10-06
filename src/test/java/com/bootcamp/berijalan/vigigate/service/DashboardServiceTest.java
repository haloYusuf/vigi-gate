package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.RiskStatus;
import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import com.bootcamp.berijalan.vigigate.dto.response.DashboardLogResponse;
import com.bootcamp.berijalan.vigigate.model.entity.Guest;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Visit mockVisit;

    @BeforeEach
    void setUp() {
        Guest guest = Guest.builder()
                .nik("1234567890123456")
                .nama("Joko Anwar")
                .photo("mock-photo")
                .build();

        mockVisit = Visit.builder()
                .id(UUID.randomUUID())
                .guest(guest)
                .purpose("Meeting Direksi")
                .riskStatus(RiskStatus.GREEN)
                .visitStatus(VisitStatus.CHECKED_IN)
                .checkInTime(LocalDateTime.now())
                .build();
    }

    @Test
    void getActiveDashboardState_ShouldReturnMappedData() {
        // Given
        when(visitRepository.findByVisitStatusOrderByCheckInTimeDesc(VisitStatus.CHECKED_IN))
                .thenReturn(List.of(mockVisit));

        // When
        DashboardLogResponse result = dashboardService.getActiveDashboardState();

        // Then
        assertEquals(1, result.getTotalActiveGuests());
        assertEquals(1, result.getActiveGuests().size());
        assertEquals("Joko Anwar", result.getActiveGuests().getFirst().getNama());
        assertEquals("Meeting Direksi", result.getActiveGuests().getFirst().getPurpose());
    }
}