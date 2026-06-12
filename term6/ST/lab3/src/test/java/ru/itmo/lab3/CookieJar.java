package ru.itmo.lab3;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

final class CookieJar {

    static final Path FILE =
            Paths.get(System.getProperty("user.home"), ".lab3-drive-session");

    private CookieJar() {}

    static void save(WebDriver driver) throws IOException {
        Set<Cookie> cookies = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie c : cookies) {
            sb.append(safe(c.getName())).append('\t')
              .append(safe(c.getValue())).append('\t')
              .append(safe(c.getDomain())).append('\t')
              .append(safe(c.getPath())).append('\t')
              .append(c.getExpiry() == null ? "" : c.getExpiry().getTime())
              .append('\t').append(c.isSecure())
              .append('\t').append(c.isHttpOnly())
              .append('\n');
        }
        Files.writeString(FILE, sb.toString());
    }

    static List<Cookie> load() throws IOException {
        if (!Files.exists(FILE)) return List.of();

        List<Cookie> result = new ArrayList<>();
        for (String line : Files.readAllLines(FILE)) {
            if (line.isBlank()) continue;
            String[] parts = line.split("\t", -1);
            if (parts.length < 7) continue;

            String name      = parts[0];
            String value     = parts[1];
            String domain    = parts[2];
            String path      = parts[3];
            Date   expiry    = parts[4].isEmpty() ? null : new Date(Long.parseLong(parts[4]));
            boolean isSecure = Boolean.parseBoolean(parts[5]);
            boolean httpOnly = Boolean.parseBoolean(parts[6]);

            result.add(new Cookie.Builder(name, value)
                    .domain(domain)
                    .path(path)
                    .expiresOn(expiry)
                    .isSecure(isSecure)
                    .isHttpOnly(httpOnly)
                    .build());
        }
        return result;
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace('\t', ' ').replace('\n', ' ');
    }
}
