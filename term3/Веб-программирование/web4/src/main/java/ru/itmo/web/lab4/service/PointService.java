package ru.itmo.web.lab4.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.web.lab4.model.Point;
import ru.itmo.web.lab4.model.User;
import ru.itmo.web.lab4.repository.PointRepository;
import ru.itmo.web.lab4.util.AreaChecker;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public Point processPoint(double x, double y, double radius, User user) {
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
        point.setUser(user);

        return pointRepository.save(point);
    }

    public List<Point> getAllPointsByUser(User user) {
        return pointRepository.findByUser(user);
    }

    public void deletePoint(Long id) {
        pointRepository.deleteById(id);
    }

    public void deleteAllPointsByUser(User user) {
        List<Point> userPoints = pointRepository.findByUser(user);
        pointRepository.deleteAll(userPoints);
    }
}