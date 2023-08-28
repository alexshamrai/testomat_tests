package io.testomat.ui;

import java.time.Duration;

import io.testomat.ui.asserts.SuitePageAsserts;
import io.testomat.ui.data.BaseSuiteInfo;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;

public class SuitePage extends BasePage{

    public SuitePage isLoaded(String testSuiteName) {
        $("h3").shouldBe(text(testSuiteName), Duration.ofSeconds(20));
        return this;
    }

    public String getSuiteId() {
        var idInWebPage = $(".copy-id").getText();
        int startIndex = idInWebPage.indexOf("@S") + 2;
        return idInWebPage.substring(startIndex);
    }

    public SuitePageAsserts assertThat(BaseSuiteInfo expectedSuiteTitle) {
        return new SuitePageAsserts(expectedSuiteTitle);
    }

    public SuitePage createNewTest(String testName) {
        $("[placeholder='Add new test']").val(testName);
        $$(".primary-btn").findBy(text("Create")).click();
        return this;
    }

    public TestPage openTestByName(String targetTestName) {
        $$("span").findBy(text(targetTestName)).click();
        return page(TestPage.class);
    }
}
