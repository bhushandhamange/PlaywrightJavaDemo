package com.example.com;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@UsePlaywright
public class PageFixtureTest {

    @Test
    void verifyPageTitle(Page page){
        page.navigate("https://practicesoftwaretesting.com/");

        String title = page.title();
        Assertions.assertTrue(title.contains("Practice Software Testing"));
    }

    @Test
    void searchProduct(Page page){
        page.navigate("https://practicesoftwaretesting.com/");

        page.locator("#search-query").fill("Pliers");
        int count = page.locator(".card").count();

        Assertions.assertTrue(count > 0);
    }

}
