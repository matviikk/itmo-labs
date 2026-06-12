package ru.itmo.web.lab3.util;

import javax.management.NotificationEmitter;

public interface PointStatsMBean extends NotificationEmitter {
    void addPoint(boolean isHit);
    int getTotalPoints();
    int getHitPoints();
}