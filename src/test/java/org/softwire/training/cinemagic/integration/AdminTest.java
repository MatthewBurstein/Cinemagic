package org.softwire.training.cinemagic.integration;

import com.google.common.base.Function;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.softwire.training.cinemagic.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "CreateTestData.sql")
})
public class AdminTest {

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    @Test
    public void testTitle() {
        navigateToAdminLogin();
        assertThat("Cinemagic", equalTo(driver.getTitle()));
    }

    @Test
    public void createNewFilm() {
        logIn();
        System.out.println("FILM LIST BEFORE INSERTION");
        waitForElement(By.id("films-form-name-field"));
        driver.findElement(By.id("films-form-name-field")).click();
        shortWait();
        driver.findElement(By.id("films-form-name-field")).sendKeys("Another Test Film");
        shortWait();
        driver.findElement(By.id("films-form-length-minutes-field")).click();
        shortWait();
        driver.findElement(By.id("films-form-length-minutes-field")).sendKeys("120");
        shortWait();
        String body = driver.findElement(By.tagName("body")).getText();
        System.out.println("===BODY===");
        System.out.println(body);
        driver.findElement(By.id("films-form-submit-button")).click();
        shortWait();
        System.out.println("===BODY===");
        System.out.println(driver.findElement(By.tagName("body")).getText());
        String filmTable = driver.findElement(By.tagName("table")).getText();
        System.out.println("===FILMTABLE===");
        System.out.println(filmTable);
        System.out.println("FILM LIST AFTER INSERTION");
        assertThat("film is added correctly", filmTable.contains("Another Test Film"));
    }

    @Test
    public void createNewCinema() {
        logIn();
        driver.findElement(By.id("admin-link-cinemas")).click();
        waitForElement(By.id("cinemas-form-submit-button"));
        driver.findElement(By.id("cinemas-form-name-field")).sendKeys("Another Test Cinema");
        driver.findElement(By.id("cinemas-form-submit-button")).click();
        shortWait();
        System.out.println("===BODY===");
        System.out.println(driver.findElement(By.tagName("body")));
        List<WebElement> cinemas = driver.findElements(By.className("cinemas-title"));
        List<String> cinemaNames = new ArrayList<>();
        cinemas.forEach(cinema -> {
            if (cinema.getText().equals("Another Test Cinema")) {
                cinemaNames.add(cinema.getText());
            }
        });
        assertThat("Exactly one cinema is created",cinemaNames.size() == 1);
    }

    @Test
    public void createNewScreen() {
        logIn();

    }

    private void logIn() {
        navigateToAdminLogin();
        driver.findElement(By.id("login-form-username-field")).sendKeys("admin");
        driver.findElement(By.id("login-form-password-field")).sendKeys("admin");
        driver.findElement(By.id("login-form-submit-button")).click();
    }


    private void navigateToAdminLogin() {
        driver.get("http://localhost:" + port + "/admin");
    }

    private void selectCinemaAndContinue(String cinemaName) {
        new Select(driver.findElement(By.tagName("select"))).selectByVisibleText(cinemaName);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    private void selectSeatAndContinue() {
        driver.findElement(By.xpath("//button[text()='U']")).click();
        driver.findElement(By.id("seat-select-book-button")).click();
    }

    @SuppressWarnings("ConstantConditions")
    private WebElement waitForElement(By locator) {
        return shortWait().until((Function<? super WebDriver, WebElement>) webDriver -> webDriver.findElement(locator));
    }

    private WebDriverWait shortWait() {
        return new WebDriverWait(driver, 10, 100);
    }
}
