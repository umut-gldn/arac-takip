package com.umutgldn.notification_service.service.impl;

import com.umutgldn.notification_service.entity.LocationLog;
import com.umutgldn.notification_service.entity.Notification;
import com.umutgldn.notification_service.event.LocationUpdatedEvent;
import com.umutgldn.notification_service.event.MilestoneReachedEvent;
import com.umutgldn.notification_service.repository.LocationLogRepository;
import com.umutgldn.notification_service.repository.NotificationRepository;
import com.umutgldn.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final LocationLogRepository locationLogRepository;


    @Override
    @Transactional
    public void recordLocationUpdate(LocationUpdatedEvent event) {
        LocationLog locationLog=LocationLog.builder()
                .simulationId(event.simulationId())
                .vehicleName(event.vehicleName())
                .routeId(event.routeId())
                .currentCoordinateIndex(event.currentCoordinateIndex())
                .totalCoordinates(event.totalCoordinates())
                .progressPercentage(event.progressPercentage())
                .latitude(event.latitude())
                .longitude(event.longitude())
                .occurredAt(event.occurredAt())
                .build();
        locationLogRepository.save(locationLog);
        log.debug("Location log saved: simulationId={} index={}", event.simulationId(), event.currentCoordinateIndex());

    }

    @Override
    @Transactional
    public void recordMilestoneReached(MilestoneReachedEvent event) {
        Notification notification=Notification.builder()
                .simulationId(event.simulationId())
                .vehicleName(event.vehicleName())
                .milestone(event.milestone().name())
                .progressPercentage(event.progressPercentage())
                .occurredAt(event.occurredAt())
                .build();
        notificationRepository.save(notification);
        log.info("Notification saved: simulationId={} milestone={}", event.simulationId(), event.milestone());
    }
}
