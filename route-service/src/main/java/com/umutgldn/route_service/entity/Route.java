package com.umutgldn.route_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 200)
    private String name;

    @Column(name = "start_latitude",nullable = false)
    private Double startLatitude;

    @Column(name = "start_longitude",nullable = false)
    private Double startLongitude;

    @Column(name = "end_latitude",nullable = false)
    private Double endLatitude;

    @Column(name = "end_longitude",nullable = false)
    private Double endLongitude;

    @Column(name = "total_distance_meters",nullable = false)
    private Double totalDistanceMeters;

    @Column(name = "total_duration_seconds",nullable = false)
    private Double totalDurationSeconds;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sequence ASC")
    @Builder.Default
    private List<RouteCoordinate> coordinates= new ArrayList<>();


    public void  addCoordinate(RouteCoordinate coordinate){
        coordinates.add(coordinate);
        coordinate.setRoute(this);
    }

}
