package ru.itmo.web.lab3.beans;

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
    @Inject
    private PointService pointService;

    private double x;
    private double y;
    private double radius = 2.0;

    @PostConstruct
    public void init() {
        pointService.initDatabase();
    }

    /**
     * Проверяет попадание точки в область.
     */
    public void checkPoint() {
        pointService.processPoint(x, y, radius);
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