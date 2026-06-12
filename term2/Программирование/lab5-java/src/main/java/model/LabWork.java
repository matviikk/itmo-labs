package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.bytebuddy.asm.Advice;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Класс, представляющий лабораторную работу.
 * Включает в себя информацию о названии, координатах, дате создания, минимальной и средней оценке,
 * сложности и авторе работы. ID каждой работы генерируется автоматически и уникален.
 */
public class LabWork implements Comparable<LabWork>, Serializable, Validatable {
    private static Integer generatedId = 1;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    private String name; //Поле не может быть null, Строка не может быть пустой

    private Coordinates coordinates; //Поле не может быть null

    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    private Double minimalPoint; //Поле может быть null, Значение поля должно быть больше 0

    private int averagePoint; //Значение поля должно быть больше 0

    private Difficulty difficulty; //Поле может быть null

    private Person author; //Поле может быть null

    /**
     * Конструктор по умолчанию, инициализирует объект с параметрами по умолчанию.
     */
    public LabWork() {
        this.id = generatedId++;
        this.name = "empty";
        this.coordinates = new Coordinates();
        this.creationDate = LocalDate.now();
        this.minimalPoint = 1.0;
        this.averagePoint = 1;
    }
    /**
     * Конструктор с параметрами для инициализации всех полей класса.
     */
    public LabWork(String name, Coordinates coordinates, LocalDate creationDate, Double minimalPoint, int averagePoint, Difficulty difficulty, Person author) {
        this.id = generatedId++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.averagePoint = averagePoint;
        this.difficulty = difficulty;
        this.author = author;
    }

    // Геттеры и сеттеры для каждого поля класса

    public static Integer getGeneratedId() {
        return generatedId;
    }

    public static void setGeneratedId(Integer generatedId) {
        LabWork.generatedId = generatedId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Double getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Double minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public int getAveragePoint() {
        return averagePoint;
    }

    public void setAveragePoint(int averagePoint) {
        this.averagePoint = averagePoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", minimalPoint=" + minimalPoint +
                ", averagePoint=" + averagePoint +
                ", difficulty=" + difficulty +
                ", author=" + author +
                '}';
    }
    /**
     * Метод сравнения для упорядочивания лабораторных работ по средней оценке.
     */
    @Override
    public int compareTo(LabWork o) {
        int c = Integer.compare(this.averagePoint, o.averagePoint);
        if (c != 0) {
            return c;
        }
        else {
            return Integer.compare(this.id, o.id);
        }
    }
    /**
     * Метод для проверки корректности значений всех полей класса.
     */
    @Override
    public boolean validate() {
        if (id == null || id <= 0){
            System.out.println("\u001B[31mError: Айди не должно быть null и должно быть больше 0 \u001B[0m");
            return false;
        }
        if (name == null || name.isEmpty()){
            System.out.println("\u001B[31mError: Название не должно быть null и не должно быть пустым \u001B[0m");
            return false;
        }
        if (coordinates == null){
            System.out.println("\u001B[31mError: Координаты не должны быть null \u001B[0m");
            return false;
        }
        if (creationDate == null){
            System.out.println("\u001B[31mError: Дата создания не должна быть null \u001B[0m");
            return false;
        }
        if (minimalPoint == null || minimalPoint <= 0){
            System.out.println("\u001B[31mError: Минимальная оценка не должна быть null и должна быть больше 0 \u001B[0m");
            return false;
        }
        if (averagePoint <= 0){
            System.out.println("\u001B[31mError: Средняя оценка должна быть больше 0 \u001B[0m");
            return false;
        }
        return true;
    }
}