package org.softwire.training.cinemagic.integration.helpers;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitManager {

    private WebDriver driver;

    public WaitManager(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement waitForId(String id) {
        return waitForElement(By.id(id));
    }

    public WebElement waitForXpath(String xpath) {
        return waitForElement(By.xpath(xpath));
    }

    public WebElement waitForClass(String className) { return waitForElement(By.className(className)); }

    @SuppressWarnings("ConstantConditions")
    private WebElement waitForElement(By locator) {
        return shortWait().until((Function<? super WebDriver, WebElement>) webDriver -> webDriver.findElement(locator));
    }

    public WebDriverWait shortWait() {
        return new WebDriverWait(driver, 10, 100);
    }

}
