package ru.stanley.messenger.Utils;

import org.json.JSONObject;
import ru.stanley.messenger.Models.Message;

public enum MessageType {

    AUTH(
            "{\"type\":\"AUTH\",\"data\":{\"username\":\"\",\"password\":\"\"}}"
    ),
    REGISTER(
            "{\"type\":\"REGISTER\",\"data\":{\"username\":\"\",\"passwordHash\":\"\",\"email\":\"\",\"phone\":\"\"}}"
    ),
    REGISTER_SUCCESS(
        "{\"type\":\"REGISTER_SUCCESS\"}"
    ),
    REGISTER_FAIL(
        "{\"type\":\"REGISTER_FAIL\"}"
    ),
    CHAT_MESSAGE(
            "{\"type\":\"CHAT_MESSAGE\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
    );

    private final String jsonTemplate;

    MessageType(String jsonTemplate) {
        this.jsonTemplate = jsonTemplate;
    }

    public String getJsonTemplate() {
        return jsonTemplate;
    }

    public JSONObject createJsonObject() {
        return new JSONObject(jsonTemplate);
    }

    public Message createMessage(JSONObject data) {
        return new Message(this.name(), data);
    }

}
