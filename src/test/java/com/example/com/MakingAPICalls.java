package com.example.com;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class MakingAPICalls {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    Page page;

    record Product (String name, Double price) {}

    private static APIRequestContext requestContext;

    @BeforeAll
    public static void setup(){
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com")
                        .setExtraHTTPHeaders(new HashMap<>(){
                            {put("Accept", "application/json");}
                        })
        );

        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
//                        .setSlowMo(500)
                        .setArgs(Arrays.asList("--no-sandbox","--disable-extensions","--disable-gpu"))
        );
        browserContext = browser.newContext();
    }

    @DisplayName("Check presence of Known products")
    @ParameterizedTest(name = "Product: {0}")
    @MethodSource("products")
    public void testProductPresence(Product product) {
        page = browserContext.newPage();
        page.navigate("https://practicesoftwaretesting.com");
        page.getByPlaceholder("Search").fill(product.name);
        page.getByTestId("search-submit").click();

        //check if the product appears in the search result with correct price and name
        Locator productCard = page.locator(".card")
                .filter(new Locator.FilterOptions()
                        .setHasText(product.name)
                        .setHasText(Double.toString(product.price))
                );
        PlaywrightAssertions.assertThat(productCard).isVisible();
    }

    static Stream<Product> products() {
        APIResponse response = requestContext.get("/products?page=2");
        Assertions.assertThat(response.status()).isEqualTo(200);

        JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
        JsonArray data = jsonObject.getAsJsonArray("data");

        return data.asList().stream()
                .map(element -> {
                    JsonObject productJson = element.getAsJsonObject();
                    String name = productJson.get("name").getAsString();
                    Double price = productJson.get("price").getAsDouble();
                    return new Product(name, price);
                });
    }

    @AfterEach
    public void closePage(){
        page.close();
    }

    @AfterAll
    public static void tearDown(){
        browserContext.close();
        playwright.close();
    }
}
