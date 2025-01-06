import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class Yuksel extends Parent{
    private String cityId;

    @Test
    public void createCities() {
        Map<String, Object> cities = new LinkedHashMap<>();
        cities.put("id", null);
        cities.put("name", "RussiaCity");
        cities.put("shortName", "GermanCity");

        Map<String, String> countryId = new LinkedHashMap<>();
        countryId.put("id", "63aa9b3a1c58aa2af1a6ab19");
        cities.put("country", countryId);

        cities.put("translateName", new String[]{});

        String cityId = given()
                .spec(reqSpec)
                .body(cities)
                .when()
                .post("/school-service/api/cities")
                .then()
                .statusCode(201)
                .assertThat().time(lessThan(1000L))
                .log().body()
                .extract().path("id");

        cities.put("id",cityId);
    }

    @Test(dependsOnMethods = "createCities")
    public void updateCities() {
        Map<String, Object> cities = new LinkedHashMap<>();
        cities.put("id", "676c522c8f874a2932dd0a61");

        Map<String, String> countryId = new LinkedHashMap<>();
        countryId.put("id", "63aa9b3a1c58aa2af1a6ab19");
        cities.put("country", countryId);

        cities.put("translateName", new String[]{});

        given()
                .spec(reqSpec)
                .body(cities)
                .when()
                .put("/school-service/api/cities")
                .then()
                .statusCode(200)
                .assertThat().time(lessThan(1000L))
                .log().body();
    }

    @Test(dependsOnMethods = "updateCities")
    public void deleteCities() {
        given()
                .spec(reqSpec)
                .pathParam("id", cityId)
                .when()
                .delete("/school-service/api/cities/" + cityId)
                .then()
                .statusCode(200);
    }
}
