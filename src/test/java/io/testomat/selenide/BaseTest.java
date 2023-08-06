package io.testomat.selenide;

import java.time.Duration;
import java.util.Set;

import com.codeborne.selenide.Configuration;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit5.SoftAssertsExtension;
import com.codeborne.selenide.junit5.TextReportExtension;
import io.testomat.api.login.CredentialsLoader;
import io.testomat.api.login.LoginController;
import io.testomat.api.login.model.Credentials;
import io.testomat.selenide.common.SoftAssertExtension;
import com.github.javafaker.Faker;
import io.testomat.ui.LoginSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith({SoftAssertsExtension.class, SoftAssertExtension.class})
public class BaseTest {

    LoginSteps loginSteps = new LoginSteps();
    Credentials credentials = CredentialsLoader.getCredentials();
    Faker faker = new Faker();
    String authToken;

    @RegisterExtension
    static TextReportExtension textReportExtension = new TextReportExtension().onFailedTest(true).onSucceededTest(true);

    // TODO extract parameters to properties
    static {
        Configuration.baseUrl = "https://uat.testomat.io/";
        Configuration.browserSize = "1024*1024";
        Configuration.clickViaJs = true;
        Configuration.fastSetValue = true;
        Configuration.remoteReadTimeout = Duration.ofSeconds(30).toMillis();
        Configuration.remoteConnectionTimeout = Duration.ofSeconds(30).toMillis();
    }

    public void openPageAsLoggedInUser(String relativeUrl) {
        open("/");
        signUserWithCookies(loginSteps.getLoginCookies(credentials));
        open(relativeUrl);
    }

    private void signUserWithCookies(Set<Cookie> cookies) {
        cookies.forEach(cookie -> WebDriverRunner.getWebDriver().manage().addCookie(cookie));
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }
    @BeforeEach
    void getToken() {
        authToken = new LoginController().loginUser(credentials);
    }

}
