package co.com.bancolombia.model.shared.common.exception.model;

public class CodeMessage {

    public static final String MALFORMED_REQUEST_CODE = "MALFORMED_REQUEST";
    public static final String USER_NOT_FOUND_CODE = "USER_NOT_FOUND";
    public static final String INVALID_CREDENTIALS_CODE = "INVALID_CREDENTIALS";
    public static final String REQUEST_INVALID_MESSAGE = "Request inválido";
    public static final String USER_NOT_EXISTS_MESSAGE = "Usuario no existe";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    public static final String INVALID_EMAIL_FORMAT_CODE = "INVALID_EMAIL_FORMAT";
    public static final String WEAK_PASSWORD_CODE = "WEAK_PASSWORD";
    public static final String EMAIL_ALREADY_EXISTS_CODE = "EMAIL_ALREADY_EXISTS";
    public static final String INVALID_EMAIL_FORMAT_MESSAGE = "Email inválido";
    public static final String WEAK_PASSWORD_MESSAGE = "Password débil";
    public static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Email ya registrado";


    private CodeMessage() {
        throw new IllegalStateException("CodeMessage class");
    }
}