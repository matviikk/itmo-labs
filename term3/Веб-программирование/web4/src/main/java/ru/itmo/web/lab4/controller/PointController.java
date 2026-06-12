package ru.itmo.web.lab4.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itmo.web.lab4.model.Point;
import ru.itmo.web.lab4.model.User;
import ru.itmo.web.lab4.service.PointService;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PointController {

    private static final Logger logger = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    @PostMapping
    public ResponseEntity<Point> checkPoint(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double radius,
            @AuthenticationPrincipal User user) {
        logger.debug("Received request to check point: x={}, y={}, radius={}", x, y, radius);

        if (user == null) {
            logger.warn("Unauthorized access attempt to checkPoint.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Processing point for user: {}", user.getUsername());
        Point point = pointService.processPoint(x, y, radius, user);
        logger.debug("Point processed successfully: {}", point);

        return ResponseEntity.ok(point);
    }

    @GetMapping
    public ResponseEntity<List<Point>> getAllPoints(@AuthenticationPrincipal User user) {
        if (user == null) {
            logger.warn("Unauthorized access attempt to getAllPoints.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Authenticated user: {}", user.getUsername());
        List<Point> points = pointService.getAllPointsByUser(user);
        return ResponseEntity.ok(points);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        logger.debug("Received request to delete point with id={}", id);

        pointService.deletePoint(id);
        logger.info("Deleted point with id={}", id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPoints(@AuthenticationPrincipal User user) {
        logger.debug("Received request to delete all points");

        if (user == null) {
            logger.warn("Unauthorized access attempt to deleteAllPoints.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Deleting all points for user: {}", user.getUsername());
        pointService.deleteAllPointsByUser(user);
        logger.info("All points deleted for user: {}", user.getUsername());

        return ResponseEntity.ok().build();
    }
}
