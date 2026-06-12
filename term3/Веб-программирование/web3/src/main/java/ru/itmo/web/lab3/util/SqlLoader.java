package ru.itmo.web.lab3.util;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;


/**
 * Утилита для загрузки SQL-запросов из файла.
 */
@Log
public class SqlLoader {
    private static final Map<String, String> sqlQueries = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Инициализирует SQL-запросы из файла `queries.sql`.
     */
    public static void init() {
        if (!initialized) {
            try (InputStream input = SqlLoader.class.getClassLoader()
                    .getResourceAsStream("db/queries.sql")) {
                if (input == null) {
                    throw new IOException("Unable to find queries.sql");
                }

                String content = new BufferedReader(new InputStreamReader(input))
                        .lines().collect(Collectors.joining("\n"));

                String[] queries = content.split("--");
                for (String query : queries) {
                    if (query.trim().isEmpty()) continue;

                    String[] parts = query.trim().split("\n", 2);
                    if (parts.length == 2) {
                        String queryName = parts[0].trim();
                        String queryContent = parts[1].trim();
                        sqlQueries.put(queryName, queryContent);
                    }
                }

                initialized = true;
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error loading SQL queries", e);
            }
        }
    }

    /**
     * Получает SQL-запрос по его имени.
     *
     * @param queryName имя запроса
     * @return строка SQL-запроса
     */
    public static String getQuery(String queryName) {
        init();
        return sqlQueries.get(queryName);
    }
}