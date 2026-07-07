package gettingStarted.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LogoutPage {

    WebDriver driver;
    WebDriverWait wait;

    private By logoutButton = By.xpath("//form[@action='/Account/Logout']//button");
    private By loginLink = By.linkText("Login");

    public LogoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
    }

    public boolean isLoggedOut() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                loginLink)).isDisplayed();
    }
}