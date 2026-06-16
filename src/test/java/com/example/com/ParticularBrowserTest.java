package com.example.com;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ParticularBrowserTest {

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    public void setup(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox","--disable-extensions","--disable-gpu"))
        );
        page = browser.newPage();
    }

    @AfterEach
    public void tearDown(){
        browser.close();
        playwright.close();
    }

    @Test
    void verifyPageTitle(){
        page.navigate("https://practicesoftwaretesting.com/");

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void searchProduct(){
        page.navigate("https://practicesoftwaretesting.com/");

        page.locator("#search-query").fill("Pliers");
        int count = page.locator(".card").count();

        Assertions.assertTrue(count > 0);
    }

}
