package com.lua.luanegra.objects;

public class VideoObject {
    private final String videoText;
    private final String uid;
    private String data;
    private String hora, creatorUID;
    private final String videoLink;
    private final String videoCreator;
    private final String videoImageCreator;

    public VideoObject(String videoLink, String uid, String data, String hora, String videoText, String videoCreator, String videoImageCreator){
        this.videoText = videoText;
        this.uid = uid;
        this.data = data;
        this.hora = hora;
        this.videoLink = videoLink;
        this.videoCreator = videoCreator;
        this.videoImageCreator = videoImageCreator;
    }

    public String getCreatorUID() {
        return creatorUID;
    }

    public void setCreatorUID(String creatorUID) {
        this.creatorUID = creatorUID;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getUid() {
        return uid;
    }

    public String getVideoCreator() {
        return videoCreator;
    }

    public String getVideoImageCreator() {
        return videoImageCreator;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public String getVideoText() {
        return videoText;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

}
