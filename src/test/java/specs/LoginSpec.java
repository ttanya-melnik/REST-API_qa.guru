package specs;

import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

import helpers.CustomAllureListener;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class LoginSpec {

// loginRequestSpec — спецификация запроса для успешного логина
  public static RequestSpecification loginRequestSpec = with()
      .filter(CustomAllureListener.withCustomTemplates())
      .log().uri()
      .log().body()
      .log().headers()
      .contentType(JSON)
//            .baseUri("https://reqres.in")
      .basePath("/api/login");


 // loginResponseSpec — проверка успешного ответа.
  public static ResponseSpecification loginResponseSpec = new ResponseSpecBuilder()
      .expectStatusCode(200)
      .log(STATUS)
      .log(BODY)
      .build();


  // missingPasswordResponseSpec — проверка ошибки.
  public static ResponseSpecification missingPasswordResponseSpec = new ResponseSpecBuilder()
      .expectStatusCode(400)
      .log(STATUS)
      .log(BODY)
      .build();
}
