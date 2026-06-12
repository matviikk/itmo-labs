package ru.itmo.web.lab3.util;

import javax.management.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PointStats extends NotificationBroadcasterSupport implements PointStatsMBean {
    private final AtomicInteger totalPoints = new AtomicInteger(0);
    private final AtomicInteger hitPoints = new AtomicInteger(0);
    private long sequenceNumber = 1;

    @Override
    public void addPoint(boolean isHit) {
        int currentTotal = totalPoints.incrementAndGet();
        if (isHit) hitPoints.incrementAndGet();

        if (currentTotal % 10 == 0) {
            Notification notification = new Notification(
                    "ru.itmo.web.lab3.util.PointStats.notification",
                    this,
                    sequenceNumber++,
                    System.currentTimeMillis(),
                    "User set " + currentTotal + " points"
            );
            sendNotification(notification);
        }
    }

    @Override
    public int getTotalPoints() {
        return totalPoints.get();
    }

    @Override
    public int getHitPoints() {
        return hitPoints.get();
    }
}