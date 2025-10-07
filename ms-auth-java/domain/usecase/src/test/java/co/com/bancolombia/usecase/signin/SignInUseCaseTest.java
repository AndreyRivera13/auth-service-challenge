package co.com.bancolombia.usecase.signin;

import co.com.bancolombia.model.shared.common.exception.DomainError;
import co.com.bancolombia.model.session.Session;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignInUseCaseTest {

    private SignInRepository signInRepository;
    private UserRepository userRepository;
    private SignInUseCase useCase;

    @BeforeEach
    void setUp() {
        signInRepository = mock(SignInRepository.class);
        userRepository = mock(UserRepository.class);
        useCase = new SignInUseCase(signInRepository, userRepository);
    }

    @Test
    void signinFeliz_guardaSesionYRetornaSession() {
        String email = "user@mail.com";
        String pwd = "secret";
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(new User(email, pwd)));
        when(signInRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.execute(new User(email, pwd), null))
                .assertNext(session -> {
                    assertNotNull(session.getSessionId());
                    assertEquals(email, session.getEmail());
                })
                .verifyComplete();

        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(signInRepository).save(captor.capture());
        assertEquals(email, captor.getValue().getEmail());
        assertNotNull(captor.getValue().getSessionId());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void usuarioNoExiste_emiteDomainError_yNoGuardaSesion() {
        String email = "nouser@mail.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(new User(email, "x"), null))
                .expectError(DomainError.class)
                .verify();

        verify(userRepository).findByEmail(email);
        verify(signInRepository, never()).save(any());
    }

    @Test
    void credencialesInvalidas_emiteDomainError_yNoGuardaSesion() {
        String email = "user@mail.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(new User(email, "correcta")));

        StepVerifier.create(useCase.execute(new User(email, "incorrecta"), null))
                .expectError(DomainError.class)
                .verify();

        verify(userRepository).findByEmail(email);
        verify(signInRepository, never()).save(any());
    }
}
