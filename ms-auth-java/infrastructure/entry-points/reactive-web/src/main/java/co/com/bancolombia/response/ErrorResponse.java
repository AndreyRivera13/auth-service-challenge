package co.com.bancolombia.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private Object details;
    private String messageId;
    private String consumerCode;
    private int status;
    private String path;
    private long timestamp;
}
