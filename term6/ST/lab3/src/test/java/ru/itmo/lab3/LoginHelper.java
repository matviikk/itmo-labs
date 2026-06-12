package ru.itmo.lab3;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

final class LoginHelper {

    private static final String SIGNIN_URL =
            "https://accounts.google.com/signin/v2/identifier"
                    + "?service=wise&continue=https%3A%2F%2Fdrive.google.com%2F";

    private LoginHelper() {}

    static void login(WebDriver driver, WebDriverWait wait) {
        String email = required("DRIVE_EMAIL");
        String password = required("DRIVE_PASSWORD");

        driver.get(SIGNIN_URL);

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='email' and @name='identifier']")));
        emailInput.sendKeys(email);
        emailInput.sendKeys(Keys.RETURN);

        WebElement pwInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='password']")));
        sleep(500);
        pwInput.sendKeys(password);
        pwInput.sendKeys(Keys.RETURN);

        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                d.getCurrentUrl().contains("drive.google.com/drive")
                        || d.getCurrentUrl().contains("recoveryoptions"));

        if (driver.getCurrentUrl().contains("recoveryoptions")) {
            dismissRecoveryNag(driver, wait);
        }

        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d ->
                d.getCurrentUrl().contains("drive.google.com/drive"));
    }

    private static void dismissRecoveryNag(WebDriver driver, WebDriverWait wait) {
        WebElement skip = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//*[self::button or @role='button']"
                        + "[contains(translate(., 'НЕСЕЙЧАСПОЗЖЕПРОПУСТИТЬSKIPLATERNOW',"
                        + "'несейчаспозжепропуститьskiplaternow'), 'не сейчас')"
                        + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'not now')"
                        + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'later')"
                        + " or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'skip')]"
        )));
        skip.click();
    }

    private static String required(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                    "Set environment variable " + name + " before running tests");
        }
        return v;
    }

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}
