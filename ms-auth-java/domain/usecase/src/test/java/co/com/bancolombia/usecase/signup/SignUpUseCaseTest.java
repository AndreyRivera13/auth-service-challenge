package co.com.bancolombia.usecase.signup;

import co.com.bancolombia.model.exception.DomainError;
import co.com.bancolombia.model.signup.gateways.SignUpRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;


class SignUpUseCaseTest {

    private SignUpRepository signUpRepository;
    private UserRepository userRepository;
    private SignUpUseCase useCase;

    @BeforeEach
    void setUp() {
        signUpRepository = mock(SignUpRepository.class);
        userRepository = mock(UserRepository.class);
        useCase = new SignUpUseCase(signUpRepository, userRepository);
    }

    @Test
    void signupFeliz_guardaUsuario_yCompleta() {
        String email = "user@mail.com";
        String pwd = "passwordLargo";
        User user = new User(email, pwd, "User Name");

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());
        when(signUpRepository.save(user)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(user, null))
                .verifyComplete();

        verify(userRepository).findByEmail(email);
        verify(signUpRepository).save(user);
    }

    @Test
    void emailDuplicado_emiteDomainError_yNoGuarda() {
        String email = "user@mail.com";
        String pwd = "passwordLargo";
        User existing = new User(email, pwd, "User Name");

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.execute(new User(email, pwd, "New Name"), null))
                .expectError(DomainError.class)
                .verify();

        verify(userRepository).findByEmail(email);
        verify(signUpRepository, never()).save(any());
    }

    @Test
    void emailInvalido_emiteDomainError_yNoConsultaNiGuarda() {
        User user = new User("invalido@@mail", "passwordLargo", "User Name");

        StepVerifier.create(useCase.execute(user, null))
                .expectError(DomainError.class)
                .verify();

        verifyNoInteractions(userRepository, signUpRepository);
    }

    @Test
    void passwordDebil_emiteDomainError_yNoConsultaNiGuarda() {
        User user = new User("user@mail.com", "short", "User Name");

        StepVerifier.create(useCase.execute(user, null))
                .expectError(DomainError.class)
                .verify();

        verifyNoInteractions(userRepository, signUpRepository);
    }
}
