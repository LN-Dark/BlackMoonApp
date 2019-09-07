package com.lua.luanegra.objects;

import android.view.View;

public class GroupObject {
    private  String groupName;
    private  String logoUri;
    private  String uid;
    private String creator;
    private View view;
    private String descricaoSalaPrivada, textColor;
    private String corApresentacao, chatKey, whatKindOfRoom;
    private String numeroDeUtilizadores;

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getWhatKindOfRoom() {
        return whatKindOfRoom;
    }

    public void setWhatKindOfRoom(String whatKindOfRoom) {
        this.whatKindOfRoom = whatKindOfRoom;
    }

    public String getChatKey() {
        return chatKey;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getNumeroDeUtilizadores() {
        return numeroDeUtilizadores;
    }

    public void setNumeroDeUtilizadores(String numeroDeUtilizadores) {
        this.numeroDeUtilizadores = numeroDeUtilizadores;
    }

    public String getCorApresentacao() {
        return corApresentacao;
    }

    public void setCorApresentacao(String corApresentacao) {
        this.corApresentacao = corApresentacao;
    }

    public String getDescricaoSalaPrivada() {
        return descricaoSalaPrivada;
    }

    public void setDescricaoSalaPrivada(String descricaoSalaPrivada) {
        this.descricaoSalaPrivada = descricaoSalaPrivada;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    private String  imagemToShare, textToShare;

    public String getImagemToShare() {
        return imagemToShare;
    }

    public void setTextToShare(String textToShare) {
        this.textToShare = textToShare;
    }

    public String getTextToShare() {
        return textToShare;
    }

    public void setImagemToShare(String imagemToShare) {
        this.imagemToShare = imagemToShare;
    }

    public String getUid() {
        return uid;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public GroupObject(String uid, String groupName, String logoUri){
        this.groupName = groupName;
        this.uid = uid;
        this.logoUri = logoUri;
    }
}
