package com.telemarket.telemarketer.http;

/**
 * Http状态码
 */
public enum Status {
    SUCCESS_200(200, "OK"),
    NOT_MODIFIED_304(304, "NOT MODIFIED"),
    NOT_FOUND_404(404, "NOT FOUND"),
    BAD_REQUEST_400(400, "BAD REQUEST"),
    METHOD_NOT_ALLOWED_405(405, "METHOD NOT ALLOWED"),
    INTERNAL_SERVER_ERROR_500(500, "INTERNAL SERVER ERROR");
    private int code;
    private String message;

    Status(int code, String msg) {
        this.code = code;
        message = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}