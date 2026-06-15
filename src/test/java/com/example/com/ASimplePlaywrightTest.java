package com.example.com;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ASimplePlaywrightTest {

    @Test
    void verifyPageTitle(){
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();
        page.navigate("https://practicesoftwaretesting.com/");

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));

        browser.close();
        playwright.close();
    }

    @Test
    void searchProduct(){
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();
        page.navigate("https://practicesoftwaretesting.com/");

        page.locator("//input[@id='search-query']").fill("Pliers");
        int count = page.locator(".card").count();

        Assertions.assertTrue(count > 0);

        browser.close();
        playwright.close();
    }
}
