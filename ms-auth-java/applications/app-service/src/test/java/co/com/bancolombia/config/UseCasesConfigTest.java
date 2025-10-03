package co.com.bancolombia.config;

import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.signup.gateways.SignUpRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.signin.SignInUseCase;
import co.com.bancolombia.usecase.signup.SignUpUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepository userRepository() {
            return org.mockito.Mockito.mock(UserRepository.class);
        }

        @Bean
        public SessionRepository sessionRepository() {
            return org.mockito.Mockito.mock(SessionRepository.class);
        }

        @Bean
        public SignInRepository signInRepository() {
            return org.mockito.Mockito.mock(SignInRepository.class);
        }

        @Bean
        public SignUpRepository signUpRepository() {
            return org.mockito.Mockito.mock(SignUpRepository.class);
        }

        @Bean
        public SignInUseCase signInUseCase(SignInRepository signInRepository, SessionRepository sessionRepository) {
            return new SignInUseCase(signInRepository, sessionRepository);
        }

        @Bean
        public SignUpUseCase signUpUseCase(SignUpRepository signUpRepository) {
            return new SignUpUseCase(signUpRepository);
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}