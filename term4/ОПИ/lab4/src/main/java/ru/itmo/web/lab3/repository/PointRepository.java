package ru.itmo.web.lab3.repository;

import ru.itmo.web.lab3.model.Point;
import ru.itmo.web.lab3.util.DatabaseProperties;
import ru.itmo.web.lab3.util.SqlLoader;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Репозиторий для работы с данными точек в базе данных.
 */
@ApplicationScoped
@Log
public class PointRepository {
    /**
     * Инициализирует таблицу в базе данных для хранения точек.
     */
    public void initTable() {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(SqlLoader.getQuery("Create points table"));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error initializing database", e);
        }
    }

    /**
     * Сохраняет точку в базе данных.
     *
     * @param point объект точки, который необходимо сохранить
     */
    public void save(Point point) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    SqlLoader.getQuery("Save point"),
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDouble(1, point.getX());
                pstmt.setDouble(2, point.getY());
                pstmt.setDouble(3, point.getRadius());
                pstmt.setBoolean(4, point.isHit());
                pstmt.setTimestamp(5, Timestamp.valueOf(point.getCheckTime()));
                pstmt.setLong(6, point.getExecutionTime());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        point.setId(rs.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error saving point", e);
        }
    }

    /**
     * Извлекает все точки из базы данных.
     *
     * @return список всех точек
     */
    public List<Point> findAll() {
        List<Point> points = new ArrayList<>();
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(SqlLoader.getQuery("Get all points"))) {
                while (rs.next()) {
                    Point point = new Point();
                    point.setId(rs.getLong("id"));
                    point.setX(rs.getDouble("x"));
                    point.setY(rs.getDouble("y"));
                    point.setRadius(rs.getDouble("radius"));
                    point.setHit(rs.getBoolean("hit"));
                    point.setCheckTime(rs.getTimestamp("check_time").toLocalDateTime());
                    point.setExecutionTime(rs.getLong("execution_time"));
                    points.add(point);
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error retrieving points", e);
        }
        return points;
    }

    /**
     * Удаляет точку из базы данных по её идентификатору.
     *
     * @param id идентификатор удаляемой точки
     */
    public void delete(Long id) {
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(SqlLoader.getQuery("Delete point by id"))) {
                pstmt.setLong(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error deleting point", e);
        }
    }

    /**
     * Удаляет все точки из базы данных.
     */
    public void deleteAll() {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(SqlLoader.getQuery("Delete all points"));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error deleting all points", e);
        }
    }

    /**
     * Получает соединение с базой данных.
     *
     * @return объект соединения
     * @throws SQLException если возникает ошибка соединения
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseProperties.getUrl(),
                DatabaseProperties.getUsername(),
                DatabaseProperties.getPassword()
        );
    }
}