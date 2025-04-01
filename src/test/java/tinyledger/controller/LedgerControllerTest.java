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
import tinyledger.dto.TransactionRequest;
import tinyledger.dto.TransferRequest;
import tinyledger.model.Account;
import tinyledger.model.Transaction;
import tinyledger.service.LedgerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LedgerControllerTest {
    @LocalServerPort
    private int port;

    @MockBean
    private LedgerService ledgerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testAccount = new Account("User Name", "user1", BigDecimal.ZERO);
        testTransaction = new Transaction("tx123", "User Name", new BigDecimal("100.00"), 
            Transaction.TransactionType.DEPOSIT, "Test transaction", LocalDateTime.now());
    }

    @Test
    void deposit_WithValidRequest_ShouldReturnTransaction() throws Exception {
        // Given
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "Initial deposit");
        when(ledgerService.recordTransaction(eq("User Name"), any(), eq(Transaction.TransactionType.DEPOSIT), any()))
            .thenReturn(testTransaction);

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/transactions/User Name/deposit")
            .then()
            .statusCode(200)
            .body("id", equalTo("tx123"))
            .body("accountId", equalTo("User Name"))
            .body("amount", equalTo(100.00f))
            .body("type", equalTo("DEPOSIT"))
            .body("description", equalTo("Test transaction"))
            .body("timestamp", notNullValue());
    }

    @Test
    void withdraw_WithValidRequest_ShouldReturnTransaction() throws Exception {
        // Given
        TransactionRequest request = new TransactionRequest(new BigDecimal("50.00"), "Withdrawal");
        Transaction withdrawalTx = new Transaction("tx123", "User Name", new BigDecimal("50.00"), 
            Transaction.TransactionType.WITHDRAWAL, "Withdrawal", LocalDateTime.now());
        when(ledgerService.recordTransaction(eq("User Name"), any(), eq(Transaction.TransactionType.WITHDRAWAL), any()))
            .thenReturn(withdrawalTx);

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/transactions/User Name/withdraw")
            .then()
            .statusCode(200)
            .body("id", equalTo("tx123"))
            .body("accountId", equalTo("User Name"))
            .body("amount", equalTo(50.00f))
            .body("type", equalTo("WITHDRAWAL"))
            .body("description", equalTo("Withdrawal"))
            .body("timestamp", notNullValue());
    }

    @Test
    void withdraw_WithInsufficientFunds_ShouldReturnBadRequest() throws Exception {
        // Given
        TransactionRequest request = new TransactionRequest(new BigDecimal("100.00"), "Withdrawal");
        when(ledgerService.recordTransaction(eq("User Name"), any(), eq(Transaction.TransactionType.WITHDRAWAL), any()))
            .thenThrow(new IllegalArgumentException("Insufficient funds"));

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/transactions/User Name/withdraw")
            .then()
            .statusCode(400)
            .body("message", equalTo("Insufficient funds"))
            .body("status", equalTo(400));
    }

    @Test
    void transfer_WithValidRequest_ShouldReturnTransactions() throws Exception {
        // Given
        TransferRequest request = new TransferRequest("User Name", "Jane Doe", new BigDecimal("50.00"), "Transfer");
        Transaction withdrawalTx = new Transaction("tx1", "User Name", new BigDecimal("50.00"), 
            Transaction.TransactionType.WITHDRAWAL, "Transfer to Jane Doe: Transfer", LocalDateTime.now());
        Transaction depositTx = new Transaction("tx2", "Jane Doe", new BigDecimal("50.00"), 
            Transaction.TransactionType.DEPOSIT, "Transfer from User Name: Transfer", LocalDateTime.now());
        when(ledgerService.transferMoney(eq("User Name"), eq("Jane Doe"), any(), any()))
            .thenReturn(List.of(withdrawalTx, depositTx));

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/transactions/transfer")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].id", equalTo("tx1"))
            .body("[0].accountId", equalTo("User Name"))
            .body("[0].amount", equalTo(50.00f))
            .body("[0].type", equalTo("WITHDRAWAL"))
            .body("[0].description", equalTo("Transfer to Jane Doe: Transfer"))
            .body("[1].id", equalTo("tx2"))
            .body("[1].accountId", equalTo("Jane Doe"))
            .body("[1].amount", equalTo(50.00f))
            .body("[1].type", equalTo("DEPOSIT"))
            .body("[1].description", equalTo("Transfer from User Name: Transfer"));
    }

    @Test
    void transfer_WithInsufficientFunds_ShouldReturnBadRequest() throws Exception {
        // Given
        TransferRequest request = new TransferRequest("User Name", "Jane Doe", new BigDecimal("100.00"), "Transfer");
        when(ledgerService.transferMoney(eq("User Name"), eq("Jane Doe"), any(), any()))
            .thenThrow(new IllegalArgumentException("Insufficient funds"));

        // When/Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/transactions/transfer")
            .then()
            .statusCode(400)
            .body("message", equalTo("Insufficient funds"))
            .body("status", equalTo(400));
    }

    @Test
    void getTransactions_WithValidAccountId_ShouldReturnTransactions() throws Exception {
        // Given
        when(ledgerService.getTransactionHistory("User Name"))
            .thenReturn(List.of(testTransaction));

        // When/Then
        given()
            .when()
            .get("/api/transactions/User Name")
            .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].id", equalTo("tx123"))
            .body("[0].accountId", equalTo("User Name"))
            .body("[0].amount", equalTo(100.00f))
            .body("[0].type", equalTo("DEPOSIT"))
            .body("[0].description", equalTo("Test transaction"))
            .body("[0].timestamp", notNullValue());
    }

    @Test
    void getTransactions_WithInvalidAccountId_ShouldReturnNotFound() throws Exception {
        // Given
        when(ledgerService.getTransactionHistory("Invalid Account"))
            .thenThrow(new IllegalArgumentException("Account not found"));

        // When/Then
        given()
            .when()
            .get("/api/transactions/Invalid Account")
            .then()
            .statusCode(404)
            .body("message", equalTo("Account not found"))
            .body("status", equalTo(404));
    }
} 