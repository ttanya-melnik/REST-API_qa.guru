import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;



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


public class LoginTests {

  // API тесты на библиотеке RestAssured (тесты авторизации на reqres.in)

  /* 1) Сделать POST запрос к данному адресу: https://reqres.in/api/login
 передаём связку: {"email": "eve.holt@reqres.in", "password": "cityslicka"}
     2) Получить ответ: {"token": "QpwL5tke4Pnpja7X4"}
     3) В ответе проверить следующие данные: "QpwL5tke4Pnpja7X4" и status code 200
     */


  // Успешный сценарий. Отправляет валидные данные.
  @Test
  void successfulLoginTest() {
    String authData = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\"}";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    given()
        .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body("token", is("QpwL5tke4Pnpja7X4"));
  }

  // Негативный тест. Отправляет пустое тело → ожидает 400 и ошибку "Missing email or username"
  @Test
  void unsuccessfulLogin400Test() {
    String authData = "reqres_01b6fbde581f4ce3be4ec935facf4736";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    given()
        .header("x-api-key", apiKey)
        .auth().oauth2(authData)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is("Missing email or username"));
  }


  // Негативный тест. Неправильный email → 400 + "user not found"
  @Test
  void userNotFoundTest() {
    String authData = "{\"email\": \"eveasdas.holt@reqres.in\", \"password\": \"cda\"}";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    given()
        .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is("user not found"));
  }


  // Есть email, но нет пароля → 400 + "Missing password"
  @Test
  void missingPasswordTest() {
    String authData = "{\"email\": \"eveasdas.holt@reqres.in\"}";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";


    given()
        .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is("Missing password"));
  }



  // Есть пароль, но нет email → 400 + "Missing email or username"
  @Test
  void missingLoginTest() {
    String authData = "{\"password\": \"cda\"}";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";

    given()
        .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is("Missing email or username"));
  }


  // Кривое тело (%}) → проверяет 400 (без конкретной проверки сообщения)
  @Test
  void wrongBodyTest() {
    String authData = "%}";
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";
    given()
        .header("x-api-key", apiKey)
        .body(authData)
        .contentType(JSON)
        .log().uri()

        .when()
        .post("https://reqres.in/api/login")

        .then()
        .log().status()
        .log().body()
        .statusCode(400);
  }


  // Отправляет POST безContent-Type: application/json → ожидает 415 (Unsupported Media Type)
  @Test
  void unsuccessfulLogin415Test() {
    String apiKey = "reqres_01b6fbde581f4ce3be4ec935facf4736";
    given()
        .header("x-api-key", apiKey)
        .log().uri()
        .post("https://reqres.in/api/login")
        .then()
        .log().status()
        .log().body()
        .statusCode(415);
  }

}
