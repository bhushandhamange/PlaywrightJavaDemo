package com.example.com;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UsePlaywright
public class PlaywrightLocatorsTest {

    @BeforeEach
    void openPage(Page page){
        page.navigate("https://practicesoftwaretesting.com/");
    }

    @Test
    void byText(Page page){
        page.getByText("Bolt Cutters").click();
        PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
    }

    @Test
    void byAltText(Page page){
        page.getByAltText("Combination Pliers").click();
        PlaywrightAssertions.assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Combination Pliers"))).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();
    }
}
