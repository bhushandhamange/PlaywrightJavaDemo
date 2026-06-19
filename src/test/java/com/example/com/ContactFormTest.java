package com.example.com;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class ContactFormTest {

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
        page.navigate("https://practicesoftwaretesting.com/contact");
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testContactFormSubmission() {

        // Fill out the contact form
        var firstName = page.getByLabel("First Name");
        var lastName = page.getByLabel("Last Name");
        var email = page.getByLabel("Email");
        var message = page.getByLabel("Message");
        var subject = page.getByLabel("Subject");

        firstName.fill("Bhushan");
        lastName.fill("Dhamange");
        email.fill("bhushan.dhamange@gmail.com");
        message.fill("Bhushan Dhamange");
        subject.selectOption("payments");

        assertThat(firstName).hasValue("Bhushan");
        assertThat(lastName).hasValue("Dhamange");
        assertThat(email).hasValue("bhushan.dhamange@gmail.com");
        assertThat(message).hasValue("Bhushan Dhamange");
        assertThat(subject).hasValue("payments");
    }

    @Test
    public void uploadFile() throws IOException {
        var attachment = page.getByLabel("Attachment");
        Path fileToUpload = Paths.get("src/test/resources/data/sample-data.txt");
        attachment.setInputFiles(fileToUpload);
        org.assertj.core.api.Assertions.assertThat(attachment.inputValue()).endsWith("sample-data.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
    public void verifyErrorMessage(String fieldName) {
        // Fill out the contact form
        var firstName = page.getByLabel("First name");
        var lastName = page.getByLabel("Last name");
        var email = page.getByLabel("Email");
        var message = page.getByLabel("Message");
        var subject = page.getByLabel("Subject");

        firstName.fill("Bhushan");
        lastName.fill("Dhamange");
        email.fill("bhushan.dhamange@gmail.com");
        message.fill("Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange " +
                "Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange " +
                "Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange Bhushan Dhamange");
        subject.selectOption("payments");

        page.getByLabel(fieldName).clear();

        page.getByText("Send").click();

        var alertMessage = page.getByRole(AriaRole.ALERT);

        assertThat(alertMessage).hasText(fieldName + " is required");
    }
}
