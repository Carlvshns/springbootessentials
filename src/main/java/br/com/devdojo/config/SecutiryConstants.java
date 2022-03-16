package br.com.devdojo.config;

public class SecutiryConstants {
    // Authorization Bearer 
    static final String SECRET = "DevDojoFoda";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    static final String SIGN_UP_URL = "/user/sign-up";
    static final long EXPIRATION_TIME = 86400000L;
}