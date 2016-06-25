/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab;

import com.tim.dzenlab.obj.TokenAudit;
import com.tim.dzenlab.obj.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mikhail Titov
 */
@RestController
@RequestMapping("/ui")
public class MainController {
    private final DataManager dataManager;

    @Autowired
    public MainController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    
    @GetMapping("/get-users")
    public List<User> getUsers() {
        return dataManager.listUsers();
    }
    
    @GetMapping("/create-test-users")
    public void createTestUsers() {
        dataManager.createTestUsers();
    }
    
    @GetMapping("/get-token-history")
    public List<TokenAudit.View> getTokenAudit() {
        return dataManager.listTokenAuditView();
    }
}
