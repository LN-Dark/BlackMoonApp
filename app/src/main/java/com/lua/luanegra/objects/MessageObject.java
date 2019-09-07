package com.lua.luanegra.objects;

import java.util.ArrayList;

public class MessageObject {
    private String message;
    private String senderID, senderName;
    private String data;
    private String hora, userImageUri;
    private String chatID;
    private String chatKey;
    private ArrayList<UserObject> listaUsers;
    public final ArrayList<String> mediaUrlList;
    private String cor_chatReciever, cor_chatSender, corTexto, cor_dataHora;

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getCor_chatReciever() {
        return cor_chatReciever;
    }

    public String getCor_chatSender() {
        return cor_chatSender;
    }

    public void setCor_chatReciever(String cor_chatReciever) {
        this.cor_chatReciever = cor_chatReciever;
    }

    public String getCor_dataHora() {
        return cor_dataHora;
    }

    public void setCor_chatSender(String cor_chatSender) {
        this.cor_chatSender = cor_chatSender;
    }

    public String getCorTexto() {
        return corTexto;
    }

    public void setCor_dataHora(String cor_dataHora) {
        this.cor_dataHora = cor_dataHora;
    }

    public void setCorTexto(String corTexto) {
        this.corTexto = corTexto;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UserObject> getListaUsers() {
        return listaUsers;
    }

    public void setListaUsers(ArrayList<UserObject> listaUsers) {
        this.listaUsers = listaUsers;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getmessage() {
        return message;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public MessageObject(String senderID, String message, ArrayList<String> mediaUrlList, String data, String hora){
        this.senderID = senderID;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
        this.data = data;
        this.hora = hora;
    }
}
