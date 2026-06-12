package ru.itmo.lab3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DriveCreateTest extends BaseTest {

    private static final String CREATE_BTN_XPATH = "//button[normalize-space(.)='Создать']";

    @ParameterizedTest(name = "[{0}] клик 'Создать' открывает меню с пунктом 'Папка'")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldOpenCreateMenu(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        WebElement create = waitClickable(CREATE_BTN_XPATH);
        create.click();

        WebElement folderItem = waitVisible(
                "//*[@role='menuitem' and contains(., 'апк')]");
        assertTrue(folderItem.isDisplayed(),
                "После клика 'Создать' должен появиться пункт про папку");
    }
}
