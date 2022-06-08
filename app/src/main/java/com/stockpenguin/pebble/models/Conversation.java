package com.stockpenguin.pebble.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class Conversation {

    public enum ConversationType {
        DUO, GROUP
    }

    private String conversationId;
    private ConversationType conversationType;

    private ArrayList<Message> messages;

    /* group chat */
    private String conversationName;
    private String conversationOwner;
    private String user1Username;
    private String user2Username;
    private String user1PhotoUrl;
    private String user2PhotoUrl;

    public Conversation() {
        messages = new ArrayList<>();
    }

    public Conversation(String conversationId, ConversationType conversationType,
                        String user1, String user2,
                        String user1PhotoUrl, String user2PhotoUrl) {
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.user1Username = user1;
        this.user2Username = user2;
        this.user1PhotoUrl = user1PhotoUrl;
        this.user2PhotoUrl = user2PhotoUrl;

        messages = new ArrayList<>();
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationOwner() {
        return conversationOwner;
    }

    public void setConversationOwner(String conversationOwner) {
        this.conversationOwner = conversationOwner;
    }

    public String getUser1Username() {
        return user1Username;
    }

    public void setUser1Username(String user1Username) {
        this.user1Username = user1Username;
    }

    public String getUser2Username() {
        return user2Username;
    }

    public void setUser2Username(String user2Username) {
        this.user2Username = user2Username;
    }

    public String getUser1PhotoUrl() {
        return user1PhotoUrl;
    }

    public void setUser1PhotoUrl(String user1PhotoUrl) {
        this.user1PhotoUrl = user1PhotoUrl;
    }

    public String getUser2PhotoUrl() {
        return user2PhotoUrl;
    }

    public void setUser2PhotoUrl(String user2PhotoUrl) {
        this.user2PhotoUrl = user2PhotoUrl;
    }
}
