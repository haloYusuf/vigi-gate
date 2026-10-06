package com.bootcamp.berijalan.vigigate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RegistrationRequest {

    @NotBlank(message = "NIK tidak boleh kosong")
    @Size(min = 16, max = 16, message = "NIK must be exactly 16 characters")
    private String nik;

    @NotBlank(message = "Tujuan kunjungan wajib diisi")
    private String purpose;

    private String nama;

    private String photo; // Base64 atau Mock string
}