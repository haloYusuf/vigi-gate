package com.bootcamp.berijalan.vigigate.dto.response;

import com.bootcamp.berijalan.vigigate.constant.RiskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class ActiveGuestDto {
    // Penting untuk dikirim ke frontend agar tombol "Check-Out" tahu ID mana yang mau di-hit
    private UUID visitId;
    private String nik;
    private String nama;
    private String photo;
    private String purpose;
    private RiskStatus riskStatus;
    private LocalDateTime checkInTime;
}
