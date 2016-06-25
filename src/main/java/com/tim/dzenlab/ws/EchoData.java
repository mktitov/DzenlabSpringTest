/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Mikhail Titov
 */
public class EchoData {
    private final String message;

    @JsonCreator
    public EchoData(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }    
}
