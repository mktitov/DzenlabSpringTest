/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab.obj;

import java.util.Date;

/**
 *
 * @author Mikhail Titov
 */
public class ApiToken {
    private final String token;
    private final Date expirationDate;

    public ApiToken(String token, Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }    

    public String getToken() {
        return token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }    
}
