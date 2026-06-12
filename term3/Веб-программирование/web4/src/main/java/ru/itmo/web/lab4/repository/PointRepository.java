package ru.itmo.web.lab4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.web.lab4.model.Point;
import ru.itmo.web.lab4.model.User;
import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUser(User user);
}