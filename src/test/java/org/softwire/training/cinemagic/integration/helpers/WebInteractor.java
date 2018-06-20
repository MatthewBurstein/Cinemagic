package org.softwire.training.cinemagic.integration.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebInteractor {

    private final WebDriver driver;

    public WebInteractor(WebDriver webDriver) {
        this.driver = webDriver;
    }

    public void fillFieldById(String id, CharSequence text) {
        findById(id).clear();
        findById(id).sendKeys(text);
    }

    public void fillFieldByClass(String className, CharSequence text) {
        findByClassName(className).clear();
        findByClassName(className).sendKeys(text);
    }

    public void clickById(String id) {
        findById(id).click();
    }

    public void clickByXpath(String id) {
        findByXpath(id).click();
    }

    public void clickByClass(String className) { findByClassName(className).click(); }

    public WebElement findByClassName(String className) {
        return driver.findElement(By.className(className));
    }

    public WebElement findByXpath(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public WebElement findByTagName(String tagName) {
        return driver.findElement(By.tagName(tagName));
    }

    private WebElement findById(String id) {
        return driver.findElement(By.id(id));
    }
}
