package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * Класс, представляющий человека.
 * Включает в себя информацию об имени, дне рождения, росте и местоположении.
 */
public class Person implements Serializable, Validatable {

    private String name; //Поле не может быть null, Строка не может быть пустой

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime birthday; //Поле не может быть null

    private Integer height; //Поле может быть null, Значение поля должно быть больше 0

    private Location location; //Поле не может быть null
    /**
     * Конструктор по умолчанию, инициализирует объект с параметрами по умолчанию.
     */
    public Person() {
        this.name = "empty";
        this.birthday = LocalDateTime.now();
        this.height = 1;
        this.location = new Location(0.0, 0L, 0, "empty");
    }
    /**
     * Конструктор с параметрами для инициализации всех полей класса.
     *
     * @param name Имя человека, не должно быть null и пустым.
     * @param birthday Дата рождения, не должна быть null.
     * @param height Рост, может быть null, должен быть больше 0.
     * @param location Место нахождения, не должно быть null.
     */
    public Person(String name, LocalDateTime birthday, Integer height, Location location) {
        this.name = name;
        this.birthday = birthday;
        this.height = height;
        this.location = location;
    }
    // Геттеры и сеттеры для каждого поля класса
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    /**
     * Метод для проверки корректности значений всех полей класса.
     * Выводит сообщение об ошибке, если какое-либо поле не удовлетворяет условиям.
     *
     * @return true, если все поля корректны, иначе false.
     */
    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()){
            System.out.println("\u001B[31mError: имя не должно быть null и не должно быть пустым \u001B[0m");
            return false;
        }
        if (birthday == null){
            //System.out.println("\u001B[31mError: День рождения не должно быть null \u001B[0m");
            return false;
        }
        if (height == null || height <= 0){
            System.out.println("\u001B[31mError: Рост не должен быть null и должен быть больше 0 \u001B[0m");
            return false;
        }
        if (location == null){
            System.out.println("\u001B[31mError: Место рождения не должно быть null \u001B[0m");
            return false;
        }
        return true;
    }
}
