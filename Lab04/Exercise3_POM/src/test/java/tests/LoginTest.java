package tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import pages.LoginPage;
import utils.DriverFactory;

import java.time.Duration;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Login Tests using Page Object Model")
public class LoginTest extends BaseTest {
    static LoginPage loginPage;

    @BeforeAll
    static void setUp() {
        loginPage = new LoginPage(driver);
    }

    @Test
    @Order(1)
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        loginPage.navigate();
        loginPage.login("tomsmith", "SuperSecretPassword!");
        assertTrue(loginPage.getMessageText(loginPage.getSuccessLocator()).contains("You logged into a secure area!"));
    }

    @Test
    @Order(2)
    @DisplayName("Should show error for invalid credentials")
    void testLoginFail() {
        loginPage.navigate();
        loginPage.login("wronguser", "wrongpassword");
        assertTrue(loginPage.getMessageText(loginPage.getErrorLocator()).contains("invalid"));
    }

    @ParameterizedTest(name = "CSV Inline: {0} / {1}")
    @Order(3)
    @CsvSource({
            "tomsmith, SuperSecretPassword!, success",
            "wronguser, SuperSecretPassword!, error",
            "tomsmith, wrongpassword, error",
            "'', '', error"
    })
    void testLoginCsvInline(String username, String password, String expected) {
        verifyLogin(username, password, expected);
    }

    @ParameterizedTest(name = "CSV File: {0} / {1}")
    @Order(4)
    @CsvFileSource(resources = "/login-data.csv", numLinesToSkip = 1)
    void testLoginFromCSV(String username, String password, String expected) {
        verifyLogin(username, password, expected);
    }

    // Helper dùng chung cho 2 test tham số – tránh lặp code (DRY)
    private void verifyLogin(String username, String password, String expected) {
        loginPage.navigate();
        username = (username == null) ? "" : username.trim();
        password = (password == null) ? "" : password.trim();

        loginPage.login(username, password);

        if (expected.equals("success")) {
            assertTrue(loginPage.getMessageText(loginPage.getSuccessLocator()).contains("You logged into a secure area!"));
        } else {
            assertTrue(loginPage.getMessageText(loginPage.getErrorLocator()).toLowerCase().contains("invalid"));
        }
    }

}