/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab;

import com.tim.dzenlab.obj.ApiToken;
import com.tim.dzenlab.obj.User;
import com.tim.dzenlab.ws.ErrorData;
import com.tim.dzenlab.ws.ErrorData.ErrorCode;
import com.tim.dzenlab.ws.LoginData;
import com.tim.dzenlab.ws.Message;
import com.tim.dzenlab.ws.Message.MessageType;
import java.io.IOException;
import static java.lang.System.err;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author Mikhail Titov
 */
public class WsHandler extends TextWebSocketHandler {

    private final DataManager dataManager;

    @Autowired
    public WsHandler(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        System.out.println("\n\nRECEIVED WS MESSAGE: "+textMessage.toString());
        try {
            Message message = Message.deserialize(textMessage.getPayload());        
            Message resp = processMessage(message, session);
            session.sendMessage(new TextMessage(Message.serializer(resp)));
        } catch (Throwable e) {
            e.printStackTrace(System.out);            
            sendError(session, e);
        }
    }
    
    private Message processMessage(Message request, WebSocketSession session) {
        if (!MessageType.LOGIN_CUSTOMER.equals(request.getType())) {
            //checking correctness of the api token
            if (!checkApiToken(request.getApiToken(), session))
                return new Message(MessageType.CUSTOMER_ERROR, request.getSequenceId(),
                        new ErrorData(ErrorCode.INVALID_API_TOKEN));
        }
        switch (request.getType()) {
            case LOGIN_CUSTOMER:
                return processLogin(request, session);
            case ECHO_REQUEST:
                return new Message(MessageType.ECHO_RESPONSE, request.getSequenceId(), request.getData());
            default:
                return new Message(MessageType.CUSTOMER_ERROR, request.getSequenceId(),
                        new ErrorData(ErrorCode.INVALID_MESSAGE_TYPE));
        }
    }
    
    private boolean checkApiToken(String apiToken, WebSocketSession session) {
        //looking up for token in the session user properties
        if (apiToken==null)
            return false;
        ApiToken token = (ApiToken) session.getAttributes().get(apiToken);
        boolean res = false;
        if (token!=null) {
            if (token.getExpirationDate().getTime() > System.currentTimeMillis())
                res = true;
            else
                session.getAttributes().remove(apiToken);
        } else {
            //checking token using database manager
            User user = dataManager.getUserByApiToken(apiToken);
            if (user!=null) {
                session.getAttributes().put(user.getToken(), new ApiToken(user.getToken(), user.getTokenExpirationDate()));
                res =  true;
            }
        }
        return res;
    }

    private void sendError(WebSocketSession session, Throwable e) throws IOException {
        Message resp = new Message(MessageType.CUSTOMER_ERROR, DataManager.generateUniqId(), 
                new ErrorData(ErrorCode.INVALID_MESSAGE_FORMAT,  err.getClass().getName()+": "+e.getMessage()));
        session.sendMessage(new TextMessage(Message.serializer(resp)));
    }

    @Transactional
    private Message processLogin(Message message, WebSocketSession session) {
        LoginData loginData = (LoginData)message.getData();
        System.out.println("LOGIN DATA: "+loginData);
        System.out.println("DATA MANAGER: "+dataManager);
//        return dataManager.withTx(()->{
            Message resp;
            User user = dataManager.getUser(loginData.getEmail(), loginData.getPwd());
            if (user == null)
                resp = new Message(MessageType.CUSTOMER_ERROR, message.getSequenceId(),
                        new ErrorData(ErrorCode.CUSTOMER_NOT_FOUND));
            else {
                ApiToken token = dataManager.generateToken();
                dataManager.auditTokenCreation(user, token);
                user.setToken(token.getToken());
                user.setTokenExpirationDate(token.getExpirationDate());
                dataManager.updateUser(user);
                session.getAttributes().put(token.getToken(), token);
                resp = new Message(MessageType.CUSTOMER_API_TOKEN, message.getSequenceId(), token);
            }
            return resp;
//        });
    }

}
