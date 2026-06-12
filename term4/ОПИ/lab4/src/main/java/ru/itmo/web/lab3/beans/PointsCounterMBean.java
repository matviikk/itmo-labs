package ru.itmo.web.lab3.beans;

import ru.itmo.web.lab3.model.Point;

public interface PointsCounterMBean {
    void proceedPoint(Point point);
    int getPointsCounter();
    int getHitsCounter();
    void resetCounters();
}