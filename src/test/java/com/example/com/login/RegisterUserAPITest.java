package com.example.com.login;

import com.example.com.domain.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UsePlaywright
public class RegisterUserAPITest {

    private APIRequestContext request;

    @BeforeEach
    void setup(Playwright playwright) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL("https://api.practicesoftwaretesting.com")
        );
    }

    @AfterEach
    void tearDown() {
        if (request != null) {
            request.dispose();
        }
    }

    @Test
    void registerUserAPITest() {
        User validUser = User.randomUser();

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(validUser)
        );

        String responseBody = response.text();
        Gson gson = new Gson();
        User registeredUser = gson.fromJson(responseBody, User.class);

        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Successful Registration should return 201 response code")
                    .isEqualTo(201);
            softly.assertThat(registeredUser.first_name()).isEqualTo(validUser.first_name());
            softly.assertThat(registeredUser.last_name()).isEqualTo(validUser.last_name());
            softly.assertThat(registeredUser.email()).isEqualTo(validUser.email());
            softly.assertThat(registeredUser.phone()).isEqualTo(validUser.phone());

            softly.assertThat(jsonObject.get("id").getAsString())
                    .as("Response should contain 'id' field")
                    .isNotEmpty();
            softly.assertThat(jsonObject.has("password"))
                    .as("Response should not contain 'password' field")
                    .isFalse();

            softly.assertThat(response.headers().get("content-type"))
                    .as("Response should have Content-Type header set to application/json")
                    .isEqualTo("application/json");
        });
    }

    @Test
    void firstNameMandatoryTest() {
        User validUser = User.randomUser();
        User userWithoutFirstName = validUser.withFirstName(null);

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(userWithoutFirstName)
        );

        JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Registration without first name should return 422 response code")
                    .isEqualTo(422);

            softly.assertThat(jsonObject.has("first_name"))
                    .as("Response should contain 'first_name' field indicating the error")
                    .isTrue();

            softly.assertThat(jsonObject.get("first_name").getAsString())
                    .as("Error message for 'first_name' should indicate that it is required")
                    .isEqualTo("The first name field is required.");

        });
    }
}
