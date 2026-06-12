package ru.itmo.web.lab3.service;

import ru.itmo.web.lab3.beans.AverageClickTime;
import ru.itmo.web.lab3.beans.PointsCounter;
import ru.itmo.web.lab3.model.Point;
import ru.itmo.web.lab3.util.AreaChecker;
import ru.itmo.web.lab3.repository.PointRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления точками, выполняющий проверку попадания в область и взаимодействие с репозиторием.
 */
@ApplicationScoped
public class PointService {
    @Inject
    private PointRepository pointRepository;
    @Inject
    private PointsCounter pointsCounter;
    @Inject
    private AverageClickTime averageClickTime;

    public void initDatabase() {
        pointRepository.initTable();
    }

    /**
     * Обрабатывает точку, проверяя её попадание в область и сохраняя результат в базе данных.
     *
     * @param x координата X точки
     * @param y координата Y точки
     * @param radius радиус области
     */
    public void processPoint(double x, double y, double radius) {
        long startTime = System.nanoTime();
        boolean hit = AreaChecker.checkHit(x, y, radius);
        long executionTime = (System.nanoTime() - startTime) / 1000;

        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setRadius(radius);
        point.setHit(hit);
        point.setCheckTime(LocalDateTime.now());
        point.setExecutionTime(executionTime);

        pointRepository.save(point);

        pointsCounter.proceedPoint(point);
        averageClickTime.proceedPoint(point);
    }

    /**
     * Извлекает все точки из базы данных.
     *
     * @return список всех точек
     */
    public List<Point> getAllPoints() {
        return pointRepository.findAll();
    }

    /**
     * Удаляет точку из базы данных по её идентификатору.
     *
     * @param id идентификатор точки
     */
    public void deletePoint(Long id) {
        pointRepository.delete(id);
    }

    /**
     * Удаляет все точки из базы данных.
     */
    public void deleteAllPoints() {
        pointRepository.deleteAll();
    }
}