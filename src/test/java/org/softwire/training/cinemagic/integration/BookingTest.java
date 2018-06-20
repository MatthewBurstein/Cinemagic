package org.softwire.training.cinemagic.integration;

import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.softwire.training.cinemagic.integration.Admin.LoginManager;
import org.softwire.training.cinemagic.integration.helpers.WaitManager;
import org.softwire.training.cinemagic.integration.helpers.WebInteractor;
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

    private WebInteractor webInteractor;
    private WaitManager waitManager;
    private LoginManager loginManager;

    @Before
    public void createInteractor() {
        this.webInteractor = new WebInteractor(driver);
        this.waitManager = new WaitManager(driver);
        this.loginManager = new LoginManager(driver, port);
    }

    @Test
    public void testTitle() {
        loginManager.userLogin();
        assertThat("Cinemagic", equalTo(driver.getTitle()));
    }

    @Test
    public void testMainlineBookingFlow() {
        String cinemaName = "Test Cinema";
        loginManager.userLogin();

        waitManager.waitForClass("cinema-select");

        selectCinemaAndContinue(cinemaName);
        waitManager.waitForClass("film-time-select");

        webInteractor.clickByClass("film-selection-button");
        waitManager.waitForClass("seat-select");

        selectSeatAndContinue();
        waitManager.waitForClass("booking-success");

        WebElement element = webInteractor.findByClassName("booking-success");
        assertThat("Confirms booking", element.getText().contains("Booking Successful!"));
    }

    @Test
    public void testWhenNoCinemaChosen_RemainsOnCinemaSelection() {
        loginManager.userLogin();
        waitManager.waitForClass("cinema-select");
        webInteractor.clickByXpath("//button[@type='submit']");
        String chooseCinemaMessage = webInteractor.findByClassName("cinema-select").getText();
        assertThat("remains on cinema selection screen", chooseCinemaMessage.contains("Choose a Cinema"));

    }

    @Test
    public void testWhenNoSeatsSelected_DisplaysErrorMessage() {
        String cinemaName = "Test Cinema";
        loginManager.userLogin();
        waitManager.waitForClass("cinema-select");

        selectCinemaAndContinue(cinemaName);
        waitManager.waitForClass("film-time-select");

        webInteractor.clickByClass("film-selection-button");
        waitManager.waitForClass("seat-select");

        webInteractor.clickById("seat-select-book-button");
        String errorMessage = webInteractor.findByClassName("errorMessage").getText();
        String expectedMessage = "You must select at least one seat";
        assertThat("displays error when no seat selected", errorMessage.contains(expectedMessage));
    }

    private void selectCinemaAndContinue(String cinemaName) {
        new Select(driver.findElement(By.tagName("select"))).selectByVisibleText(cinemaName);
        webInteractor.clickByXpath("//button[@type='submit']");
    }

    private void selectSeatAndContinue() {
        webInteractor.clickByXpath("//button[text()='U']");
        webInteractor.clickById("seat-select-book-button");
    }
}
