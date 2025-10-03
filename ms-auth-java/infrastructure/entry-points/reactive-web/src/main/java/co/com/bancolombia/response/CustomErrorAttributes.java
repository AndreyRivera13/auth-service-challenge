package co.com.bancolombia.response;

import co.com.bancolombia.model.contextdata.ContextData;
import co.com.bancolombia.model.exception.AppException;
import co.com.bancolombia.model.exception.DomainError;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "INTERNAL_ERROR";
        String message = "Ha ocurrido un error interno";
        Object details = null;
        ContextData context = null;

        if (error instanceof DomainError de) {
            code = de.getCode();
            message = de.getMessage();
            details = de.getDetails();
            context = de.getContext();
            status = mapDomainCode(code);
        } else if (error instanceof AppException ae) {
            code = ae.getCode();
            message = ae.getMessage();
            status = mapDomainCode(code);
        }

        ErrorResponse body = ErrorResponse.builder()
                .code(code)
                .message(message)
                .details(details)
                .messageId(context != null ? context.getMessageId() : null)
                .consumerCode(context != null ? context.getConsumerCode() : null)
                .status(status.value())
                .path(request.path())
                .timestamp(System.currentTimeMillis())
                .build();

        Map<String, Object> map = new HashMap<>();
        map.put("status", status.value());
        map.put("body", body);
        return map;
    }

    private HttpStatus mapDomainCode(String code) {
        return switch (code) {
            case "MALFORMED_REQUEST", "INVALID_EMAIL_FORMAT", "WEAK_PASSWORD", "MISSING_HEADER" -> HttpStatus.BAD_REQUEST;
            case "INVALID_CREDENTIALS" -> HttpStatus.UNAUTHORIZED;
            case "USER_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "EMAIL_ALREADY_EXISTS" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
