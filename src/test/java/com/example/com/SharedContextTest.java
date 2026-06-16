package com.example.com;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

public class SharedContextTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    Page page;

    @BeforeAll
    public static void setupBrowser(){
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(2000)
                        .setArgs(Arrays.asList("--no-sandbox","--disable-extensions","--disable-gpu"))
        );
        browserContext = browser.newContext();
    }

    @BeforeEach
    public void setup(){
        page = browserContext.newPage();
    }

    @AfterAll
    public static void tearDown(){
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
        page.getByTestId("search-submit").click();

        int count = page.locator(".card").count();
        System.out.println(count);
        Assertions.assertTrue(count > 0);
    }

    @Test
    void searchByTestId(){
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByAltText("Combination Pliers").click();
        page.getByTestId("add-to-cart").click();
    }

    @Test
    void listOfProducts(){
        page.navigate("https://practicesoftwaretesting.com/");
        page.locator("#search-query").fill("Pliers");
        page.getByTestId("search-submit").click();

        page.locator(".card").first().waitFor();

        int count = page.locator(".card").filter(
                new Locator.FilterOptions().setHasText("Out of stock")).count();
        System.out.println(count);
        Assertions.assertTrue(count > 0);
    }

}
