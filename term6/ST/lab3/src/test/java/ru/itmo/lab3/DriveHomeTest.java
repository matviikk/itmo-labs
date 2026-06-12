package ru.itmo.lab3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DriveHomeTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] Drive открывается, URL = drive.google.com/drive/...")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldOpenDriveHome(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String url = driver.getCurrentUrl();
        assertTrue(url.contains("drive.google.com/drive"),
                "Должны попасть в реальный Drive, а получили: " + url);
    }

    @ParameterizedTest(name = "[{0}] заголовок страницы содержит 'Google Диск'")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldHaveGoogleDriveInTitle(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String title = driver.getTitle();
        assertTrue(title != null && title.contains("Google Диск"),
                "В title ожидаем 'Google Диск', получили: " + title);
    }

    @ParameterizedTest(name = "[{0}] на странице видна кнопка 'Создать'")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldShowCreateButton(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        WebElement create = waitVisible("//button[normalize-space(.)='Создать']");
        assertTrue(create.isDisplayed(), "Кнопка 'Создать' должна быть видна");
    }
}
