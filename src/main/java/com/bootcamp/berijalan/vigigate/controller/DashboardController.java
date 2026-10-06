package com.bootcamp.berijalan.vigigate.controller;

import com.bootcamp.berijalan.vigigate.dto.response.AiSummaryResponse;
import com.bootcamp.berijalan.vigigate.service.AiReportService;
import com.bootcamp.berijalan.vigigate.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/vigi/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final SseNotificationService sseNotificationService;
    private final AiReportService aiReportService;

    // Produces MediaType.TEXT_EVENT_STREAM_VALUE sangat penting agar browser tahu ini adalah jalur streaming SSE
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamDashboard() {
        return sseNotificationService.subscribe();
    }

    //Untuk AI
    @GetMapping("/today")
    public ResponseEntity<AiSummaryResponse> getTodaySummary() {
        AiSummaryResponse response = aiReportService.generateTodaySummary();
        return ResponseEntity.ok(response);
    }
}
