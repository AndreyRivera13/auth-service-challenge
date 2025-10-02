package co.com.bancolombia.model.exception.model;

public class CodeMessage {
    public static final String INVALID_HEADER_DETAIL = "Faltan cabeceras obligatorias";
    public static final String MISSING_PARAMS_BODY = "Faltan parámetros obligatorios.";
    public static final String SUID_CONSUMER_DETAIL = "El consumidor no se encuentra registrado";
    public static final String INVALID_DATA_DETAIL = "Uno o más datos no poseen un valor válido.";
    public static final String EMPTY_SEARCH_IDENTITY_DETAIL = "Registro no encontrado";
    public static final String MAXIM_DELEGATES_NUMBER_DETAIL = "Supera el número máximo permitido de delegados";
    public static final String SCHEME_INVALID_DETAIL = "Control no válido";
    public static final String SCHEME_ALREADY_ASSIGN_DETAIL = "Cliente ya tiene un esquema asociado";
    public static final String EXPIRATION_DAY_MAXIMUM_DETAIL = "Cantidad de días no válido";
    public static final String EXTERNAL_FAIL_DETAIL = "Error en la llamanda a un servicio externo";
    public static final String UNKNOWN_ERROR_PERMISSION_DETAIL =
            "Ha ocurrido un error interno en el servicio transversal de permisos";
    public static final String UNKNOWN_ERROR_ROLLBACK_SERVICE = "Ha ocurrido un error interno en el servicio";
    public static final String UNKNOWN_ERROR_PARAM_DETAIL = "Ha ocurrido un error interno en el servicio";
    public static final String UNKNOWN_ERROR_DETAIL = "Ha ocurrido un error interno en el servicio";
    public static final String CLIENT_NOT_EXIST_DETAIL = "Consulta no retorna resultado";
    public static final String NOT_PRIVILEGES_DETAIL = "No cuenta con los permisos para ejecutar la acción";
    public static final String USER_WITH_RELATIONSHIP_DETAIL = "Usuario con relación existente";
    public static final String RELATIONSHIP_NOT_FOUND_DETAIL = "No se encuentran registros de relacionamiento";
    public static final String REGISTER_EXIST_DETAIL = "No es posible crear la relación";
    public static final String USER_NOT_EXIST_DETAIL = "No existe registro para la consulta";
    public static final String USER_NOT_FOUND_DETAIL = "El AID no corresponde a la identidad ingresada";
    public static final String STATUS_ALIAS_DISABLE_DETAIL = "El estado del alias no se encuentra activo";
    public static final String ROL_NOT_EXIST_DETAIL = "El role no existe";
    public static final String UPDATE_IN_PROCESS_DETAIL = "Existe una actualizacion en proceso";
    public static final String DEFAULT_MESSAGE = "Ha ocurrido un error interno en el servicio.";
    public static final String STATUS_DIFFERENT_PENDING_DETAIL = "El estado de relación es diferente a PENDIENTE";
    public static final String EXCEEDED_NUMBER_OF_RETRIES_DETAIL = "Superaste el número de intentos permitidos";
    public static final String UNAUTHORIZED_CONSUMER_DETAIL = "Consumidor no autorizado";
    public static final String USER_WITHOUT_DELEGATES_DETAIL = "Usuario no cuenta con delegaciones";
    public static final String PAGE_WITHOUT_RECORDS_DETAIL = "Pagina no cuenta con registros";
    public static final String UNPROCESSED_MESSAGE_DETAIL = "No fue posible procesar la transacción";
    public static final String NO_ACTIVE_ROL_CODE_DETAIL = "Role no se encuentra en estado ACTIVO";
    public static final String SCHEMA_FULL_DETAIL = "Cantidad titulares supera control elegido";
    public static final String SELF_MANAGEMENT_UPDATE_ROL_DETAIL = "No se permite la autogestión de cambio de role";
    public static final String PENDING_RELATIONSHIP_DETAIL = "Relación en estado PENDIENTE";
    public static final String DELEGATE_WITH_ROLE_DETAIL = "El delegado ya cuenta con el rol a asignar";
    public static final String TITULAR_ROLE_CODE_DETAIL = "El Role pertenece a un TITULAR";
    public static final String ROLE_INVALID_DETAIL = "Dominio de valores asociado al rol es inválido";
    public static final String OTP_CODE_NOT_VALID_DETAIL = "Código de afiliación no válido";
    public static final String CLIENT_WITHOUT_SCHEMA_DETAIL = "El cliente no tiene un control asociado";
    public static final String TITULAR_NOT_FOUND_DETAIL = "No se encontraron titulares";
    public static final String STATUS_NOT_MODIFY_RELATIONSHIP_DETAIL =
            "El estado actual de la relación no se puede modificar";
    public static final String REGISTER_WITHOUT_REP_DETAIL = "No se ha registrado el representante legal";
    public static final String TRANSACTION_IN_PROCESS_DETAIL = "Ya existe una transacción en proceso";
    public static final String TRANSACTION_AVAILABILITY_DETAIL =
            "Ha ocurrido un error interno en el servicio de transacciones";
    public static final String OTP_CODE_VALIDATED_DETAIL = "Código de afiliación ya se validó";
    public static final String OTP_CODE_EXPIRED_DETAIL = "Código de afiliación expirado";
    public static final String UNAUTHORIZED = "Cliente no autorizado";

    private CodeMessage() {
        throw new IllegalStateException("CodeMessage class");
    }
}