package ru.itmo.lab3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DriveSearchTest extends BaseTest {

    private static final String SEARCH_XPATH = "//input[@name='q']";

    @ParameterizedTest(name = "[{0}] поле поиска видимо и активно")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldShowSearchField(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        WebElement search = waitVisible(SEARCH_XPATH);
        assertTrue(search.isDisplayed(), "Поле поиска должно быть видно");
        assertTrue(search.isEnabled(), "Поле поиска должно быть активным");
    }

    @ParameterizedTest(name = "[{0}] в поле поиска можно ввести текст")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldAcceptSearchInput(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        WebElement search = waitVisible(SEARCH_XPATH);
        search.click();
        search.sendKeys("отчёт");

        assertEquals("отчёт", search.getDomProperty("value"),
                "В поле поиска должно быть введённое слово");
    }
}
