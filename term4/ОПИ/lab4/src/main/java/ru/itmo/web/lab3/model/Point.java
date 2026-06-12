package ru.itmo.web.lab3.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Представляет координатную точку с параметрами проверки попадания в область.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private double x;
    private double y;
    private double radius;
    private boolean hit;
    private LocalDateTime checkTime;
    private long executionTime;
}