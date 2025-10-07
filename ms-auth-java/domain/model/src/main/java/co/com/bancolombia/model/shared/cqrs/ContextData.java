package co.com.bancolombia.model.shared.cqrs;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ContextData {
    private String messageId;
    private String consumerCode;
}
