package model;

import java.io.Serializable;
/**
 * Перечисление, представляющее уровни сложности.
 * Содержит три уровня сложности: HARD, IMPOSSIBLE, и INSANE.
 */
public enum Difficulty implements Serializable {
    HARD,
    IMPOSSIBLE,
    INSANE;
    /**
     * Возвращает константу сложности по её строковому представлению.
     * Этот метод учитывает регистр символов.
     *
     * @param value строковое представление уровня сложности.
     * @return соответствующая константа сложности, если она найдена; иначе null.
     */
    public static Difficulty findByValue(String value) {
        for (Difficulty difficulty : values()) {
            if (difficulty.name().equals(value)) {
                return difficulty;
            }
        }
        return null;
    }
}
