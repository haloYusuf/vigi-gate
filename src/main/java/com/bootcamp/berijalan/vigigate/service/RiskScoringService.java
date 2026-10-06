package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.constant.RiskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class RiskScoringService {

    public RiskStatus calculateRisk(long existingVisitsToday, LocalDateTime checkInTime) {
        LocalTime time = checkInTime.toLocalTime();
        long totalCheckIns = existingVisitsToday + 1;

        // Kondisi RED
        boolean isLateNight = time.isAfter(LocalTime.of(21, 59)) || time.isBefore(LocalTime.of(6, 0));
        if ((isLateNight && totalCheckIns > 2) || totalCheckIns > 5) {
            return RiskStatus.RED;
        }

        // Kondisi YELLOW
        boolean isEvening = time.isAfter(LocalTime.of(17, 59)) && time.isBefore(LocalTime.of(22, 0));
        if (isEvening || totalCheckIns > 3) {
            return RiskStatus.YELLOW;
        }

        // Kondisi GREEN
        return RiskStatus.GREEN;
    }
}