import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class Kadriye {

    Faker randomUreteci = new Faker();
    RequestSpecification reqSpec;
    String countryName = "";
    String countryCode = "";
    String countryId = "";
    String stateName = "";
    String stateShortName = "";
    String StateId = "";

    public Kadriye() {
    }

    @BeforeClass
    public void Setup() {
        RestAssured.baseURI = "https://test.mersys.io/";
        Map<String, String> userCredential = new HashMap();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies = ((Response)((ValidatableResponse)((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().contentType(ContentType.JSON).body(userCredential).when().post("/auth/login", new Object[0])).then()).log().all()).statusCode(200)).extract().response()).detailedCookies();
        this.reqSpec = (new RequestSpecBuilder()).setContentType(ContentType.JSON).addCookies(cookies).build();
    }

    @Test
    public void CreateCountry() {
        String var10001 = this.randomUreteci.address().country();
        this.countryName = var10001 + this.randomUreteci.number().digits(5);
        var10001 = this.randomUreteci.address().countryCode();
        this.countryCode = var10001 + this.randomUreteci.number().digits(5);
        Map<String, String> createCountry = new HashMap();
        createCountry.put("name", this.countryName);
        createCountry.put("code", this.countryCode);
        createCountry.put("hasState", "true");
        createCountry.put("translateName", (String) null);
        this.countryId = (String)((ValidatableResponse)((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().spec(this.reqSpec).body(createCountry).when().post("school-service/api/countries", new Object[0])).then()).log().body()).statusCode(201)).extract().path("id", new String[0]);
    }

    @Test
    public void ListState() {
        ((ValidatableResponse)((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().spec(this.reqSpec).when().get("school-service/api/states", new Object[0])).then()).log().all()).statusCode(200)).time(Matchers.lessThan(2000L));
    }

    @Test(
            dependsOnMethods = {"CreateCountry"}
    )
    public void AddState() {
        this.stateName = this.randomUreteci.address().streetName();
        this.stateShortName = this.randomUreteci.address().firstName();
        Map<String, Object> createState = new HashMap();
        createState.put("name", this.stateName);
        createState.put("shortName", this.stateShortName);
        createState.put("country", Map.of("id", this.countryId));
        createState.put("translateName", new Object[0]);
        Response response = (Response)((ValidatableResponse)((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().spec(this.reqSpec).body(createState).when().post("school-service/api/states", new Object[0])).then()).log().body()).statusCode(201)).extract().response();
        this.StateId = (String)response.path("id", new String[0]);
        this.stateName = (String)response.path("name", new String[0]);
    }

    @Test(
            dependsOnMethods = {"AddState"}
    )
    public void EditState() {
        Map<String, Object> countryData = new HashMap();
        countryData.put("id", this.countryId);
        Map<String, Object> updateState = new HashMap();
        updateState.put("id", this.StateId);
        updateState.put("name", this.stateName);
        updateState.put("shortName", this.randomUreteci.address().firstName());
        updateState.put("country", countryData);
        updateState.put("translateName", new Object[0]);
        ((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().spec(this.reqSpec).body(updateState).when().put("school-service/api/states/", new Object[0])).then()).log().body()).statusCode(200);
    }

    @Test(
            dependsOnMethods = {"EditState"}
    )
    public void deleteState() {
        ((ValidatableResponse)((ValidatableResponse)((Response)RestAssured.given().spec(this.reqSpec).pathParam("StateId", this.StateId).log().uri().when().delete("school-service/api/states/{StateId}", new Object[0])).then()).log().body()).statusCode(200);
        System.out.println("Eyalet silindi.ID: " + this.StateId);
    }
}



