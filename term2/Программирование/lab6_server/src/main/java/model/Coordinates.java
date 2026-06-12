package model;

import java.io.Serializable;
/**
 * Класс представляет координаты в двумерном пространстве.
 * Каждая координата включает компоненты x и y, оба из которых не могут быть null.
 * Для y дополнительно требуется, чтобы его значение было больше -500.
 */
public class Coordinates implements Serializable, Validatable {

    private Double x; //Поле не может быть null

    private Double y; //Значение поля должно быть больше -500, Поле не может быть null
    /**
     * Конструктор по умолчанию инициализирует координаты значениями (0.0, 0.0).
     */
    public Coordinates() {
        this.x = 0.0;
        this.y = 0.0;
    }
    /**
     * Конструктор с параметрами для создания координат с заданными значениями x и y.
     *
     * @param x координата по оси x, не должна быть null.
     * @param y координата по оси y, не должна быть null и должна быть больше -500.
     */
    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
    /**
     * Возвращает значение координаты x.
     *
     * @return значение x.
     */
    public Double getX() {
        return x;
    }
    /**
     * Устанавливает значение координаты x.
     *
     * @param x новое значение для x, не должно быть null.
     */
    public void setX(Double x) {
        this.x = x;
    }
    /**
     * Возвращает значение координаты y.
     *
     * @return значение y.
     */
    public Double getY() {
        return y;
    }
    /**
     * Устанавливает значение координаты y.
     *
     * @param y новое значение для y, не должно быть null и должно быть больше -500.
     */
    public void setY(Double y) {
        this.y = y;
    }
    /**
     * Проверяет корректность установленных значений координат.
     * Выводит ошибку, если значения не удовлетворяют условиям.
     *
     * @return true, если значения координат корректны, иначе false.
     */
    @Override
    public boolean validate() {
        if (x == null){
            System.out.println("\u001B[31mError: X не должен быть null \u001B[0m");
            return false;
        }
        if (y == null || y <= -500){
            System.out.println("\u001B[31mError: Y не должен быть null и должен быть больше -500 \u001B[0m");
            return false;
        }
        return true;
    }
}