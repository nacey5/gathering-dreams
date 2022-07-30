package com.hzh.gatheringproject.message;

import com.hzh.gatheringproject.util.UserHolder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message {
    private boolean success;
    private String reason;

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

}
