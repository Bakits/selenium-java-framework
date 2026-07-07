package gettingStarted.tests;

import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        return new Object[][] {
                {"admin", "password", true},
                {"admin", "wrongpass", false},
                {"wronguser", "password", false}
        };
    }
}