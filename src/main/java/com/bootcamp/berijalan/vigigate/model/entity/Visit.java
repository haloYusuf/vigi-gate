package com.bootcamp.berijalan.vigigate.model.entity;

import com.bootcamp.berijalan.vigigate.constant.RiskStatus;
import com.bootcamp.berijalan.vigigate.constant.VisitStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visits")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private String purpose;

    @CreationTimestamp
    @Column(name = "check_in_time", nullable = false, updatable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_status", nullable = false)
    private RiskStatus riskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", nullable = false)
    private VisitStatus visitStatus;
}