package com.umutgldn.route_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "route_coordinates",
        indexes = @Index(name = "idx_route_sequence",columnList = "route_id, sequence")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCoordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "route_id",nullable = false)
    private Route route;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}
