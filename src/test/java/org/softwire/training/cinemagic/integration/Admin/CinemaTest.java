package org.softwire.training.cinemagic.integration.Admin;

import com.google.common.base.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
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
public class CinemaTest {

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
    public void createNewCinema() {
        logIn();
        driver.findElement(By.id("admin-link-cinemas")).click();
        waitForElement(By.id("cinemas-form-submit-button"));
        driver.findElement(By.id("cinemas-form-name-field")).sendKeys("Another Test Cinema");
        driver.findElement(By.id("cinemas-form-submit-button")).click();
        WebElement cinemaNameElement = shortWait().until((Function<? super WebDriver, WebElement>) webDriver ->
                webDriver.findElement(cinemaDetailsXPath("Another Test Cinema")));
        assertThat(cinemaNameElement.getText(), equalTo("Another Test Cinema"));
        // Is there a better assertion to use here? If the element does not exist the test will fail while waiting in cinemaNameElement before the assertion runs.
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

    private By cinemaDetailsXPath(String filmName) {
        return By.xpath("//h3[@class=\"cinema-details-name\"][text()=\"" + filmName + "\"]");
    }

}
