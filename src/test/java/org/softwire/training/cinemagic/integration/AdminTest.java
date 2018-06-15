package org.softwire.training.cinemagic.integration;

import com.google.common.base.Function;
import org.junit.Rule;
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
    public void logIn() {
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
