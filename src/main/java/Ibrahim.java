

import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Ibrahim extends Parent {

    String GroupName = "";
    String GroupDescription = "";
    String GroupID = "";
    String SchoolID="6576fd8f8af7ce488ac69b89";

    @Test
    public void CreateStudentGroups() {

        GroupName = randomUreteci.lorem().word() + randomUreteci.number().digits(5);
        GroupDescription = randomUreteci.lorem().sentence() + randomUreteci.number().digits(5);

        Map<String, String> newGroup = new HashMap<>();
        newGroup.put("name", GroupName);
        newGroup.put("description", GroupDescription);
        newGroup.put("schoolId",SchoolID );


        GroupID=
                given()
                        .spec(reqSpec)
                        .body(newGroup)
                        .log().uri()

                        .when()
                        .post("school-service/api/student-group")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "CreateStudentGroups")
    public void CreateStudentGroupsNegative() {

        Map<String, String> newGroup = new HashMap<>();

        newGroup.put("name", GroupName);
        newGroup.put("description", GroupDescription);
        newGroup.put("schoolId", SchoolID);


        given()
                .spec(reqSpec)
                .body(newGroup)
                .log().uri()

                .when()
                .post("school-service/api/student-group")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsStringIgnoringCase("already"))
        ;
    }

    @Test(dependsOnMethods = "CreateStudentGroupsNegative")
    public void EditStudentGroups() {

        GroupName= "Team5_Grubu_"+randomUreteci.number().digits(5);
        GroupDescription="Team5_Aciklama_"+randomUreteci.number().digits(5);

        Map<String, String> editGroup = new HashMap<>();

        editGroup.put("id", GroupID);
        editGroup.put("name", GroupName);
        editGroup.put("description", GroupDescription);
        editGroup.put("schoolId", SchoolID);


        given()
                .spec(reqSpec)
                .body(editGroup)
                .log().uri()

                .when()
                .put("school-service/api/student-group")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(GroupName))
                .body("description",equalTo(GroupDescription))
        ;
    }

    @Test(dependsOnMethods = "EditStudentGroups")
    public void DeleteStudentGroups() {

        Map<String, String> deleteGroup = new HashMap<>();
        deleteGroup.put("id", GroupID);

        given()
                .spec(reqSpec)
                .body(deleteGroup)
                .pathParam("GroupID",GroupID)
                .log().uri()

                .when()
                .delete("school-service/api/student-group/{GroupID}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "DeleteStudentGroups")
    public void DeleteStudentGroupsNegative() {

        Map<String, String> deleteGroup = new HashMap<>();
        deleteGroup.put("id", GroupID);

        given()
                .spec(reqSpec)
                .body(deleteGroup)
                .pathParam("GroupID",GroupID)
                .log().uri()

                .when()
                .delete("school-service/api/student-group/{GroupID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }



}

