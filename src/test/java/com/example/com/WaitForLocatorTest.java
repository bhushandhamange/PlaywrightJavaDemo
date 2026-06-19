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


public class WaitForLocatorTest {

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
    public void checkForCorrectPrices(){
        page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);

        List<Double> prices = page.getByTestId("product-price").allInnerTexts().stream()
                .map(priceText -> Double.parseDouble(priceText.replace("$", "")))
                .toList();
        
        Assertions.assertThat(prices).
                isNotEmpty().
                allMatch(price -> price > 0)
                .doesNotContain(0.0)
                .allMatch(price -> price < 1000)
                .allSatisfy(price -> Assertions.assertThat(price).isGreaterThan(0).isLessThan(1000));
    }

    @Test
    public void waitForAllProductNames() {
        page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
        List<String> allProductNames = page.getByTestId("product-name").allInnerTexts();
        Assertions.assertThat(allProductNames).contains("Pliers", "Bolt Cutters", "Hammer");
    }

    @Test
    public void waitForAllProductImages(){
        page.waitForSelector(".card-img-top");
        List<String>  allProductImages = page.locator(".card-img-top").all().stream()
                .map(element -> element.getAttribute("alt"))
                .toList();
        Assertions.assertThat(allProductImages).contains("Pliers", "Bolt Cutters", "Hammer");
    }

    @Test
    public void shouldSortProductNames(){
        page.getByLabel("Sort").selectOption("Name (A - Z)");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        List<String> sortedProductNames = page.getByTestId("product-name").allInnerTexts();

        Assertions.assertThat(sortedProductNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
    }

    @Test
    public void shouldSortProductNames_inReverseOrder(){
        page.getByLabel("Sort").selectOption("Name (Z - A)");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        List<String> sortedProductNames = page.getByTestId("product-name").allInnerTexts();

        Assertions.assertThat(sortedProductNames).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    public void shouldWaitForCheckboxToBeChecked(){
        var scredriverFilter = page.getByLabel("Screwdriver");
        scredriverFilter.click();
        assertThat(scredriverFilter).isChecked();
    }

    @Test
    public void shouldFilterProductByCategories(){
        page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
        page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();

        page.waitForSelector(".card",
            new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(2000));

        var filterdProductNames = page.getByTestId("product-name").allInnerTexts();

        Assertions.assertThat(filterdProductNames).contains("Sheet Sander", "Belt Sander", "Cordless Drill 20V");
    }
}
