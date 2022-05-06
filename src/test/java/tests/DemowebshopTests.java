package tests;

import io.qameta.allure.Story;
import io.restassured.http.Cookies;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class DemowebshopTests extends TestBase {

    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @Test
    @DisplayName("Вход в личный кабинет и получение куки авторизации")
    void userLogInTest() {

        step("Залогиниться через API и получить куку \"NOPCOMMERCE.AUTH\"", () -> {

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
                    .extract()
                    .response()
                    .cookie("NOPCOMMERCE.AUTH");

            System.out.println("\n Cookies in userLogInTest are: " + cookie + "\n");

            step("Открыть любую страницу для активации сессии пользователя", () ->
                    open("/content/images/thumbs/0000215.png"));

            step("Применить куку \"NOPCOMMERCE.AUTH\"", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", cookie)));

            step("Открыть основную страницу сайта", () ->
                    open(""));

            step("Проверить, что пользователь вошел в личный кабинет", () ->
                    $(".account").shouldHave(text(email)));
        });
    }

    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @Test
    @DisplayName("Добавление товара в пустую корзину залогиненного пользователя")
    void addToEmptyCartTest() {

        Cookies cookiesAll = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("RememberMe", rememberMe)
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract()
                .response()
                .getDetailedCookies();

        System.out.println("\n Cookies in addToEmptyCartTest are: " + cookiesAll + "\n");

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookies(cookiesAll)
                .formParam("product_attribute_5_7_1", 1)
                .formParam("addtocart_5.EnteredQuantity", 2)
                .when()
                .post("/addproducttocart/details/5/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", is(true))
                .body("message", is("The product has been added to your " +
                        "\u003ca href=\"/cart\"\u003eshopping cart\u003c/a\u003e"))
                .body("updatetopcartsectionhtml", is("(2)"));
    }

    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @Test
    @DisplayName("Очистка корзины пользователя")
    void toUpdateCartTest() {

        Cookies cookiesAll = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("Email", email)
                .formParam("Password", password)
                .formParam("RememberMe", rememberMe)
                .when()
                .post("/login")
                .then()
                .log().all()
                .statusCode(302)
                .extract()
                .response()
                .getDetailedCookies();

        System.out.println("\n Cookies in clearCartTest are: " + cookiesAll + "\n");

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookies(cookiesAll)
                .formParam("removefromcart", 2374929)
                .formParam("itemquantity2374929", 2)
                .formParam("updatecart", "Update shopping cart")
                .when()
                .post("/cart")
                .then()
                .log().cookies()
                .statusCode(200);
    }

    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @Test
    @DisplayName("Добавление товара и корзины нового пользователя")
    void newUserUpdateCartTest() {

        step("Зарегистрироваться через API и получить куку \"NOPCOMMERCE.AUTH\"", () -> {

            given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("__RequestVerificationToken", requestVerificationToken)
                    .formParam("Gender", gender)
                    .formParam("FirstName", firstName)
                    .formParam("LastName", lastName)
                    .formParam("Email", emailRegistration)
                    .formParam("Password", passwordRegistration)
                    .formParam("ConfirmPassword", passwordRegistration)
                    .formParam("register-button", registerButton)
                    .when()
                    .post("/register")
                    .then()
                    .statusCode(302);

        });

        step("Залогиниться через API и получить куку \"NOPCOMMERCE.AUTH\"", () -> {

            String cookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", emailRegistration)
                    .formParam("Password", passwordRegistration)
                    .formParam("RememberMe", rememberMe)
                    .when()
                    .post("/login")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract()
                    .response()
                    .cookie("NOPCOMMERCE.AUTH");

            System.out.println("\n Cookies in userLogInTest are: " + cookie + "\n");

            step("Открыть любую страницу для активации сессии пользователя", () ->
                    open("/content/images/thumbs/0000215.png"));

            step("Применить куку \"NOPCOMMERCE.AUTH\"", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", cookie)));

            step("Открыть основную страницу сайта", () ->
                    open(""));

            step("Проверить, что пользователь вошел в личный кабинет", () ->
                    $(".account").shouldHave(text(emailRegistration)));
        });
    }
}
