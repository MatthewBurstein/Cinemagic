package org.softwire.training.cinemagic.integration.Admin;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.softwire.training.cinemagic.integration.helpers.WaitManager;
import org.softwire.training.cinemagic.integration.helpers.WebInteractor;
import org.softwire.training.cinemagic.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

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

    private WebInteractor webInteractor;
    private WaitManager waitManager;
    private LoginManager logInManager;

    @Before
    public void createHelpers() {
        webInteractor = new WebInteractor(driver);
        waitManager = new WaitManager(driver);
        logInManager = new LoginManager(driver, port);
    }

    @Test
    public void createNewFilm() {
        logInManager.adminLogin();
        waitManager.waitForId("films-form-name-field");
        webInteractor.fillFieldById("films-form-name-field", "Another Test Film");
        webInteractor.fillFieldById("films-form-length-minutes-field", "120");
        webInteractor.clickById("films-form-submit-button");
        waitManager.shortWait();
        WebElement nameElement = waitManager.waitForXpath(filmDetailsXPath("Another Test Film"));
        WebElement rowElement = nameElement.findElement(By.xpath("./.."));
        WebElement lengthMinutesElement = rowElement.findElement(By.className("films-details-length-minutes"));
        String filmTable = webInteractor.findByTagName("table").getText();
        assertThat(lengthMinutesElement.getText(), Matchers.equalTo("120"));
        assertThat("film is added correctly", filmTable.contains("Another Test Film"));
    }

    private String filmDetailsXPath(String filmName) {
        return "//td[@class=\"films-details-name\"][text()=\"" + filmName + "\"]";
    }
}
