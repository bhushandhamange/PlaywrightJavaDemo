package com.example.com;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class ContactFormTest_PW_Options {
    
    @BeforeEach
    public void setup(Page page) {
        page.navigate("https://practicesoftwaretesting.com/contact");
    }

    @Test
    public void testContactFormSubmission(Page page) {

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
    public void uploadFile(Page page) throws IOException {
        var attachment = page.getByLabel("Attachment");
        Path fileToUpload = Paths.get("src/test/resources/data/sample-data.txt");
        attachment.setInputFiles(fileToUpload);
        org.assertj.core.api.Assertions.assertThat(attachment.inputValue()).endsWith("sample-data.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
    public void verifyErrorMessage(String fieldName, Page page) {
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
