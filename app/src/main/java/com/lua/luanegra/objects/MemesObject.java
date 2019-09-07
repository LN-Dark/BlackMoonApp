package com.lua.luanegra.objects;

public class MemesObject {
    private final String uri;
    private String chatID, chatKey;
    private String whatActivity;

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public MemesObject(String uri){
        this.uri = uri;
    }

    public String getWhatActivity() {
        return whatActivity;
    }

    public void setWhatActivity(String whatActivity) {
        this.whatActivity = whatActivity;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public String getUri() {
        return uri;
    }

}
