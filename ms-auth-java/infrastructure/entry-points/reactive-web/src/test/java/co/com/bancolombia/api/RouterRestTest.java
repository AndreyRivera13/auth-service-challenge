package co.com.bancolombia.api;

import co.com.bancolombia.filter.HeadersValidation;
import co.com.bancolombia.usecase.signin.SignInUseCase;
import co.com.bancolombia.usecase.signup.SignUpUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {
    @MockBean
    private HeadersValidation headersValidation;

    @MockBean
    private SignInUseCase signInUseCase;

    @MockBean
    private SignUpUseCase signUpUseCase;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void signupPOSTUseCase() {
        when(headersValidation.validateHeaders(ArgumentMatchers.any()))
                .thenReturn(Mono.empty());
        when(signUpUseCase.execute(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Mono.just(""));

        webTestClient.post()
                .uri("/api/v1/signup")
                .contentType(MediaType.APPLICATION_JSON) // <- Agrega esta línea
                .accept(MediaType.APPLICATION_JSON)
                .header("message-id", "123e4567-e89b-12d3-a456-426614174000")
                .header("consumer-code", "test-consumer")
                .bodyValue("{\"name\":\"Test\",\"email\":\"test@test.com\",\"password\":\"1234\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();

    }

    @Test
    void signinPOSTUseCase() {
        when(headersValidation.validateHeaders(ArgumentMatchers.any()))
                .thenReturn(Mono.empty());
        when(signInUseCase.execute(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("message-id", "123e4567-e89b-12d3-a456-426614174000")
                .header("consumer-code", "test-consumer")
                .bodyValue("{\"email\":\"test@test.com\",\"password\":\"12345678\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }
}
