package com.oda.interfaces.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(Metadata meta, T data) {

    public record Metadata(Result result, String errorCode, String message) {
        public enum Result {
            SUCCESS, FAIL
        }
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(new Metadata(Metadata.Result.SUCCESS, null, null), data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(new Metadata(Metadata.Result.SUCCESS, null, null), null);
    }

    public static ApiResponse<Object> fail(String errorCode, String errorMessage) {
        return new ApiResponse<>(new Metadata(Metadata.Result.FAIL, errorCode, errorMessage), null);
    }
}
