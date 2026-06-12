package Interfaces;

public interface Observable {
    String observe(String s);
    String detectAtmosphere(String s);  // Обнаружение наличия атмосферы
    String perceiveAirQuality(String s);  // Оценка качества воздуха
}