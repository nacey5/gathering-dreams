package com.hzh.gatheringproject.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String userID;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String userID) {
        this.userID = userID;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
