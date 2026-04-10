package demoqa.tests;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static demoqa.tests.TestData.login;
import static demoqa.tests.TestData.password;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import static org.hamcrest.Matchers.is;

public class CollectionTests extends TestBase {

// Авторизация пользователя, очистка коллекции книг в корзине, добавление новой книги, проверка в интерфейсе
  @Test
  void addBookToCollectionTest() {
    String authData = """
    {
      "userName": "test123456",
      "password": "Test123456@"
      }
    """;

    Response authResponse = given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .body(authData)
        .when()
        .post("/Account/v1/Login")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().response();

    given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .header("Authorization", "Bearer "  + authResponse.path("token"))
        .body(authData)
        .queryParams("UserId", authResponse.path("userId"))
        .when()
        .delete("/BookStore/v1/Books")
        .then()
        .log().status()
        .log().body()
        .statusCode(204);



    String isbn = "9781491950296";
    String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
        authResponse.path("userId") , isbn);


    given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .header("Authorization", "Bearer "  + authResponse.path("token"))
        .body(bookData)
        .when()
        .post("/BookStore/v1/Books")
        .then()
        .log().status()
        .log().body()
        .statusCode(201);



    open("/favicon.ico");
    // Установили cookie
    getWebDriver().manage().addCookie(new Cookie("userID", authResponse.path("userId")));
    getWebDriver().manage().addCookie(new Cookie("expires",authResponse.path("expires")));
    getWebDriver().manage().addCookie(new Cookie("token", authResponse.path("token")));

    open("/profile");
    By.xpath("//a[text()='Understanding ECMAScript 6']");
  }



// Авторизация пользователя, удаление товара из корзины (204)
  @Test
  void addBookToCollection_withDelete1Book_Test() {
    String authData = "{\"userName\":\"" + login + "\",\"password\":\"" + password + "\"}";

    Response authResponse = given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .body(authData)
        .when()
        .post("/Account/v1/Login")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().response();

    String isbn = "9781491950296";
    String deleteBookData = format("{\"userId\":\"%s\",\"isbn\":\"%s\"}",
        authResponse.path("userId") , isbn);

    given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .header("Authorization", "Bearer " + authResponse.path("token"))
        .body(deleteBookData)
        .when()
        .delete("/BookStore/v1/Book")
        .then()
        .log().status()
        .log().body()
        .statusCode(204);

    String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
        authResponse.path("userId") , isbn);

    given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .header("Authorization", "Bearer " + authResponse.path("token"))
        .body(bookData)
        .when()
        .post("/BookStore/v1/Books")
        .then()
        .log().status()
        .log().body()
        .statusCode(201);

    open("/favicon.ico");
    getWebDriver().manage().addCookie(new Cookie("userID", authResponse.path("userId")));
    getWebDriver().manage().addCookie(new Cookie("expires", authResponse.path("expires")));
    getWebDriver().manage().addCookie(new Cookie("token", authResponse.path("token")));

    open("/profile");
    By.xpath("//a[text()='Understanding ECMAScript 6']");
  }



// Негативный тест (авторизовались, добавили товар в корзину, который уже добавлен ранее 400)
@Test
void negative400BookToCollectionTest() {
  String authData = """
      {
        "userName": "test123456",
        "password": "Test123456@"
        }
      """;

  Response authResponse = given()
      .log().uri()
      .log().method()
      .log().body()
      .contentType(JSON)
      .body(authData)
      .when()
      .post("/Account/v1/Login")
      .then()
      .log().status()
      .log().body()
      .statusCode(200)
      .extract().response();

  String isbn = "9781491950296";
  String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}", authResponse.path("userId"), isbn);

  given()
      .log().uri()
      .log().method()
      .log().body()
      .contentType(JSON)
      .header("Authorization", "Bearer " + authResponse.path("token"))
      .body(bookData)
      .when()
      .post("/BookStore/v1/Books")
      .then()
      .log().status()
      .log().body()
      .statusCode(400)
      .body("code", is("1210"))
      .body("message", is("ISBN already present in the User's Collection!"));
}



  // Негативный тест (пользователь не авторизован 401)
  @Test
  void negative401AddBookToCollectionTest() {

    String userId = "862cbbea-c3e8-4a39-be64-3b053aedeef2";
    String isbn = "9781491950296";
    String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}", userId, isbn);


    given()
        .log().uri()
        .log().method()
        .log().body()
        .contentType(JSON)
        .body(bookData)
        .when()
        .post("/BookStore/v1/Books")
        .then()
        .log().status()
        .log().body()
        .statusCode(401)
        .body("code", is("1200"))
        .body("message", is("User not authorized!"));


  }

}

