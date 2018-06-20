package org.softwire.training.cinemagic.integration.Admin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.softwire.training.cinemagic.integration.helpers.WaitManager;
import org.softwire.training.cinemagic.integration.helpers.WebInteractor;
import org.softwire.training.cinemagic.models.Cinema;
import org.softwire.training.cinemagic.services.CinemaService;
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
public class ScreenTest {

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected WebDriver driver;

    @Autowired
    public CinemaService cinemaService;

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
    public void createNewScreen() {
        String testScreenName = "Test Screen";
        String testScreenRows = "8";
        String testScreenRowsWidth = "7";
        loginManager.adminLogin();
        webInteractor.clickById("admin-link-cinemas");
        waitManager.waitForClass("screen-form-name-field");
        completeNewScreenForm(testScreenName, testScreenRows, testScreenRowsWidth);
        WebElement screenNameElement = findScreenName(testScreenName);
        WebElement screenRowsElement = findScreenRows(testScreenRows);
        WebElement screenRowsWidthElement = findScreenRowsWidth(testScreenRowsWidth);
        assertThat(screenNameElement.getText(), equalTo(testScreenName));
        assertThat(screenRowsElement.getText(), equalTo(testScreenRows));
        assertThat(screenRowsWidthElement.getText(), equalTo(testScreenRowsWidth));
    }

    private void completeNewScreenForm(String testScreenName, String testScreenRows, String testScreenRowsWidth) {
        webInteractor.fillFieldByClass("screen-form-name-field", testScreenName);
        webInteractor.fillFieldByClass("screen-form-rows-field", testScreenRows);
        webInteractor.fillFieldByClass("screen-form-row-width-field", testScreenRowsWidth);
        webInteractor.clickByClass("screen-form-submit-button");
    }

    private WebElement findScreenName(String screenName) {
        WebElement screenNameElement = waitManager.waitForXpath("//td[@class=\"screen-detail-name\"][text()=\"" + screenName + "\"]");
        return screenNameElement;
    }

    private WebElement findScreenRows(String screenRows) {
        WebElement screenRowsElement = waitManager.waitForXpath("//td[@class=\"screen-detail-rows\"][text()=\"" + screenRows + "\"]");
        return screenRowsElement;
    }

    private WebElement findScreenRowsWidth(String screenRowsWidth) {
        WebElement screenRowsWidthElement = waitManager.waitForXpath("//td[@class=\"screen-detail-row-width\"][text()=\"" + screenRowsWidth+ "\"]");
        return screenRowsWidthElement;
    }

    private WebElement findCinemaDiv(String cinemaName) {
        WebElement cinemaNameElement = waitManager.waitForXpath("//h3[@class=\"cinema-details-name\"][text()=\"" + cinemaName + "\"]");
        return cinemaNameElement.findElement(By.xpath("./.."));
    }
}