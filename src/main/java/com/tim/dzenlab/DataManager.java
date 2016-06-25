/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab;

import com.tim.dzenlab.obj.ApiToken;
import com.tim.dzenlab.obj.TokenAudit;
import com.tim.dzenlab.obj.User;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mikhail Titov
 */
@Repository
@Transactional
public class DataManager  {
    public final static long TOKEN_EXPIRATION_TIMEOUT = 300_000; //in ms
    
    private final JdbcTemplate tpl;
    private final SimpleJdbcInsert userInsert;
    private final SimpleJdbcInsert auditInsert;

    @Autowired
    public DataManager(DataSource dataSource) {
        tpl = new JdbcTemplate(dataSource);
        this.userInsert = new SimpleJdbcInsert(tpl).withTableName("jdbc_users").usingGeneratedKeyColumns("id");
        this.auditInsert = new SimpleJdbcInsert(tpl).withTableName("jdbc_token_audit").usingGeneratedKeyColumns("id");
    }
    
    public List<User> listUsers() {
        return tpl.query("select * from jdbc_users", new BeanPropertyRowMapper<User>(User.class));
    }
    
    public List<TokenAudit.View> listTokenAuditView() {
        return tpl.query(
                "select u.email user_email, t.* "
                    + "from jdbc_token_audit t "
                    + "inner join jdbc_users u on u.id = t.user_id",
                new BeanPropertyRowMapper<TokenAudit.View>(TokenAudit.View.class));
    }
    
    public void saveUser(User user) {
        Number key = userInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(user));
        user.setId(key.longValue());
    }
    
    public void updateUser(User user) {
        tpl.update(
                "update jdbc_users "
                    + "set email = ?, pwd = ?, token = ?, token_expiration_date = ?"
                    + "where id = ?",
                user.getEmail(), user.getPwd(), user.getToken(), user.getTokenExpirationDate(), user.getId()
        );
    }

    public void createTestUsers() {
        createUserIfNeed(new User("user1@email.ru", "user1"));
        createUserIfNeed(new User("user2@gmail.com", "user2"));
    }
    
    public static String generateUniqId() {
        return UUID.randomUUID().toString();
    }
    
    private void createUserIfNeed(User user) {
        int cnt = tpl.queryForObject("select count(*) from jdbc_users where email=?", Integer.class, user.getEmail());
        if (cnt==0)
            saveUser(user);
    }

    public User getUserByApiToken(String apiToken) {
        try {
            return tpl.queryForObject(
                    "select * from jdbc_users where token=? and token_expiration_date>?", 
                    new Object[]{apiToken, new Timestamp(System.currentTimeMillis())}, 
                    new BeanPropertyRowMapper<User>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUser(String email, String pwd) {
        try {
            return tpl.queryForObject(
                    "select * from jdbc_users where email=? and pwd=?", 
                    new Object[]{email, pwd}, 
                    new BeanPropertyRowMapper<User>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ApiToken generateToken() {
        return new ApiToken(generateUniqId(), new Date(System.currentTimeMillis()+TOKEN_EXPIRATION_TIMEOUT));
    }

    public void auditTokenCreation(User user, ApiToken token) {
        TokenAudit audit = new TokenAudit(token, user.getId());
        Number key = auditInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(audit));
        audit.setId(key.longValue());
    }
}
