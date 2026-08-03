package com.umutgldn.notification_service.service;

import com.umutgldn.notification_service.event.LocationUpdatedEvent;
import com.umutgldn.notification_service.event.MilestoneReachedEvent;

public interface NotificationService {
    void recordLocationUpdate(LocationUpdatedEvent event);
    void recordMilestoneReached(MilestoneReachedEvent event);
}
