package com.umutgldn.notification_service.listener;

import com.umutgldn.notification_service.event.LocationUpdatedEvent;
import com.umutgldn.notification_service.event.MilestoneReachedEvent;
import com.umutgldn.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "vehicle.location.updated",groupId = "${spring.kafka.consumer.group-id}")
    public void onLocationUpdated(LocationUpdatedEvent event){
        log.debug("Received LocationUpdatedEvent: {}", event);
        notificationService.recordLocationUpdate(event);
    }

    @KafkaListener(topics = "simulation.milestone.reached",groupId = "${spring.kafka.consumer.group-id}")
    public void onMilestoneReached(MilestoneReachedEvent event){
        log.info("Received MilestoneReachedEvent: {}", event);
        notificationService.recordMilestoneReached(event);
    }
}
