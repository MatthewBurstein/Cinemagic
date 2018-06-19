package org.softwire.training.cinemagic.integration.Admin;

import com.google.common.base.Function;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../CreateTestData.sql")
})

public class FilmTest {
    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    @Autowired
    FilmService filmService;

    @Test
    public void createNewFilm() {
        logIn();
        waitForElement(By.id("films-form-name-field"));
        driver.findElement(By.id("films-form-name-field")).clear();
        driver.findElement(By.id("films-form-name-field")).sendKeys("Another Test Film");
        driver.findElement(By.id("films-form-length-minutes-field")).clear();
        driver.findElement(By.id("films-form-length-minutes-field")).sendKeys("120");
        String body = driver.findElement(By.tagName("body")).getText();
        driver.findElement(By.id("films-form-submit-button")).click();
        shortWait();
        WebElement nameElement = shortWait().until((Function<? super WebDriver, WebElement>) webDriver ->
                webDriver.findElement(filmDetailsXPath("Another Test Film")));

        String filmTable = driver.findElement(By.tagName("table")).getText();
        WebElement rowElement = nameElement.findElement(By.xpath("./.."));
        WebElement lengthMinutesElement = rowElement.findElement(By.className("films-details-length-minutes"));
        assertThat(lengthMinutesElement.getText(), Matchers.equalTo("120"));
        assertThat("film is added correctly", filmTable.contains("Another Test Film"));
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

    @SuppressWarnings("ConstantConditions")
    private WebElement waitForElement(By locator) {
        return shortWait().until((Function<? super WebDriver, WebElement>) webDriver -> webDriver.findElement(locator));
    }

    private WebDriverWait shortWait() {
        return new WebDriverWait(driver, 10, 100);
    }


    private By filmDetailsXPath(String filmName) {
        return By.xpath("//td[@class=\"films-details-name\"][text()=\"" + filmName + "\"]");
    }
}
