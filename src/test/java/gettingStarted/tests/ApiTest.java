package gettingStarted.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ApiTest {

    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setUp() {
        ExtentSparkReporter spark = new ExtentSparkReporter("extent-report-api.html");
        spark.config().setReportName("API Test Report");
        spark.config().setDocumentTitle("REST Assured TestNG Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "Bakita");
        extent.setSystemInfo("API", "jsonplaceholder.typicode.com");

        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test(priority = 1)
    public void getUserTest() {
        test = extent.createTest("GET User Test");
        test.log(Status.INFO, "Sending GET request to /users/1");

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/users/1")
                .then()
                .extract().response();

        test.log(Status.INFO, "Response status code: " + response.getStatusCode());
        test.log(Status.INFO, "Response body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");
        Assert.assertEquals(response.jsonPath().getString("name"), "Leanne Graham",
                "User name mismatch");

        test.pass("GET request successful - User retrieved correctly");
    }

    @Test(priority = 2)
    public void createPostTest() {
        test = extent.createTest("POST Create Post Test");
        test.log(Status.INFO, "Sending POST request to /posts");

        String requestBody = "{\n" +
                "  \"title\": \"Bakita Automation Test\",\n" +
                "  \"body\": \"This post was created by REST Assured\",\n" +
                "  \"userId\": 1\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .extract().response();

        test.log(Status.INFO, "Response status code: " + response.getStatusCode());
        test.log(Status.INFO, "Response body: " + response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 201, "Status code mismatch");
        Assert.assertEquals(response.jsonPath().getString("title"), "Baks Automation Test",
                "Post title mismatch");

        test.pass("POST request successful - Post created correctly");
    }

    @Test(priority = 3)
    public void deletePostTest() {
        test = extent.createTest("DELETE Post Test");
        test.log(Status.INFO, "Sending DELETE request to /posts/1");

        Response response = given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/posts/1")
                .then()
                .extract().response();

        test.log(Status.INFO, "Response status code: " + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");

        test.pass("DELETE request successful - Post deleted correctly");
    }

    @AfterClass
    public void tearDown() {
        extent.flush();
    }
}