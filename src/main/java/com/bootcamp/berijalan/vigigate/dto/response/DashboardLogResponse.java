package com.bootcamp.berijalan.vigigate.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardLogResponse {
    // Di luar list, menyimpan jumlah total yang belum check-out
    private long totalActiveGuests;
    // List detail tamunya
    private List<ActiveGuestDto> activeGuests;
}
