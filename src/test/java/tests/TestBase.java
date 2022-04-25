package tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

    public static final String email = "g-amelin@mail.ru";
    public static final String password = "Qwerty1234";
    public static final boolean rememberMe = false;

    @BeforeEach
    public void precondition() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }
}
