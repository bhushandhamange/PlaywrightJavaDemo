package com.example.com;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class WaitForElementToAppearAndDisappear {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    Page page;

    @BeforeAll
    public static void setupBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        browserContext = browser.newContext();
    }

    @BeforeEach
    public void setup() {
        page = browserContext.newPage();
        page.navigate("https://practicesoftwaretesting.com");
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void shouldShowToasterMessage() {
        page.getByText("Bolt Cutters").click();
        page.getByText("Add to cart").click();

        // Wait for the toaster message to appear
        assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");

        page.waitForCondition(() -> !page.getByRole(AriaRole.ALERT).isVisible());
    }

    @Test
    public void shouldUpdateItemCountInCart() {
        page.getByText("Bolt Cutters").click();
        page.getByText("Add to cart").click();

        // Wait for the cart count to update
        page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
//        page.waitForSelector("[data-test='cart-quantity']:has-text('1')");
    }

    @Test
    public void sortByAscendingPrice(){

        //https://api.practicesoftwaretesting.com/products?page=0&sort=price,desc&between=price,1,100&is_rental=false

        //wait for response from API call
        page.waitForResponse("**/products?page=0&sort=price,desc**",
                () -> {
                    page.getByTestId("sort").selectOption("Price (High - Low)");
                });

        //find all prices on page
        var productPrices = page.getByTestId("product-price").allInnerTexts().stream()
                .map(WaitForElementToAppearAndDisappear::extractPrices)
                .toList();

        //verify prices in descending order
        System.out.println(productPrices);
        Assertions.assertThat(productPrices).isNotEmpty().isSortedAccordingTo(Comparator.reverseOrder());
    }

    private static double extractPrices(String priceText){
        return Double.parseDouble(priceText.replace("$", ""));
    }
}
