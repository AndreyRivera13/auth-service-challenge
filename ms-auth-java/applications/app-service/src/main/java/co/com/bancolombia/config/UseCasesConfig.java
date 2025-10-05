package co.com.bancolombia.config;

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
    public SignInUseCase signInUseCase(SignInRepository signInRepository, UserRepository userRepository) {
        return new SignInUseCase(signInRepository,userRepository );
    }

    @Bean
    public SignUpUseCase signUpUseCase(SignUpRepository signUpRepository, UserRepository userRepository) {
        return new SignUpUseCase(signUpRepository,userRepository);
    }
}
