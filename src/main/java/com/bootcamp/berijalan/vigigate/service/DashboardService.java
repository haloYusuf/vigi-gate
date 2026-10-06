package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import com.bootcamp.berijalan.vigigate.dto.response.ActiveGuestDto;
import com.bootcamp.berijalan.vigigate.dto.response.DashboardLogResponse;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.model.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final VisitRepository visitRepository;

    public DashboardLogResponse getActiveDashboardState() {
        List<Visit> activeVisits = visitRepository.findByVisitStatusOrderByCheckInTimeDesc(VisitStatus.CHECKED_IN);

        List<ActiveGuestDto> guestList = activeVisits.stream().map(visit -> ActiveGuestDto.builder()
                .visitId(visit.getId())
                .nik(visit.getGuest().getNik())
                .nama(visit.getGuest().getNama())
                .photo(visit.getGuest().getPhoto())
                .purpose(visit.getPurpose())
                .riskStatus(visit.getRiskStatus())
                .checkInTime(visit.getCheckInTime())
                .build()).toList();

        return DashboardLogResponse.builder()
                .totalActiveGuests(guestList.size())
                .activeGuests(guestList)
                .build();
    }
}
