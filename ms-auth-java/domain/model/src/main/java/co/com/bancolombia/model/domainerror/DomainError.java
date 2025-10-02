package co.com.bancolombia.model.domainerror;
import co.com.bancolombia.model.contextdata.ContextData;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class DomainError extends RuntimeException {
    private String code;
    private String message;
    private Object details;
    private ContextData context;

    @Override
    public String getMessage() {
        return message;
    }
}
