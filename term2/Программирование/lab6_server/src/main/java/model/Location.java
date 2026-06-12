package model;

import java.io.Serializable;
/**
 * Класс, представляющий местоположение.
 * Содержит координаты x, y, z и название места.
 * Все поля, кроме z, не могут быть null.
 */
public class Location implements Serializable, Validatable {

    private Double x; //Поле не может быть null

    private Long y; //Поле не может быть null
    private double z;

    private String name; //Поле не может быть null
    /**
     * Конструктор по умолчанию, инициализирует местоположение с нулевыми координатами и пустым названием.
     */
    public Location() {
        this.x = 0.0;
        this.y = 0L;
        this.z = 0.0;
        this.name = "empty";
    }
    /**
     * Конструктор с параметрами для инициализации всех полей класса.
     *
     * @param x Координата x, не должна быть null.
     * @param y Координата y, не должна быть null.
     * @param z Координата z.
     * @param name Название места, не должно быть null.
     */
    public Location(Double x, Long y, double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }
    // Геттеры и сеттеры для каждого поля класса
    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * Проверяет корректность значений полей класса.
     * Выводит сообщение об ошибке, если какое-либо поле не удовлетворяет условиям.
     *
     * @return true, если все поля корректны, иначе false.
     */
    @Override
    public boolean validate() {
        if (x == null){
            System.out.println("\u001B[31mError: Координата X не должна быть null \u001B[0m");
            return false;
        }
        if (y == null){
            System.out.println("\u001B[31mError: Координата Y не должна быть null \u001B[0m");
            return false;
        }
        if (name == null){
            System.out.println("\u001B[31mError: Название места рождения не должно быть null \u001B[0m");
            return false;
        }
        return true;
    }
}
