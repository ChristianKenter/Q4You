package app.entities;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ClientRestAssuredTest {

    private String clientName;
    private final String clientPassword = "TestPassword123";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
    }

    @BeforeEach
    void setUpEach() {
        clientName = "test" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void registerClientSuccessfully() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(200)
                .header("Set-Cookie", containsString("auth="));
    }

    @Test
    void registerClientWithoutUsername() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(400);
    }

    @Test
    void registerClientWithoutPassword() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .when()
                .post("/client/register")
                .then()
                .statusCode(400);
    }

    @Test
    void registerDuplicateClient() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(200);

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(400);
    }

    @Test
    void loginClientSuccessfully() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(200);

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/login")
                .then()
                .statusCode(200)
                .header("Set-Cookie", containsString("auth="));
    }

    @Test
    void loginClientWithIncorrectPassword() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/register")
                .then()
                .statusCode(200);

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .formParam("clientPassword", "WrongPassword")
                .when()
                .post("/client/login")
                .then()
                .statusCode(401);
    }

    @Test
    void loginClientWithoutUsername() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientPassword", clientPassword)
                .when()
                .post("/client/login")
                .then()
                .statusCode(400);
    }

    @Test
    void loginClientWithoutPassword() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("clientName", clientName)
                .when()
                .post("/client/login")
                .then()
                .statusCode(400);
    }

    @Test
    void logoutClientSuccessfully() {
        given()
                .redirects().follow(false)
                .when()
                .post("/client/logout")
                .then()
                .statusCode(302)
                .header("Location", equalTo("/auth/login"));
    }
}
