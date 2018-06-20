package org.softwire.training.cinemagic.integration.Admin;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
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
public class ScreenTest {

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
    public void createNewScreen() {
        loginManager.adminLogin();
        webInteractor.clickById("admin-link-cinemas");
        System.out.println(webInteractor.findByTagName("body").getText());
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        waitManager.shortWait();
        System.out.println("====AFTER====");
        System.out.println(webInteractor.findByTagName("body").getText());
        waitManager.waitForId("screen-form-name-field");
        webInteractor.fillFieldById("screen-form-name-field", "Screen 2");
        webInteractor.fillFieldById("screen-form-rows-field", "8");
        webInteractor.fillFieldById("screen-form-row-width-field", "7");
    }

    private void selectCinemaAndContinue(String cinemaName) {
        new Select(driver.findElement(By.tagName("select"))).selectByVisibleText(cinemaName);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }
}