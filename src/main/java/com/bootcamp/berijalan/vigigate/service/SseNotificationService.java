package com.bootcamp.berijalan.vigigate.service;

import com.bootcamp.berijalan.vigigate.dto.response.DashboardLogResponse;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseNotificationService {
    private final DashboardService dashboardService;

    // CopyOnWriteArrayList aman untuk multithreading
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // Set timeout ke 1 jam (3600000ms), jika lewat frontend akan otomatis reconnect
        SseEmitter emitter = new SseEmitter(3600000L);
        emitters.add(emitter);

        // Hapus emitter jika koneksi terputus dari sisi frontend
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // Kirim data state saat ini seketika saat pertama kali frontend connect
        sendInitialData(emitter);

        return emitter;
    }

    private void sendInitialData(SseEmitter emitter) {
        try {
            DashboardLogResponse currentState = dashboardService.getActiveDashboardState();
            emitter.send(SseEmitter.event().name("dashboard-update").data(currentState));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
    }

    // Method ini akan dipanggil dari GuestServicedan VisitService
    public void broadcastDashboardUpdate() {
        DashboardLogResponse updatedState = dashboardService.getActiveDashboardState();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("dashboard-update").data(updatedState));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    @PreDestroy
    public void onShutdown() {
        log.info("Server mau mati, menutup semua koneksi SSE...");
        for (SseEmitter emitter : emitters) {
            // Memutus koneksi dengan baik-baik
            emitter.complete();
        }
        // Bersihkan daftar
        emitters.clear();
    }
}
