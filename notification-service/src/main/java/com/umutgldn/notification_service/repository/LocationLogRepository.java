package com.umutgldn.notification_service.repository;

import com.umutgldn.notification_service.entity.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationLogRepository extends JpaRepository<LocationLog,Long> {

}
