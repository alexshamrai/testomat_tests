package io.testomat.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import lombok.Data;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@UtilityClass
public class PlaywrightWrapper {

    private static final ConcurrentHashMap<Long, PlaywrightEnvironment> playwrightEnvironment = new ConcurrentHashMap<>();

    public PlaywrightEnvironment getEnvironment() {
        long threadId = Thread.currentThread().getId();
        if (!playwrightEnvironment.containsKey(threadId)) {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(Configuration.headless)
                    .setTimeout(Configuration.browserToStartTimeout)
                    .setDevtools(Configuration.devTools)
                    .setSlowMo(Configuration.poolingInterval)
            );
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            playwrightEnvironment.put(threadId, new PlaywrightEnvironment(context, page, playwright, browser));
        }

        return playwrightEnvironment.get(threadId);
    }

    public void close() {
        long threadId = Thread.currentThread().getId();

        if (playwrightEnvironment.containsKey(threadId)) {
            playwrightEnvironment.get(threadId).getPage().close();
            playwrightEnvironment.get(threadId).getContext().close();
            playwrightEnvironment.get(threadId).getBrowser().close();
            playwrightEnvironment.get(threadId).getPlaywright().close();
            playwrightEnvironment.remove(threadId);
        }
    }

    public void open(String url) {
        var targetUrl = StringUtils.isNotBlank(Configuration.baseUrl) ? Configuration.baseUrl + url : url;
        getEnvironment().getPage().navigate(targetUrl);
    }

    public PlaywrightElement find(String selector) {
        return new PlaywrightElement(getEnvironment().getPage().locator(selector).first());
    }

    public PlaywrightElement find(String selector, String text) {
        return new PlaywrightElement(getEnvironment().getPage().locator(selector).filter(
            new Locator.FilterOptions().setHasText(text)
        ));
    }

    public void confirm(String text) {
        getEnvironment().getPage().onceDialog(dialog -> {
            if (dialog.message().equals(text)) {
                dialog.accept();
            }
        });
    }

    public void addCookies(List<Cookie> cookies) {
        long threadId = Thread.currentThread().getId();
        BrowserContext context = playwrightEnvironment.get(threadId).getContext();
        List<Cookie> adjustedCookies = cookies.stream()
                                              .map(cookie -> {
                                                  cookie.setPath("/");
                                                  cookie.setDomain("uat.testomat.io");
                                                  return cookie;
                                              })
                                              .collect(Collectors.toList());
        context.addCookies(adjustedCookies);
    }

    @Data
    public static class PlaywrightEnvironment {

        private final BrowserContext context;
        private final Page page;
        private final Playwright playwright;
        private final Browser browser;
    }
}

