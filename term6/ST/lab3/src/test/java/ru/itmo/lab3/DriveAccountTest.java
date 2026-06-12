package ru.itmo.lab3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DriveAccountTest extends BaseTest {

    @ParameterizedTest(name = "[{0}] виден аватар аккаунта (мы залогинены)")
    @ValueSource(strings = {"chrome", "firefox"})
    void shouldShowAccountAvatar(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        WebElement account = waitVisible(
                "//*[contains(@aria-label, 'Аккаунт Google')]");
        assertTrue(account.isDisplayed(), "Аватар аккаунта должен быть виден");
    }
}
