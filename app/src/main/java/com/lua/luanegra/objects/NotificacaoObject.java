package com.lua.luanegra.objects;

public class NotificacaoObject {
    private String titulo, mensagem, data, hora, id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public NotificacaoObject(String titulo, String data, String hora, String mensagem, String id){
        this.titulo = titulo;
        this.data = data;
        this.hora = hora;
        this.mensagem = mensagem;
        this.id = id;
    }

    public NotificacaoObject(){
        this.titulo = " ";
        this.data = " ";
        this.hora = " ";
        this.mensagem = " ";
        this.id = " ";
    }

}
