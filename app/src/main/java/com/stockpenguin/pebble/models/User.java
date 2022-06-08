package com.stockpenguin.pebble.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String displayName;
    public String photoUrl;
    public long timestamp;

    public User() {}

    public User(String uid,
                String email,
                String displayName,
                String photoUrl,
                long timestamp) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
    }
}
