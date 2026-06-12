package ru.itmo.lab3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DriveFolderTest extends BaseTest {

    private static final String CREATE_BTN_XPATH = "//button[normalize-space(.)='Создать']";
    private static final String FOLDER_MENUITEM_XPATH = "//*[@role='menuitem' and contains(., 'апк')]";
    private static final String FOLDER_NAME_INPUT_XPATH = "//input[@aria-label='Новая папка']";
    private static final String DIALOG_CREATE_BTN_XPATH = "//*[@role='dialog']//button[normalize-space(.)='Создать']";
    private static final String RENAME_MENUITEM_XPATH = "//*[@role='menuitem' and contains(., 'Переименовать')]";
    private static final String TRASH_MENUITEM_XPATH = "//*[@role='menuitem' and contains(., 'Отправить в корзину')]";
    private static final String RENAME_INPUT_XPATH = "//input[@type='text' and not(@name='q')]";
    private static final String DIALOG_OK_BTN_XPATH = "//button[normalize-space(.)='ОК']";
    private static final String ANY_ITEM_IN_MAIN_XPATH = "//*[@role='main']//strong[normalize-space(.)!='']";

    @AfterAll
    void cleanupAllFolders() {
        setUpBrowser("chrome");
        try {
            loadDriveSession();
            driver.get("https://drive.google.com/drive/my-drive");
            driver.navigate().refresh();

            try {
                new WebDriverWait(driver, Duration.ofSeconds(30))
                        .until(d -> !d.findElements(By.xpath(ANY_ITEM_IN_MAIN_XPATH)).isEmpty());
            } catch (Exception nothingToClean) {
                return;
            }

            for (int safetyLimit = 0; safetyLimit < 30; safetyLimit++) {
                var items = driver.findElements(By.xpath(ANY_ITEM_IN_MAIN_XPATH));
                if (items.isEmpty())
                    break;
                try {
                    new Actions(driver).contextClick(items.get(0)).perform();
                    waitClickable(TRASH_MENUITEM_XPATH).click();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ignored) {
                    }
                } catch (Exception skipOne) {
                }
            }
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }

    @ParameterizedTest(name = "[{0}] папку можно создать")
    @ValueSource(strings = { "chrome", "firefox" })
    void shouldCreateFolder(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String name = "lab3-create-" + System.currentTimeMillis();
        createFolder(name);

        WebElement folder = findFolder(name);
        assertTrue(folder.isDisplayed(), "Папка должна появиться в списке");
    }

    @ParameterizedTest(name = "[{0}] папку можно переименовать")
    @ValueSource(strings = { "chrome", "firefox" })
    void shouldRenameFolder(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String oldName = "lab3-rename-" + System.currentTimeMillis();
        String newName = oldName + "-new";
        createFolder(oldName);

        rightClickMenu(oldName, RENAME_MENUITEM_XPATH);

        WebElement input = wait.until(d -> {
            var candidates = d.findElements(By.xpath(RENAME_INPUT_XPATH));
            for (WebElement el : candidates) {
                if (el.isDisplayed() && !el.getDomProperty("value").isEmpty()) {
                    return el;
                }
            }
            return null;
        });
        selectAll(input);
        input.sendKeys(newName);
        waitClickable(DIALOG_OK_BTN_XPATH).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath(RENAME_INPUT_XPATH)));
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        WebElement renamed = findFolder(newName);
        assertTrue(renamed.isDisplayed(), "Папка с новым именем должна появиться");
    }

    @ParameterizedTest(name = "[{0}] папку можно удалить (отправить в корзину)")
    @ValueSource(strings = { "chrome", "firefox" })
    void shouldDeleteFolder(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String name = "lab3-delete-" + System.currentTimeMillis();
        createFolder(name);

        rightClickMenu(name, TRASH_MENUITEM_XPATH);
        wait.until(d -> d.findElements(By.xpath(folderXpath(name))).isEmpty());
    }

    @ParameterizedTest(name = "[{0}] двойной клик по папке открывает её содержимое")
    @ValueSource(strings = { "chrome", "firefox" })
    void shouldNavigateIntoFolder(String browser) {
        setUpBrowser(browser);
        loadDriveSession();

        String name = "lab3-nav-" + System.currentTimeMillis();
        createFolder(name);

        WebElement folder = findFolder(name);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", folder);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        new Actions(driver).doubleClick(folder).perform();

        wait.until(d -> d.getCurrentUrl().contains("/drive/folders/"));
        assertTrue(driver.getCurrentUrl().contains("/drive/folders/"),
                "URL должен содержать /drive/folders/");

        driver.navigate().back();
        wait.until(d -> !d.getCurrentUrl().contains("/drive/folders/"));
    }

    private void createFolder(String name) {
        waitClickable(CREATE_BTN_XPATH).click();
        waitClickable(FOLDER_MENUITEM_XPATH).click();

        WebElement input = waitVisible(FOLDER_NAME_INPUT_XPATH);
        selectAll(input);
        input.sendKeys(name);
        waitClickable(DIALOG_CREATE_BTN_XPATH).click();

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.findElements(By.xpath(folderXpath(name))).isEmpty());
    }

    private WebElement findFolder(String name) {
        return waitVisible(folderXpath(name));
    }

    private void selectAll(WebElement input) {
        input.sendKeys(Keys.chord(osModifier(), "a"));
    }

    private void rightClickMenu(String folderName, String menuItemXpath) {
        WebElement folder = findFolder(folderName);
        new Actions(driver).contextClick(folder).perform();
        waitClickable(menuItemXpath).click();
    }

    private static Keys osModifier() {
        return System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.COMMAND
                : Keys.CONTROL;
    }

    private static String folderXpath(String name) {
        return "//strong[normalize-space(.)='" + name + "']";
    }
}
