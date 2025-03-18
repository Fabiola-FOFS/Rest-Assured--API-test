import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import Entities.Booking;
import Entities.BookingDates;
import Entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BookingTests {

    public static Faker faker;
    private static RequestSpecification request;
    private static Booking booking;
    private static BookingDates bookingDates;
    private static User user;

    @BeforeAll
    public static void Setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        faker = new Faker();
        user = new User(faker.name().username(),
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().safeEmailAddress(),
                faker.internet().password(8, 10),
                faker.phoneNumber().toString());

        bookingDates = new BookingDates("2025-03-07", "2025-03-25");
        booking = new Booking(user.getFirstName(), user.getLastName(),
                (float) faker.number().randomDouble(2, 50, 100000),
                true, bookingDates,
                "");
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new ErrorLoggingFilter());
    }

    @BeforeEach
    void setRequest() {
        request = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM="); // Usar a autorização padrão
    }

    @Test
    public void getAllBookingsById_returnOk() {
        Response response = request
                .when()
                .get("/booking")
                .then()
                .extract()
                .response();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void getAllBookingsByUserFirstName_BookingExists_returnOk() {
        request
                .when()
                .queryParam("firstName", "Fabiola")
                .get("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("results", hasSize(greaterThan(0)));
    }

    @Test
    public void CreateBooking_WithValidData_returnOk() {
        Booking test = booking;
        given()
                .config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .body(test)
                .when()
                .post("/booking")
                .then()
                .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                .and()
                .assertThat()
                .statusCode(200) // Mudança para 200
                .contentType(ContentType.JSON)
                .and()
                .body("bookingid", greaterThan(0)) // Verifica se o bookingid foi retornado
                .and().time(lessThan(3000L));
    }

    @Test
    public void UpdateBooking_WithValidData_returnOk() {
        // Criando uma nova reserva para obter um ID válido
        Response response = given()
                .config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .extract()
                .response();

        // Extraindo o ID da reserva criada
        int bookingId = response.jsonPath().getInt("bookingid");
        Assertions.assertNotNull(bookingId, "O ID da reserva não deve ser nulo."); // Verificação do bookingId

        // Criando novos dados para a atualização
        Booking updatedBooking = new Booking(
                "NovoNome",
                "NovoSobrenome",
                (float) faker.number().randomDouble(2, 200, 5000),
                false,
                new BookingDates("2025-06-01", "2025-06-10"),
                "Café da manhã incluso"
        );

        // Fazendo a requisição PUT para atualizar a reserva
        given()
                .config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=") // Incluindo o cabeçalho de autorização
                .body(updatedBooking)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .assertThat()
                .statusCode(200) // Verifique se o código de status está correto
                .contentType(ContentType.JSON)
                .and()
                .body("firstname", org.hamcrest.Matchers.equalTo("NovoNome"))
                .body("lastname", org.hamcrest.Matchers.equalTo("NovoSobrenome"))
                .body("totalprice", greaterThan(199)) // Verifica se o novo preço foi atualizado
                .body("depositpaid", org.hamcrest.Matchers.equalTo(false))
                .body("bookingdates.checkin", org.hamcrest.Matchers.equalTo("2025-06-01"))
                .body("bookingdates.checkout", org.hamcrest.Matchers.equalTo("2025-06-10"))
                .body("additionalneeds", org.hamcrest.Matchers.equalTo("Café da manhã incluso"));
    }

    @Test
    public void DeleteBooking_WithValidId_returnOk() {
        // Primeiro, crie uma reserva para obter um ID válido
        Response response = given()
                .config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .extract()
                .response();

        // Extraindo o ID da reserva criada
        int bookingId = response.jsonPath().getInt("bookingid");
        Assertions.assertNotNull(bookingId, "O ID da reserva não deve ser nulo."); // Verificação do bookingId

        // Fazendo a requisição DELETE para excluir a reserva
        given()
                .config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=") // Incluindo o cabeçalho de autorização
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .assertThat()
                .statusCode(201); // Verifique qual status code é retornado após a exclusão (geralmente 204)
    }
}
