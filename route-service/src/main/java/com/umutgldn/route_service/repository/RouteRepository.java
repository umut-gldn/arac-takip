package com.umutgldn.route_service.repository;

import com.umutgldn.route_service.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route,Long> {

    @Query("""
        SELECT r FROM Route r
        LEFT JOIN FETCH r.coordinates
        WHERE r.id = :id
        """)
    Optional<Route> findByIdWithCoordinates(Long id);

    Page<Route> findAllBy(Pageable pageable);
}
