/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab.obj;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 *
 * @author Mikhail Titov
 */
public class TokenAudit {
    private Long id;
    private String token;
    private Date expirationDate;
    private Long userId;

    public TokenAudit() {
    }

    public TokenAudit(Long id, String token, Date expirationDate, Long userId) {
        this.id = id;
        this.token = token;
        this.expirationDate = expirationDate;
        this.userId = userId;
    }

    public TokenAudit(ApiToken token, Long userId) {
        this.token = token.getToken();
        this.expirationDate = token.getExpirationDate();
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public static class View extends TokenAudit {
        private String userEmail;

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
    }
}
