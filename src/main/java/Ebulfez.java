import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class Ebulfez {

    Faker faker = new Faker();
    RequestSpecification reqSpec;
    String incidentTypeId = "";
    String incidentTypeName = "";
    int minNegativeScore;
    int maxNegativeScore;

    @BeforeClass
    public void Setup() {
        RestAssured.baseURI = "https://test.mersys.io/";
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userCredential)
                .when()
                .post("/auth/login");

        response.then().statusCode(200);
        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(response.detailedCookies())
                .build();
    }

    @Test
    public void ListIncidentTypes() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("schoolId", "12345"); // Replace with a valid school ID

        RestAssured.given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post("/api/api/incident-type/search")
                .then()
                .log().all()
                .statusCode(200)
                .time(Matchers.lessThan(2000L));
    }

    @Test
    public void AddIncidentType() {
        incidentTypeName = faker.lorem().word();
        minNegativeScore = faker.number().numberBetween(1, 10);
        maxNegativeScore = faker.number().numberBetween(11, 100);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("schoolId", "12345"); // Replace with a valid school ID
        requestBody.put("name", incidentTypeName);
        requestBody.put("minNegativeScore", minNegativeScore);
        requestBody.put("maxNegativeScore", maxNegativeScore);

        Response response = RestAssured.given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .post("/api/api/incident-type");

        response.then().statusCode(201).log().body();
        incidentTypeId = response.path("id");
    }

    @Test(dependsOnMethods = {"AddIncidentType"})
    public void EditIncidentType() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", incidentTypeId);
        requestBody.put("name", faker.lorem().word());
        requestBody.put("minNegativeScore", minNegativeScore + 1);
        requestBody.put("maxNegativeScore", maxNegativeScore + 1);

        RestAssured.given()
                .spec(reqSpec)
                .body(requestBody)
                .when()
                .put("/api/api/incident-type")
                .then()
                .statusCode(200)
                .log().body();
    }

    @Test(dependsOnMethods = {"EditIncidentType"})
    public void DeleteIncidentType() {
        RestAssured.given()
                .spec(reqSpec)
                .pathParam("id", incidentTypeId)
                .when()
                .delete("/api/api/incident-type/{id}")
                .then()
                .statusCode(Matchers.oneOf(200, 204))
                .log().body();
    }

    @Test(dependsOnMethods = {"DeleteIncidentType"})
    public void DeleteInvalidIncidentType() {
        RestAssured.given()
                .spec(reqSpec)
                .pathParam("id", "invalid-id")
                .when()
                .delete("/api/api/incident-type/{id}")
                .then()
                .statusCode(400)
                .body("message", Matchers.equalTo("Incident Type Not Found"))
                .log().body();
    }
}

