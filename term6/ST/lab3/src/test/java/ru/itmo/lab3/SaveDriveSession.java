package ru.itmo.lab3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveDriveSession extends BaseTest {

    @Test
    void saveSession() throws Exception {
        setUpBrowser("chrome");
        loginToDrive();

        assertTrue(driver.getCurrentUrl().contains("drive.google.com/drive"),
                "Логин в Chrome не удался");

        CookieJar.save(driver);
        System.out.println("Cookies сохранены в " + CookieJar.FILE);
    }
}
