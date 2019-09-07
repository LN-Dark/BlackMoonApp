package com.lua.luanegra.objects;

public class LogObject {
    private final String logText;
    private final String logCreator;
    private final String uid;

    public String getLogCreator() {
        return logCreator;
    }

    public String getUid() {
        return uid;
    }

    public String getLogText() {
        return logText;
    }

    public LogObject(String logText, String logCreator, String uid){
        this.logText = logText;
        this.logCreator = logCreator;
        this.uid = uid;
    }
}
