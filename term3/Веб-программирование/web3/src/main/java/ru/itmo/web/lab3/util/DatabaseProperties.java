package ru.itmo.web.lab3.util;

import lombok.extern.java.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Утилита для загрузки свойств базы данных из файла конфигурации.
 */
@Log
public class DatabaseProperties {
    private static final Properties properties = new Properties();
    private static boolean initialized = false;

    /**
     * Инициализирует свойства базы данных из файла `database.properties`.
     */
    public static void init() {
        if (!initialized) {
            try (InputStream input = DatabaseProperties.class.getClassLoader()
                    .getResourceAsStream("db/database.properties")) {
                if (input == null) {
                    throw new IOException("Unable to find database.properties");
                }
                properties.load(input);
                initialized = true;
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error loading database properties", e);
            }
        }
    }

    /**
     * Получает URL базы данных.
     *
     * @return строка URL базы данных
     */
    public static String getUrl() {
        init();
        return properties.getProperty("db.url");
    }

    /**
     * Получает имя пользователя для подключения к базе данных.
     *
     * @return строка имени пользователя
     */
    public static String getUsername() {
        init();
        return properties.getProperty("db.username");
    }

    /**
     * Получает пароль для подключения к базе данных.
     *
     * @return строка пароля
     */
    public static String getPassword() {
        init();
        return properties.getProperty("db.password");
    }
}