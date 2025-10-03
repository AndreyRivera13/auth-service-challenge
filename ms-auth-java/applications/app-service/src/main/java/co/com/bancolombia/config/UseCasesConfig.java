package co.com.bancolombia.config;

import co.com.bancolombia.model.session.gateways.SessionRepository;
import co.com.bancolombia.model.signin.gateways.SignInRepository;
import co.com.bancolombia.model.signup.gateways.SignUpRepository;
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
    public SignInUseCase signInUseCase(SignInRepository signInRepository, SessionRepository sessionRepository) {
        return new SignInUseCase(signInRepository, sessionRepository);
    }

    @Bean
    public SignUpUseCase signUpUseCase(SignUpRepository signUpRepository) {
        return new SignUpUseCase(signUpRepository);
    }
}
