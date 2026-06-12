package ru.itmo.web.lab3.beans;

import ru.itmo.web.lab3.util.PointStats;
import javax.management.*;
import java.lang.management.ManagementFactory;
import ru.itmo.web.lab3.model.Point;
import ru.itmo.web.lab3.service.PointService;
import lombok.Data;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Управляемый бин для работы с точками в рамках сессии пользователя.
 */
@Named("pointBean")
@SessionScoped
@Data
public class PointBean implements Serializable {
    private PointStats pointStats;

    @Inject
    private PointService pointService;

    private double x;
    private double y;
    private double radius = 2.0;

    @PostConstruct
    public void init() {
        pointService.initDatabase();
        try {
            System.out.println("PID = " + ProcessHandle.current().pid());
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("ru.itmo.web.lab3.util:type=PointStats");
            pointStats = new PointStats();
            if (!mbs.isRegistered(name)) {
                mbs.registerMBean(pointStats, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет попадание точки в область.
     */
    public void checkPoint() {
        pointService.processPoint(x, y, radius);
        List<Point> points = pointService.getAllPoints();
        if (!points.isEmpty()) {
            Point last = points.get(points.size() - 1);
            pointStats.addPoint(last.isHit());
        }
    }

    /**
     * Получает список всех точек.
     *
     * @return список точек
     */
    public List<Point> getPoints() {
        return pointService.getAllPoints();
    }

    /**
     * Удаляет точку по идентификатору.
     *
     * @param id идентификатор точки
     */
    public void deletePoint(Long id) {
        pointService.deletePoint(id);
    }

    /**
     * Очищает все точки.
     */
    public void clearPoints() {
        pointService.deleteAllPoints();
    }
}