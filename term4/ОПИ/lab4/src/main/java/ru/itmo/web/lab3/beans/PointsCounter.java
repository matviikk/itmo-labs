package ru.itmo.web.lab3.beans;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;
import ru.itmo.web.lab3.model.Point;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import java.io.Serial;
import java.io.Serializable;

@ApplicationScoped
public class PointsCounter extends NotificationBroadcasterSupport
        implements PointsCounterMBean, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int pointsCounter = 0;
    private int hitsCounter = 0;
    private long notificationSequence = 0;
    @Setter
    private transient ObjectName objectName;
    @Override
    public void proceedPoint(Point point) {
        if(point.isHit()) {
            hitsCounter++;
        }
        pointsCounter++;
        checkTenPoints();
    }

    @Override
    public int getPointsCounter() {
        return pointsCounter;
    }

    @Override
    public int getHitsCounter() {
        return hitsCounter;
    }

    @Override
    public void resetCounters() {
        pointsCounter = 0;
        hitsCounter = 0;
    }

    void checkTenPoints() {
        if(pointsCounter % 10 == 0) {
            Notification notification = new Notification(
                    "PointsCounter Notification",
                    objectName != null ? objectName.toString() : "PointsCounter",
                    notificationSequence++,
                    System.currentTimeMillis(),
                    "Reached " + pointsCounter + " points");
            sendNotification(notification);
        }
    }
}
