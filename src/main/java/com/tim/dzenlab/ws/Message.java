package com.tim.dzenlab.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tim.dzenlab.obj.ApiToken;

/**
 *
 * @author Mikhail Titov
 */
public class Message {
    public enum MessageType {
        LOGIN_CUSTOMER(LoginData.class),
        CUSTOMER_API_TOKEN(ApiToken.class),
        CUSTOMER_ERROR(ErrorData.class),
        ECHO_REQUEST(EchoData.class),
        ECHO_RESPONSE(EchoData.class);

        private final Class dataClass;

        MessageType(Class dataClass) {
            this.dataClass = dataClass;
        }

        public Class getDataClass() {
            return dataClass;
        }
        
    };
    private MessageType type;
    @JsonProperty("sequence_id")
    private String sequenceId;
    @JsonProperty("api_token")
    private String apiToken;
    private Object data;

    public Message() {
    }

    public Message(MessageType type, String sequenceId, Object data) {
        this.type = type;
        this.sequenceId = sequenceId;
        this.data = data;
    }

    public Message(MessageType type, String sequenceId, String apiToken, Object data) {
        this.type = type;
        this.sequenceId = sequenceId;
        this.apiToken = apiToken;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Object getData() {
        return data;
    }

//    @JsonIgnore
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Message: type=%s, sequenceId=%s, apiToken=%s, data=%s", type, sequenceId, apiToken, data); 
    }
    
    public static Message deserialize(String json) throws Exception {
        ObjectMapper objMapper = new ObjectMapper();
        JsonNode node = objMapper.readTree(json);
        Message mess = objMapper.treeToValue(node, Message.class);
        mess.setData(objMapper.treeToValue(node.findValue("data"), mess.getType().getDataClass()));
        return mess;
    }
    
    public static String serializer(Message message) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(message);
    }
}
