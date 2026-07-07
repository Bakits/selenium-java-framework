package gettingStarted.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import gettingStarted.pages.EmployeePage;
import gettingStarted.pages.LoginPage;
import gettingStarted.pages.LogoutPage;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.annotations.Parameters;
import java.io.File;
import java.io.IOException;
import java.time.Duration;


public class SeleniumTest {

    WebDriver driver;
    WebDriverWait wait;
    ExtentReports extent;
    ExtentTest test;

    LoginPage loginPage;
    EmployeePage employeePage;
    LogoutPage logoutPage;

    @Parameters("browser")
    @BeforeClass
    public void setUp(String browser) {
        ExtentSparkReporter spark = new ExtentSparkReporter("extent-report-" + browser + ".html");
        spark.config().setReportName("EA App Test Report - " + browser);
        spark.config().setDocumentTitle("Selenium TestNG Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "Bakita");
        extent.setSystemInfo("Browser", browser);
        extent.setSystemInfo("App", "eaapp.somee.com");

        if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else if (browser.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.navigate().to("http://eaapp.somee.com/");

        loginPage = new LoginPage(driver, wait);
        employeePage = new EmployeePage(driver, wait);
        logoutPage = new LogoutPage(driver, wait);
    }

    @Test(priority = 1, dataProvider = "loginData", dataProviderClass = TestDataProvider.class)
    public void login(String username, String password, boolean expectedResult) {
        test = extent.createTest("Login Test - " + username);
        test.log(Status.INFO, "Attempting login with username: " + username);

        loginPage.login(username, password);
        test.log(Status.INFO, "Submitted credentials");

        boolean isLoggedIn;
        try {
            isLoggedIn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(),'Employees')]"))).isDisplayed();
        } catch (Exception e) {
            isLoggedIn = false;
        }

        Assert.assertEquals(isLoggedIn, expectedResult,
                "Login result mismatch for user: " + username);

        if (isLoggedIn) {
            test.pass("Login successful as expected");
            logoutPage.logout();
        } else {
            test.pass("Login failed as expected");
            driver.navigate().to("http://eaapp.somee.com/");
        }
    }                                                                                                                                 

    @Test(priority = 2, dependsOnMethods = "login")
    public void createUser() {
        test = extent.createTest("Create User Test");
        test.log(Status.INFO, "Navigating to Create Employee page");

        loginPage.login("admin", "password");

        long timestamp = System.currentTimeMillis();
        employeePage.createEmployee(
                "AutoUser" + timestamp,
                "30",
                "40",
                "AutoUser" + timestamp + "@ea.com",
                "Middle",
                "10000"
        );
        test.log(Status.INFO, "Filled and submitted employee form");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("Create")));
        Assert.assertFalse(driver.getCurrentUrl().contains("Create"));
        test.pass("Employee created successfully");
    }

    @Test(priority = 3, dependsOnMethods = "createUser")
    public void logout() {
        test = extent.createTest("Logout Test");
        test.log(Status.INFO, "Clicking logout button");

        logoutPage.logout();

        Assert.assertTrue(logoutPage.isLoggedOut());
        test.pass("Logout successful");
    }

    @AfterMethod
    public void captureScreenshotOnFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File screenshot = ts.getScreenshotAs(OutputType.FILE);
            String screenshotPath = "screenshots/" + result.getName() + "_"
                    + System.currentTimeMillis() + ".png";
            try {
                FileUtils.copyFile(screenshot, new File(screenshotPath));
                test.addScreenCaptureFromPath(screenshotPath);
                test.fail("Test failed - screenshot captured: " + screenshotPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public void tearDown() {
        extent.flush();
        if (driver != null) driver.quit();
   }
}