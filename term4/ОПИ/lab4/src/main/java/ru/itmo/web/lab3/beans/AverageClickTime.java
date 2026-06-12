package ru.itmo.web.lab3.beans;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ru.itmo.web.lab3.model.Point;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;

@ApplicationScoped
@Log4j2
public class AverageClickTime implements AverageClickTimeMBean {
    @Setter
    private ObjectName objectName;
    @Setter
    private ObjectName pointsCounterObjectName;
    private LocalDateTime firstClickTime;
    private LocalDateTime lastClickTime;
    private int pointsCount;
    private final MBeanServer mBeanServer;
    private long averageClickTime;

    public AverageClickTime() {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.firstClickTime = null;
        this.lastClickTime = null;
        this.pointsCount = 0;
    }

    @Override
    public long getAverageClickTime() {
        return averageClickTime;
    }

    @Override
    public void proceedPoint(Point point) {
        if (point == null || point.getCheckTime() == null) {
            return;
        }

        if (firstClickTime == null) {
            firstClickTime = point.getCheckTime();
        }

        lastClickTime = point.getCheckTime();

        try {
            pointsCount = (Integer) mBeanServer.getAttribute(pointsCounterObjectName, "PointsCounter");
            log.info("Number of points in first click: {}", pointsCount);
        } catch (Exception e) {
            log.error("Failed to get PointsCounter", e);
            pointsCount = 0;
        }

        if (firstClickTime == null || lastClickTime == null || pointsCount <= 1) {
            averageClickTime = -1;
            log.info("Average click time is incorrect");
            log.info("firstClickTime: {}", firstClickTime);
            log.info("lastClickTime: {}", lastClickTime);
            log.info("pointsCount: {}", pointsCount);
            return;
        }

        try {
            long durationMillis = Duration.between(firstClickTime, lastClickTime).toMillis();
            averageClickTime =  durationMillis / (pointsCount - 1);
            log.info("New average click time: {}", averageClickTime);
        } catch (Exception e) {
            e.printStackTrace();
            averageClickTime = -500;
        }
    }

    @Override
    public void reset() {
        firstClickTime = null;
        lastClickTime = null;
        pointsCount = 0;
    }
}