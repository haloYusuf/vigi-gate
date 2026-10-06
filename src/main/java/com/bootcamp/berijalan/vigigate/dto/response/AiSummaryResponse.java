package com.bootcamp.berijalan.vigigate.dto.response;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor @Builder
public class AiSummaryResponse {
    private String result;
    private long totalGuest;
}
