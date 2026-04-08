package demoqa.tests;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static demoqa.tests.TestData.login;
import static demoqa.tests.TestData.password;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;



public class LoginTests extends TestBase {


  @Test
  void successfulLoginWithUiTest() {
    open("/login");
    $("#userName").setValue(login);
    $("#password").setValue(password);
    $("#login").click();
    $("#userName-value").shouldHave(text(login));
  }



  @Test
  void successfulLoginWithApiTest() {
    String authData = """
    {
      "userName": "test123456",
      "password": "Test123456@"
      }
    """;

    Response authResponse = given()
        .log().uri()
        .log().body()
        .log().headers()
        .contentType(JSON)
        .body(authData)
        .when()
        .post("/Account/v1/Login")
        .then()
        .log().status()
        .log().body().statusCode(200)
        .extract().response();

    open("/favicon.ico");
    // Установили cookie
    getWebDriver().manage().addCookie(new Cookie("userID", authResponse.path("userId")));
    getWebDriver().manage().addCookie(new Cookie("expires",authResponse.path("expires")));
    getWebDriver().manage().addCookie(new Cookie("token", authResponse.path("token")));

    open("/profile");
    $("#userName-value").shouldHave(text(login));
  }

}
