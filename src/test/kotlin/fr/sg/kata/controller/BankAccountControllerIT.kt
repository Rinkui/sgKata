package fr.sg.kata.controller

import fr.sg.kata.utils.FakeDateService
import org.example.BankApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = [BankApplication::class, FakeDateService::class])
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
class BankAccountControllerIT(@Autowired private val webTestClient: WebTestClient) {

    @Test
    fun `post deposit should succeed`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 10
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted
    }

    @Test
    fun `get specific amount withdrawal should succeed`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 50
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted

        webTestClient
            .get()
            .uri { it.path("/api/account/money").queryParam("amount", 10).build() }
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .json(
                """
                {
                  "date": "2025-01-07",
                  "balance": 40,
                  "amount": -10
                }
            """.trimIndent()
            )
    }

    @Test
    fun `get account statement should succeed`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 50
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted

        webTestClient
            .get()
            .uri("/api/account")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .json(
                """
                {
                  "history": [
                    {
                      "date": "2025-01-07",
                      "amount": 50.0,
                      "balance": 50.0
                    }
                  ]   
                }
            """.trimIndent()
            )
    }

    @Test
    fun `balance could be negative`() {
        webTestClient
            .get()
            .uri { it.path("/api/account/money").queryParam("amount", 150).build() }
            .exchange()
            .expectStatus()
            .isOk

        webTestClient
            .get()
            .uri("/api/account")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .json(
                """
                {
                  "history": [
                     {
                      "date": "2025-01-07",
                      "amount": -150.0,
                      "balance": -150.0
                    }
                  ]   
                }
            """.trimIndent()
            )
    }

    @Test
    fun `get history should return all transactions`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 25
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted

        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 36.54
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted

        webTestClient
            .get()
            .uri { it.path("/api/account/money").queryParam("amount", 1.25).build() }
            .exchange()
            .expectStatus()
            .isOk

        webTestClient
            .get()
            .uri("/api/account")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .json(
                """
                {
                  "history": [
                     {
                      "date": "2025-01-07",
                      "amount": 25.0,
                      "balance": 25.0
                    },
                    {
                      "date": "2025-01-07",
                      "amount": 36.54,
                      "balance": 61.54
                    },
                    {
                      "date": "2025-01-07",
                      "amount": -1.25,
                      "balance": 60.29
                    }
                  ]   
                }
            """.trimIndent()
            )
    }

    @Test
    fun `post negative deposit should fail`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": -33
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectBody()
            .json(
                """
                {
                  "message": "Deposit could not be negative or zero"
                }
            """.trimIndent()
            )
    }

    @Test
    fun `post zero deposit should fail`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 0
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectBody()
            .json(
                """
                {
                  "message": "Deposit could not be negative or zero"
                }
            """.trimIndent()
            )
    }

    @Test
    fun `get all money withdrawal should succeed`() {
        webTestClient
            .post()
            .uri("/api/account/money")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                  "amount": 50
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus()
            .isAccepted

        webTestClient
            .get()
            .uri { it.path("/api/account/money").build() }
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .json(
                """
                {
                  "date": "2025-01-07",
                  "balance": 0.0,
                  "amount": -50.0
                }
            """.trimIndent()
            )
    }

    @Test
    fun `get all money withdrawal when balance is negative should fail`() {
        webTestClient
            .get()
            .uri { it.path("/api/account/money").queryParam("amount", 100).build() }
            .exchange()
            .expectStatus()
            .isOk

        webTestClient
            .get()
            .uri { it.path("/api/account/money").build() }
            .exchange()
            .expectStatus()
            .is4xxClientError
            .expectBody()
            .json(
                """
                {
                  "message": "Could not retrieve money if balance is negative"
                }
            """.trimIndent()
            )
    }
}