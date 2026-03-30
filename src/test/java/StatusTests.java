import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.hasKey;

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



public class StatusTests {

// API тесты  на библиотеке RestAssured


  /* 1) Сделать запрос к данному сайту: "https://selenoid.autotests.cloud/status"
     2) Получить ответ: {"total": 5,"used": 0,"queued": 0,"pending": 0,"browsers" ...
     3) Проверить: total = 5; */


    // Делает запрос через get и проверяет .body("total", is(5))
    @Test
    void checkTotal5() {
        get("https://selenoid.autotests.cloud/status")
            .then()
            .body("total", is(5));


}


    // Делает запрос через get и проверяет .body("total", is(5)), добавлено логирование
    @Test
    void checkTotalWithResponseLogs() {
        get("https://selenoid.autotests.cloud/status")
            .then()
            .log().all()
            .body("total", is(5));
    }



    // Делает запрос через get и проверяет .body("total", is(5))
    // Добавили логирование ДО запроса и после
    @Test
    void checkTotalWithLogs() {
        given()
            .log().all()
            .get("https://selenoid.autotests.cloud/status")
            .then()
            .log().all()
            .body("total", is(5));
    }



    // .log().uri() — логирует только URL запроса
    // .log().body() — логирует только тело ответа
    @Test
    void checkTotalWithSomeLogs() {
        given()
            .log().uri()
            .get("https://selenoid.autotests.cloud/status")
            .then()
            .log().body()
            .body("total",is(5));
    }



    /* Здесь проверяется сразу несколько вещей:
    Статус-код ответа = 200 (OK)
    total = 5
    В объекте browsers.chrome есть ключ (версия) "128.0"
    В объекте browsers.firefox есть ключ "125.0"
    Используется hasKey() из Hamcrest — удобная проверка наличия ключа в JSON. */
    @Test
    void checkTotalWithStatusLogs() {
        given()
            .log().uri()
            .get("https://selenoid.autotests.cloud/status")
            .then()
            .log().status()
            .log().body()
            .statusCode(200)
            .body("total", is(5))
            .body("browsers.chrome", hasKey("128.0"))
            .body("browsers.firefox", hasKey("125.0"));
    }
}
