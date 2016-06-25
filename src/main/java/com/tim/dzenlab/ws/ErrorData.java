/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author tim1
 */
public class ErrorData {
    public enum ErrorCode {
        CUSTOMER_NOT_FOUND("customer.notFound", "Customer not found"),
        INVALID_API_TOKEN("customer.invalidApiToken", "Invalid or expired api token"),
        INVALID_MESSAGE_TYPE("customer.invalidMessageType", "Not supported message type"),
        INVALID_MESSAGE_FORMAT("customer.invalidMessageFormat", "Invalid message format");


        private final String message;
        private final String description;

        ErrorCode(String message, String description) {
            this.message = message;
            this.description = description;
        }

        public String getCode() {
            return message;
        }

        public String getDescription() {
            return description;
        }
    }
    @JsonProperty("error_code")
    private final ErrorCode errorCode;
    @JsonProperty("error_description")
    private final String errorDescription;

    public ErrorData(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorDescription = errorCode.description;
    }

    public ErrorData(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    
}
