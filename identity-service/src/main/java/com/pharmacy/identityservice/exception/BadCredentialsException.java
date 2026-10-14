package com.pharmacy.identityservice.exception;

/**
 * Exception ném khi login fail (username không tồn tại HOẶC password sai).
 * <p>
 * Message LUÔN là "Bad credentials" — không tiết lộ chi tiết.
 */
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {
        super("Bad credentials");
    }
}