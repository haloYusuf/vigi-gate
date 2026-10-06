package com.bootcamp.berijalan.vigigate.config;

import com.google.genai.Client;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer{
    // Konfigurasi HTTP Client bawaan Spring untuk AI Report
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    // Konfigurasi CORS agar frontend bisa mengakses endpoint dan SSE
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Izinkan semua origin selama development
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // Wajib true agar koneksi SSE berhasil
                .allowCredentials(true);
    }

    // Inject AI Client bawaan dari dependency google-genai
    @Bean
    public Client geminiClient(@Value("${ai.api.key}") String apiKey) {
        return Client.builder().apiKey(apiKey).build();
    }

    @Bean
    public OpenAPI smartRegistrationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VigiGate API")
                        .description("Dokumentasi API untuk Sistem Manajemen Tamu dengan AI Summary")
                        .version("v1.0.0"));
    }
}
