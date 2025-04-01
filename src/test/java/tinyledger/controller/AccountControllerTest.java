package tinyledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import tinyledger.TinyLedgerApplication;
import tinyledger.dto.CreateAccountRequest;
import tinyledger.model.Account;
import tinyledger.service.LedgerService;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {
    @LocalServerPort
    private int port;

    @MockBean
    private LedgerService ledgerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testAccount = new Account("User Name", "user1", BigDecimal.ZERO);
    }

    @Test
    void createAccount_WithValidRequest_ShouldReturnCreatedAccount() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest("User Name");
        when(ledgerService.createAccount(any())).thenReturn(testAccount);

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/accounts")
            .then()
            .statusCode(200)
            .body("id", equalTo("User Name"))
            .body("name", equalTo("user1"))
            .body("balance", equalTo(0));
    }

    @Test
    void createAccount_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest("");

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/accounts")
            .then()
            .statusCode(400)
            .body("message", equalTo("Account name cannot be empty"))
            .body("status", equalTo(400));
    }

    @Test
    void getAccount_WithValidId_ShouldReturnAccount() throws Exception {
        // Given
        when(ledgerService.getAccount("User Name")).thenReturn(testAccount);

        // When/Then
        given()
            .when()
            .get("/api/accounts/User Name")
            .then()
            .statusCode(200)
            .body("id", equalTo("User Name"))
            .body("name", equalTo("user1"))
            .body("balance", equalTo(0));
    }

    @Test
    void getAccount_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(ledgerService.getAccount("Invalid Account"))
            .thenThrow(new IllegalArgumentException("Account not found"));

        // When/Then
        given()
            .when()
            .get("/api/accounts/Invalid Account")
            .then()
            .statusCode(404)
            .body("message", equalTo("Account not found"))
            .body("status", equalTo(404));
    }

    @Test
    void getAllAccounts_ShouldReturnListOfAccounts() throws Exception {
        // Given
        Account account1 = new Account("User Name1", "user1", BigDecimal.ZERO);
        Account account2 = new Account("User Name2", "user2", BigDecimal.ZERO);
        when(ledgerService.getAllAccounts()).thenReturn(List.of(account1, account2));

        // When/Then
        given()
            .when()
            .get("/api/accounts")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].id", equalTo("User Name1"))
            .body("[0].name", equalTo("user1"))
            .body("[0].balance", equalTo(0))
            .body("[1].id", equalTo("User Name2"))
            .body("[1].name", equalTo("user2"))
            .body("[1].balance", equalTo(0));
    }
} 