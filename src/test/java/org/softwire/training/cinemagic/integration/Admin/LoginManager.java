package org.softwire.training.cinemagic.integration.Admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginManager {

    private final Integer port;
    private WebDriver driver;

    public LoginManager(WebDriver driver, Integer port) {
        this.driver = driver;
        this.port = port;
    }

    public void adminLogin() {
        navigateToAdminLogin();
        driver.findElement(By.id("login-form-username-field")).sendKeys("admin");
        driver.findElement(By.id("login-form-password-field")).sendKeys("admin");
        driver.findElement(By.id("login-form-submit-button")).click();
    }

    public void userLogin() {
        navigateToUserLogin();
    }

    private void navigateToUserLogin() {
        driver.get("http:/localhost:" + port + "/booking");
    }

    private void navigateToAdminLogin() {
        driver.get("http://localhost:" + port + "/admin");
    }

}
