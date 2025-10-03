package co.com.bancolombia.filter;

import co.com.bancolombia.model.exception.AppException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Service
public class HeadersValidation {
    public static final String HEADERS_GENERAL_ERROR = "La petición solicitada no posee las cabeceras correctas";
    private static final String REGEX_UUID =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final Pattern uuidPattern = Pattern.compile(REGEX_UUID);

    public Mono<Void> validateHeaders(ServerRequest request) {
        String consumer = request.headers().firstHeader("x-request-id");
        String messageId = request.headers().firstHeader("message-id");
        if (!validateUUID(messageId) || consumer == null || consumer.trim().isEmpty()) {
            return Mono.error(new AppException("MISSING_HEADER", HEADERS_GENERAL_ERROR));
        }
        return Mono.empty();
    }

    private boolean validateUUID(String uuid) {
        return uuid != null && uuidPattern.matcher(uuid).matches();
    }
}
