package tests;

import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DemowebshopTests extends TestBase {

    @Test
    @DisplayName("Регистрация нового пользователя")
    void userLogInTest() {

        String cookie = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("RememberMe", rememberMe)
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract().response().cookie("NOPCOMMERCE.AUTH");

        System.out.println("\n Cookie is: " + cookie + "\n");

        open("/content/images/thumbs/0000215.png");

        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", cookie));

        open("");
        sleep(2000);

        $(".account").shouldHave(text(email));

    }

    @Test
    @DisplayName("Регистрация нового пользователя 2")
    void registerNewUserTestTwo() {

        Cookies cookie = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("RememberMe", rememberMe)
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract().response().getDetailedCookies();

        System.out.println("\n Cookie is: " + cookie + "\n");

        given()
                .cookie(String.valueOf(cookie))
                .header("Referer", "/login")
                .when()
                .get()
                .then()
//                .log().cookies()
                .statusCode(200)
                .extract().response().cookies();
    }

    @Test
    void addToCartAsNewUserTest() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_72_5_18=53" +
                        "&product_attribute_72_6_19=54" +
                        "&product_attribute_72_3_20=57" +
                        "&addtocart_72.EnteredQuantity=1")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", is(true))
                .body("message", is("The product has been added to your " +
                        "\u003ca href=\"/cart\"\u003eshopping cart\u003c/a\u003e"))
                .body("updatetopcartsectionhtml", is("(1)"));
    }

    @Test
    void addToCartWithCookieTest() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookie("Nop.customer=4a270881-47e7-42c9-a179-438c242fa564; " +
                        "ARRAffinity=1818b4c81d905377ced20e7ae987703a674897394db6e97dc1316168f754a687")
                .body("product_attribute_72_5_18=53" +
                        "&product_attribute_72_6_19=54" +
                        "&product_attribute_72_3_20=57" +
                        "&addtocart_72.EnteredQuantity=1")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", is(true))
                .body("message", is("The product has been added to your " +
                        "\u003ca href=\"/cart\"\u003eshopping cart\u003c/a\u003e"))
                .body("updatetopcartsectionhtml", is("(8)"));
    }

    @Test
    void addToCartTestWithInt() {
        Integer cartSize = 0;

        ValidatableResponse response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .cookie("Nop.customer=4a270881-47e7-42c9-a179-438c242fa564; " +
                                "ARRAffinity=1818b4c81d905377ced20e7ae987703a674897394db6e97dc1316168f754a687")
                        .body("product_attribute_72_5_18=53" +
                                "&product_attribute_72_6_19=54" +
                                "&product_attribute_72_3_20=57" +
                                "&addtocart_72.EnteredQuantity=1")
                        .when()
                        .post("http://demowebshop.tricentis.com/addproducttocart/details/72/1")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("message", is("The product has been added to your " +
                                "\u003ca href=\"/cart\"\u003eshopping cart\u003c/a\u003e"));

//        to do
//        assertThat(response.extract().path("updatetopcartsectionhtml").toString());
//                .body("updatetopcartsectionhtml", is("(8)"));
    }

}
