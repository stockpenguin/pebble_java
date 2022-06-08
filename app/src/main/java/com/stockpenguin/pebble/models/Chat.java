package com.stockpenguin.pebble.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chat {
    private String photoUrl;
    private String sender;
    private String message;

    public Chat() {}

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Chat(String photoUrl, String sender, String message) {
        this.photoUrl = photoUrl;
        this.sender = sender;
        this.message = message;


    }
}
