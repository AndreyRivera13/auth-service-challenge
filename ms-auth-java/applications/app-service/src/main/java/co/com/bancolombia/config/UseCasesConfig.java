package co.com.bancolombia.config;

import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.signin.SignInUseCase;
import co.com.bancolombia.usecase.signup.SignUpUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.bancolombia.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public SignInUseCase signInUseCase(UserRepository userRepository, SessionRepository sessionRepository) {
        return new SignInUseCase(userRepository, sessionRepository);
    }

    @Bean
    public SignUpUseCase signUpUseCase(UserRepository userRepository) {
        return new SignUpUseCase(userRepository);
    }
}
