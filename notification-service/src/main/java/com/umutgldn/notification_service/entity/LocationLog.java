package com.umutgldn.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "location_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "simulation_id",nullable = false)
    private Long simulationId;

    @Column(name = "vehicle_name",nullable = false,length = 100)
    private String vehicleName;

    @Column(name = "route_id",nullable = false)
    private Long routeId;

    @Column(name = "current_coordinate_index", nullable = false)
    private Integer currentCoordinateIndex;

    @Column(name = "total_coordinates", nullable = false)
    private Integer totalCoordinates;

    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
