package gettingStarted.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EmployeePage {

    WebDriver driver;
    WebDriverWait wait;

    private By employeesLink = By.cssSelector("a[href='/Employee']");
    private By newEmployeeLink = By.xpath("//*[contains(text(),'New Employee')]");
    private By nameField = By.name("Name");
    private By ageField = By.name("Age");
    private By durationWorkedField = By.name("DurationWorked");
    private By emailField = By.name("Email");
    private By gradeDropdown = By.id("Grade");
    private By salaryField = By.name("Salary");
    private By submitButton = By.cssSelector("button.btn-submit");

    public EmployeePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void createEmployee(String name, String age, String duration,
                                String email, String grade, String salary) {
        wait.until(ExpectedConditions.elementToBeClickable(employeesLink)).click();
        wait.until(ExpectedConditions.elementToBeClickable(newEmployeeLink)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField))
                .sendKeys(name);
        driver.findElement(ageField).sendKeys(age);
        driver.findElement(durationWorkedField).sendKeys(duration);
        driver.findElement(emailField).sendKeys(email);
        new Select(driver.findElement(gradeDropdown)).selectByVisibleText(grade);
        driver.findElement(salaryField).sendKeys(salary);

        clickSubmit();
    }

    private void clickSubmit() {
        WebElement btn = driver.findElement(submitButton);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", btn);
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}