package com.umutgldn.tracking_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationEventPublisher {
    private static final String LOCATION_TOPIC = "vehicle.location.updated";
    private static final String MILESTONE_TOPIC="simulation.milestone.reached";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishLocationUpdated(LocationUpdatedEvent event) {
        kafkaTemplate.send(LOCATION_TOPIC,String.valueOf(event.simulationId()), event);
    }

    public void publishMilestoneReached(MilestoneReachedEvent event) {
        kafkaTemplate.send(MILESTONE_TOPIC,String.valueOf(event.simulationId()), event);
    }

}
