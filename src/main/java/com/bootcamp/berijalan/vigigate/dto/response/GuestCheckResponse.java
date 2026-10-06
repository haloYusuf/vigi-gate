package com.bootcamp.berijalan.vigigate.dto.response;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class GuestCheckResponse {
    private boolean isRegistered;
    private String nama;
    private String photo;
    private boolean hasActiveVisit;
}
