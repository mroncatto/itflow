package io.github.mroncatto.itflow.application.config.constant;

public class SecurityConstant {
    private SecurityConstant() {}
    public static final String BASE_URL = "/api/v1";
    public static final String AUTHENTICATION_URL = BASE_URL + "/auth/login";
    public static final String[] PUBLIC_URL = {"/", "/swagger-ui/**", "/v3/api-docs/**", "/actuator"};
    public static final String[] ALLOW_POST = { BASE_URL + "/user/resetpassword" };
    public static final String[] ALLOW_CORS = { "*"};

}
