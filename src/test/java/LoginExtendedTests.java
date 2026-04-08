import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.LoginSpec.loginRequestSpec;
import static specs.LoginSpec.missingPasswordResponseSpec;

import helpers.CustomAllureListener;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.LoginBodyLombokModel;
import models.LoginBodyModel;
import lombok.LoginResponseLombokModel;
import models.LoginResponseModel;
import models.MissingPasswordModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import specs.LoginSpec;



  /* Цепочка RestAssured:
  given() — подготовка запроса (body, headers, логирование)
  when() — действие (.post(), .get())
  then() — проверки и логирование ответа

    Разные способы логирования
  (.log().all(), .log().uri(), .log().body(), .log().status())
  — это очень помогает понимать, что именно уходит и приходит.

    Проверки:
  .statusCode(200)
  .body("поле", is("значение"))
  .body("объект", hasKey("ключ"))

  Позитивные + негативные тесты — хорошая практика.
  ContentType.JSON — важно указывать, когда отправляешь JSON в тело. */


public class LoginExtendedTests {

  // API тесты на библиотеке RestAssured (тесты авторизации на reqres.in)

  /* 1) Сделать POST запрос к данному адресу: https://reqres.in/api/login
 передаём связку: {"email": "eve.holt@reqres.in", "password": "cityslicka"}
     2) Получить ответ: {"token": "QpwL5tke4Pnpja7X4"}
     3) В ответе проверить следующие данные: "QpwL5tke4Pnpja7X4" и status code 200
     */


  @BeforeAll
  public static void setUp() {
    RestAssured.baseURI = "https://reqres.in";
  }

/* Плохая практика:
   передаём JSON как строку
   полный URL (хотя baseURI уже есть)
   проверка через Hamcrest */
  @Test
  void successfulLoginBadPracticeTest() {
    String authData = """
        {
        "email": "eve.holt@reqres.in",
        "password": "cityslicka"
        }
        """;
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";


    given()
     .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()
        .log().body()
        .log().headers()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body("token", is("QpwL5tke4Pnpja7X4"));
  }



  /* Использование обычных POJO-моделей
    Вместо строки создали объект класса LoginBodyModel
    Для ответа используется десериализация */
  @Test
  void successfulLoginPojoTest() {
    LoginBodyModel authData = new LoginBodyModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";


    LoginResponseModel response = given()
     .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()
        .log().body()
        .log().headers()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().as(LoginResponseModel.class);

    assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
  }





  /* Lombok-версия
 Что делает Lombok:
 Автоматически генерирует getters, setters, toString(), equals() и т.д.
 Код моделей становится очень коротким и чистым.
 Это современный стандарт в Java-проектах. */
  @Test
  void successfulLoginLombokTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    LoginResponseLombokModel response = given()
     .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()
        .log().body()
        .log().headers()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().as(LoginResponseLombokModel.class);

    assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
  }


  /* Добавили Allure


    filter(new AllureRestAssured())
   — стандартный фильтр, который автоматически добавляет в Allure-отчёт полный запрос и ответ
   */
  @Test
  void successfulLoginAllureTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    LoginResponseLombokModel response = given()
     .header("x-api-key", apiKey)
        .filter(new AllureRestAssured())
        .log().uri()
        .log().body()
        .log().headers()
        .body(authData)
        .contentType(JSON)

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().as(LoginResponseLombokModel.class);

    assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
  }


/* CustomAllureListener.withCustomTemplates()
   — кастомная версия. Позволяет менять внешний вид запроса/ответа в отчёте
   с помощью шаблонов Freemarker (файлы request.ftl и response.ftl).*/
  @Test
  void successfulLoginCustomAllureTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";


    LoginResponseLombokModel response = given()
     .header("x-api-key", apiKey)
        .filter(CustomAllureListener.withCustomTemplates())
        .log().uri()
        .log().body()
        .log().headers()
        .body(authData)
        .contentType(JSON)

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .extract().as(LoginResponseLombokModel.class);

    assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
  }



// Allure Steps — разбиваем тест на логические шаги
  @Test
  void successfulLoginWithStepsTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";


    LoginResponseLombokModel response = step("Make request", ()->
        given()
            .header("x-api-key", apiKey)
            .log().uri()
            .log().body()
            .log().headers() 
            .body(authData)
            .contentType(JSON)

            .when()
            .post("https://reqres.in/api/login")

            .then()
            .log().status()
            .log().body()
            .statusCode(200)
            .extract().as(LoginResponseLombokModel.class));

    step("Check response", ()->
        assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));
  }


/* Используем готовую спецификацию запроса: given(loginRequestSpec)
   Без пути — он уже в spec: .when().post()
   Готовая проверка ответа: .spec(LoginSpec.loginResponseSpec)
  */
  @Test
  void successfulLoginWithSpecsTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");
    authData.setPassword("cityslicka");
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    LoginResponseLombokModel response = step("Make request", ()->
       given(loginRequestSpec)
           .header("x-api-key", apiKey)
            .body(authData)

            .when()
            .post()

            .then()
            .spec(LoginSpec.loginResponseSpec)
            .extract().as(LoginResponseLombokModel.class));

    step("Check response", ()->
        assertEquals("QpwL5tke4Pnpja7X4", response.getToken()));
  }



  /* Проверяет негативный сценарий (отсутствует пароль).
Используется другая ResponseSpecification (missingPasswordResponseSpec),
которая ожидает статус 400 и десериализует ответ в MissingPasswordModel */
  @Test
  void missingPasswordTest() {
    LoginBodyLombokModel authData = new LoginBodyLombokModel();
    authData.setEmail("eve.holt@reqres.in");

    MissingPasswordModel response = step("Make request", ()->
        given(loginRequestSpec)
            .body(authData)

            .when()
            .post()

            .then()
            .spec(missingPasswordResponseSpec)
            .extract().as(MissingPasswordModel.class));

    step("Check response", ()->
        assertEquals("Missing password", response.getError()));
  }
}
