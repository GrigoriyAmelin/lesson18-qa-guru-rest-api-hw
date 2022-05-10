package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.http.Cookies;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemowebshopTests extends TestBase {

    @Test
    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @DisplayName("Вход в личный кабинет зарегистрированного пользователя")
    void userLogInTest() {

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

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

            System.out.println("\n Cookie \"NOPCOMMERCE.AUTH\" in userLogInTest is: " + cookie + "\n");

            step("Открыть любую страницу для активации сессии пользователя", () ->
                    open("/content/images/thumbs/0000215.png"));

            step("Применить куку \"NOPCOMMERCE.AUTH\"", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", cookie)));
        });

        step("Открыть основную страницу сайта", () ->
                open(""));

        step("Проверить, что пользователь вошел в личный кабинет", () ->
                $(".header-links .account").shouldHave(text(email)));
    }

    @Test
    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @DisplayName("Добавление товара в корзину залогиненного пользователя")
    void addToCartTest() {

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
                .body("updatetopcartsectionhtml", is(notNullValue()));
    }

    @Test
    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @DisplayName("Очистка корзины залогиненного пользователя")
    void updateCartTest() {

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

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

        System.out.println("\n Cookie \"NOPCOMMERCE.AUTH\" in userLogInTest is: " + cookie + "\n");

        step("Открыть страницу товара и открыть новую сессию пользователя", () ->
                open("/50s-rockabilly-polka-dot-top-jr-plus-size"));

        step("Применить куку \"NOPCOMMERCE.AUTH\"", () ->
                getWebDriver().manage().addCookie(
                        new Cookie("NOPCOMMERCE.AUTH", cookie)));

        step("Добавить товар в корзину", () -> {
            $(".qty-input").setValue("2");
            $("#add-to-cart-button-5").click();
        });

        step("Открыть корзину клиента", () ->
                $("#topcartlink").click());

        step("Выбрать все товары в корзине", () -> {
                    List<WebElement> checkboxes = getWebDriver().findElements(By.name("removefromcart"));
                    int numberOfCheckboxes = checkboxes.size();
                    for (WebElement cb : checkboxes) {
                        cb.click();
                    }
                    System.out.println("Number of products is: " + numberOfCheckboxes);
                }
        );

        step("Удалить все товары из корзины", () ->
                $(".update-cart-button").click());

        step("Проверить отсутствие товаров в корзине через API", () -> {
            String response = given()
                    .cookie(cookie)
                    .when()
                    .get("/cart")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response()
                    .asString();

            System.out.println("Response body is:\n" + response);

            XmlPath responseHtml = new XmlPath(
                    CompatibilityMode.HTML,
                    response);

            String message = responseHtml.getString("**.findAll { it.@class == 'order-summary-content' }[0]");
            System.out.println("responseHtml is: \"" + message + "\"");
            assertEquals("\n" + "    \n" + "    \n" + "Your Shopping Cart is empty!    " + "\n", message);
        });
    }

    @Test
    @Story("Проверка сайта http://demowebshop.tricentis.com/")
    @DisplayName("Регистрация нового пользователя")
    void newUserRegistrationTest() {

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        step("Зарегистрироваться через GUI", () -> {
            open("");
            $(".header-links .ico-register").scrollTo().click();
            $(".registration-page").shouldHave(text("Register"));
            $(byName("Gender")).selectRadio(gender);
            $("#FirstName").setValue(firstName);
            $("#LastName").setValue(lastName);
            $("#Email").setValue(emailRegistration);
            $("#Password").setValue(passwordRegistration);
            $("#ConfirmPassword").setValue(passwordRegistration);
            $("#register-button").click();
            $(".ico-logout").click();
        });

        step("Залогиниться через API и проверить существование нового пользователя", () -> {
            String cookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", emailRegistration)
                    .formParam("Password", passwordRegistration)
                    .formParam("RememberMe", rememberMe)
                    .when()
                    .post("/login")
                    .then()
                    .log().cookies()
                    .statusCode(302)
                    .extract()
                    .response()
                    .cookie("NOPCOMMERCE.AUTH");

            System.out.println("\n Cookie \"NOPCOMMERCE.AUTH\" in userLogInTest is: " + cookie + "\n");

            step("Открыть любую страницу для активации сессии пользователя", () ->
                    open("/content/images/thumbs/0000215.png"));

            step("Применить куку \"NOPCOMMERCE.AUTH\"", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", cookie)));
        });

        step("Открыть основную страницу сайта", () ->
                open(""));

        step("Проверить, что пользователь вошел в личный кабинет", () ->
                $(".header-links .account").shouldHave(text(emailRegistration)));
    }
}
