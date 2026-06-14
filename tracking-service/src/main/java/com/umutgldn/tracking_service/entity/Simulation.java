package com.umutgldn.tracking_service.entity;

import com.umutgldn.tracking_service.event.Milestone;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "simulations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_name", nullable = false, length = 100)
    private String vehicleName;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SimulationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_milestone", length = 20)
    private Milestone lastMilestone;

    @Column(name = "current_coordinate_index", nullable = false)
    private Integer currentCoordinateIndex;

    @Column(name = "total_coordinates", nullable = false)
    private Integer totalCoordinates;

    @Column(name = "current_latitude", nullable = false)
    private Double currentLatitude;

    @Column(name = "current_longitude", nullable = false)
    private Double currentLongitude;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
