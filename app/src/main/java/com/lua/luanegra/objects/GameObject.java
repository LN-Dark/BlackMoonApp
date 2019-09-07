package com.lua.luanegra.objects;

public class GameObject {
    private String game_name;
    private String game_link_download;
    private String game_Image_Uri;

    public String getGame_Image_Uri() {
        return game_Image_Uri;
    }

    public String getGame_link_download() {
        return game_link_download;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_Image_Uri(String game_Image_Uri) {
        this.game_Image_Uri = game_Image_Uri;
    }

    public void setGame_link_download(String game_link_download) {
        this.game_link_download = game_link_download;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public GameObject(String game_name, String game_link_download, String game_Image_Uri){
        this.game_name = game_name;
        this.game_link_download = game_link_download;
        this.game_Image_Uri = game_Image_Uri;
    }
}
