package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static utils.RandomUtils.getRandomEmail;
import static utils.RandomUtils.getRandomString;

public class TestBase {

    Faker faker = new Faker();

    public static final String email = "g-amelin@mail.ru";
    public static final String password = "Qwerty1234";
    public static final boolean rememberMe = false;

    String gender = "M",
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName(),
            emailRegistration = getRandomEmail(),
            passwordRegistration = getRandomString(6);

    @BeforeEach
    public void precondition() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
        Configuration.browserSize = "1920x1088";
    }

    @AfterEach
    public void postcodition() {
        Selenide.closeWebDriver();
    }
}
