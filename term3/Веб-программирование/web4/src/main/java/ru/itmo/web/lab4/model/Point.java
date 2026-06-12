package ru.itmo.web.lab4.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;
    private double y;
    private double radius;
    private boolean hit;
    private LocalDateTime checkTime;
    private long executionTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}