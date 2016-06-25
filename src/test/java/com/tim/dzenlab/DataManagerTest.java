/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab;

import com.tim.dzenlab.obj.ApiToken;
import com.tim.dzenlab.obj.TokenAudit;
import com.tim.dzenlab.obj.User;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 *
 * @author Mikhail Titov
 */
@ContextConfiguration("/test-config.xml")
public class DataManagerTest extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    private DataManager dataManager;
    
    @Test
    public void saveUserTest() {
        User user = new User("user", "pass");
        dataManager.saveUser(user);
        assertNotNull(user.getId());
        
        final List<User> users = dataManager.listUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
    }
    
    @Test
    public void updateUserTest() {
        User user = new User("user", "pass");
        dataManager.saveUser(user);
        
        user.setEmail("user1");
        dataManager.updateUser(user);
        User updatedUser = dataManager.getUser("user1", "pass");
        assertNotNull(updatedUser);
        assertNull(dataManager.getUser("user", "pass"));        
    }
    
    @Test
    public void getUserByTokenApi() throws InterruptedException {
        User user = new User("user", "pass");
        user.setToken("123");
        user.setTokenExpirationDate(new Date(System.currentTimeMillis()+1000));
        dataManager.saveUser(user);
        
        User user2 = dataManager.getUserByApiToken("123");
        assertNotNull(user2);
        Thread.sleep(1100);
        assertNull(dataManager.getUserByApiToken("123"));
    }
    
    @Test
    public void auditTokenCreationTest() {
        User user = new User("user", "pass");
        dataManager.saveUser(user);
        assertNotNull(user.getId());
        
        Date expDate = new Date();
        ApiToken token = new ApiToken("123", expDate);
        dataManager.auditTokenCreation(user, token);
        List<TokenAudit.View> tokenAudits = dataManager.listTokenAuditView();
        assertNotNull(tokenAudits);
        assertEquals(1, tokenAudits.size());
        TokenAudit ta = tokenAudits.get(0);
        assertNotNull(ta.getId());
        assertEquals(user.getId(), ta.getUserId());
        assertEquals(expDate, ta.getExpirationDate());
        assertEquals(token.getToken(), ta.getToken());
    }
}
