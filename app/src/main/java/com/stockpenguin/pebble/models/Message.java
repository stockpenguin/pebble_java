package com.stockpenguin.pebble.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Message {
    private long timestamp;
    private String senderUid;
    private String text;

    public Message() {}

    public Message(long timestamp, String senderUid, String text) {
        this.timestamp = timestamp;
        this.senderUid = senderUid;
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
