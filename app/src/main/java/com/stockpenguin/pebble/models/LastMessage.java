package com.stockpenguin.pebble.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LastMessage {
    private String otherUserUid;
    private String message;
    private long timestamp;

    public LastMessage() {}

    public LastMessage(String otherUserUid, String message, long timestamp) {
        this.otherUserUid = otherUserUid;
        this.message = message;
        this.timestamp = timestamp;
    }
}
