package com.lua.luanegra.objects;

public class NewsObject {

    private final String noticia;
    private final String data;
    private final String hora;
    private final String icon;
    private final String newsLink;
    private String newsName;
    public NewsObject(String noticia, String data, String hora, String icon, String newsLink){
        this.noticia = noticia;
        this.data = data;
        this.hora = hora;
        this.icon = icon;
        this.newsLink = newsLink;
    }

    public String getNewsName() {
        return newsName;
    }

    public void setNewsName(String newsName) {
        this.newsName = newsName;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public String getIcon() {
        return icon;
    }

    public String getHora() {
        return hora;
    }

    public String getData() {
        return data;
    }

    public String getnoticia() {
        return noticia;
    }

}