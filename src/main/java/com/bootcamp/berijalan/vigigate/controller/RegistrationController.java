package com.bootcamp.berijalan.vigigate.controller;

import com.bootcamp.berijalan.vigigate.dto.request.RegistrationRequest;
import com.bootcamp.berijalan.vigigate.dto.response.GuestCheckResponse;
import com.bootcamp.berijalan.vigigate.model.entity.Visit;
import com.bootcamp.berijalan.vigigate.service.GuestService;
import com.bootcamp.berijalan.vigigate.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vigi/v1/reg")
@RequiredArgsConstructor
public class RegistrationController {

    private final GuestService guestService;
    private final VisitService visitService;

    @GetMapping("/{nik}")
    public ResponseEntity<GuestCheckResponse> checkNik(@PathVariable String nik) {
        return ResponseEntity.ok(guestService.checkGuestStatus(nik));
    }

    @PostMapping("/checkin")
    public ResponseEntity<Visit> registerVisitor(@Valid @RequestBody RegistrationRequest request) {
        Visit savedVisit = guestService.registerVisit(request);
        return ResponseEntity.ok(savedVisit);
    }

    @PutMapping("/checkout/{id}")
    public ResponseEntity<Visit> checkout(@PathVariable UUID id) {
        Visit updatedVisit = visitService.checkoutVisitor(id);
        return ResponseEntity.ok(updatedVisit);
    }
}
