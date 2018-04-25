package org.softwire.training.cinemagic.integration.pages.booking;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.softwire.training.cinemagic.integration.IntegrationTestSupport;
import org.softwire.training.cinemagic.integration.pages.AbstractPage;

import java.text.MessageFormat;

public class FilmSelectionPage extends AbstractPage {
    public FilmSelectionPage(WebDriver driver, IntegrationTestSupport support) {
        super(driver, support);
    }

    @Override
    public void waitForPageLoad() {
        support.waitForElement(By.className("film-time-select"));
    }

    public void selectFilm(String filmName, String time) {
        driver.findElements(By.xpath("//button[@class=\"film-selection-button\"]"))
                .stream()
                .filter(webElement -> webElement.getText().contains(filmName) && webElement.getText().contains(time))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(MessageFormat.format(
                        "No film with name {0} and time {1}{2}", filmName, time, " found")))
                .click();
    }
}
