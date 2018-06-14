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
public class BookingTest {
    @Rule
    public final WebDriverLoggingRule webDriverLoggingRule = new WebDriverLoggingRule();

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    @Test
    public void testTitle() {
        navigateToBookingPage();
        assertThat("Cinemagic", equalTo(driver.getTitle()));
    }

    @Test
    public void testMainlineBookingFlow() {
        String cinemaName = "Test Cinema";
        navigateToBookingPage();
        waitForElement(By.className("cinema-select"));

        selectCinemaAndContinue(cinemaName);
        waitForElement(By.className("film-time-select"));

        driver.findElement(By.className("film-selection-button")).click();
        waitForElement(By.className("seat-select"));

        selectSeatAndContinue();
        waitForElement(By.className("booking-success"));

        WebElement element = driver.findElement(By.className("booking-success"));
        assertThat("Confirms booking", element.getText().contains("Booking Successful!"));
    }

    @Test
    public void testWhenNoCinemaChosen_RemainsOnCinemaSelection() {
        navigateToBookingPage();
        waitForElement(By.className("cinema-select"));
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        String chooseCinemaMessage = driver
                .findElement(By.className("cinema-select"))
                .getText();
        System.out.println(chooseCinemaMessage);
        assertThat("remains on cinema selection screen", chooseCinemaMessage.contains("Choose a Cinema"));

    }

    @Test
    public void testWhenNoSeatsSelected_DisplaysErrorMessage() {
        String cinemaName = "Test Cinema";
        navigateToBookingPage();
        waitForElement(By.className("cinema-select"));

        selectCinemaAndContinue(cinemaName);
        waitForElement(By.className("film-time-select"));

        driver.findElement(By.className("film-selection-button")).click();
        waitForElement(By.className("seat-select"));

        driver.findElement(By.id("seat-select-book-button")).click();
        String errorMessage = driver.findElement(By.className("errorMessage")).getText();
        String expectedMessage = "You must select at least one seat";
        assertThat("displays error when no seat selected", errorMessage.contains(expectedMessage));

    }

    private void navigateToBookingPage() {
        driver.get("http://localhost:" + port + "/booking");
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
