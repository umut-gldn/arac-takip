package com.umutgldn.tracking_service.repository;

import com.umutgldn.tracking_service.entity.Simulation;
import com.umutgldn.tracking_service.entity.SimulationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    List<Simulation> findAllByStatus(SimulationStatus status);

    Page<Simulation> findAllBy(Pageable pageable);
}
