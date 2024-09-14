package com.paras.bsn.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCods {

    NO_CODE(0, NOT_IMPLEMENTED, "No code found"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Incorrect current password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "New password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Email and / or password is incorrect"),
    USER_ALREADY_EXISTS(305, CONFLICT, "User with this email already exists"),
    ;

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCods(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
