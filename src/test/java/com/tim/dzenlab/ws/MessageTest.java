/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tim.dzenlab.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mikhail Titov
 */
public class MessageTest extends Assert {
    
    @Test
    public void test() throws JsonProcessingException {
        Message mess = new Message(Message.MessageType.ECHO_REQUEST, "id123", "token321", new EchoData("PING"));
        System.out.println("json: \n"+Message.serializer(mess));
    }
    
    @Test
    public void deserializeTest() throws Exception {
        String json = "{\"type\":\"ECHO_REQUEST\",\"data\":{\"message\":\"PING\"},\"sequence_id\":\"id123\",\"api_token\":\"token321\"}";
        Message mess = Message.deserialize(json);
        assertTrue(mess.getData() instanceof EchoData);
    }
}
