package ru.itmo.lab3;

import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseTest {

    protected static final Duration WAIT = Duration.ofSeconds(15);

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected void setUpBrowser(String browser) {
        boolean headless = "true".equalsIgnoreCase(System.getProperty("headless", "false"));

        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            if (headless)
                options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            driver = new ChromeDriver(options);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            if (headless)
                options.addArguments("-headless");
            driver = new FirefoxDriver(options);
        } else {
            throw new IllegalArgumentException("Unknown browser: " + browser);
        }

        driver.manage().window().setSize(new Dimension(1280, 900));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, WAIT);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebElement waitVisible(String xpath) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }

    protected WebElement waitClickable(String xpath) {
        return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    }

    protected void loginToDrive() {
        LoginHelper.login(driver, wait);
    }

    protected void loadDriveSession() {
        try {
            driver.get("https://drive.google.com/robots.txt");

            for (var cookie : CookieJar.load()) {
                try {
                    driver.manage().addCookie(cookie);
                } catch (Exception ignore) {
                }
            }

            driver.get("https://drive.google.com/");
            wait.until(d -> d.getCurrentUrl().contains("drive.google.com/drive"));
        } catch (java.io.IOException e) {
            throw new RuntimeException(
                    "Не удалось прочитать сохранённые cookies. "
                            + "Запусти один раз `mvn test -Dtest=SaveDriveSession` "
                            + "(нужны DRIVE_EMAIL/DRIVE_PASSWORD).",
                    e);
        }
    }
}
