package org.softwire.training.cinemagic.integration.Admin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "../CreateTestData.sql")
})
public class CinemaTest {

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    private WebInteractor webInteractor;
    private WaitManager waitManager;
    private LoginManager loginManager;

    @Before
    public void createHelpers() {
        webInteractor = new WebInteractor(driver);
        waitManager = new WaitManager(driver);
        loginManager = new LoginManager(driver, port);
    }

    @Test
    public void testTitle() {
        loginManager.adminLogin();
        assertThat("Cinemagic", equalTo(driver.getTitle()));
    }

    @Test
    public void createNewCinema() {
        String testCinemaName = "Another Test Cinema";
        loginManager.adminLogin();
        webInteractor.clickById("admin-link-cinemas");
        waitManager.waitForId("cinemas-form-submit-button");
        webInteractor.fillFieldById("cinemas-form-name-field", testCinemaName);
        webInteractor.clickById("cinemas-form-submit-button");
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        webInteractor.findByClassName("cinema-details-name");
        WebElement cinemaNameElement = waitManager.waitForXpath(cinemaDetailsXPath(testCinemaName));
        assertThat(cinemaNameElement.getText(), equalTo(testCinemaName));
        // Is there a better assertion to use here? If the element does not exist the test will fail while waiting in cinemaNameElement before the assertion runs.
    }

    private String cinemaDetailsXPath(String cinemaName) {
        return "//h3[@class=\"cinema-details-name\"][text()=\"" + cinemaName + "\"]";
    }

}
