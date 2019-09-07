package com.lua.luanegra.objects;

import android.content.Context;

import java.util.ArrayList;

public class UserObject {
    private String name;
    private final String uid;
    private String imagemPerfilUri;
    private String notificationKey;
    private String lastOnline;
    private String isonline, patrono;
    private ArrayList<String> superAdminsList;
    private String primeiroNick, registoData, registoHora, bio, sociavel;
    private String salaPrivadaID, dataPedido, horaPedido, nomeSalaPrivada, pedidoID;
    private ArrayList<String> listaIdsAdmins, listaIdsBloquedUsers;
    private Context c;

    public Context getC() {
        return c;
    }

    public void setC(Context c) {
        this.c = c;
    }

    public String getSociavel() {
        return sociavel;
    }

    public void setSociavel(String sociavel) {
        this.sociavel = sociavel;
    }

    public String getBio() {
        return bio;
    }

    public String getPatrono() {
        return patrono;
    }

    public void setPatrono(String patrono) {
        this.patrono = patrono;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<String> getListaIdsAdmins() {
        return listaIdsAdmins;
    }

    public ArrayList<String> getListaIdsBloquedUsers() {
        return listaIdsBloquedUsers;
    }

    public void setListaIdsAdmins(ArrayList<String> listaIdsAdmins) {
        this.listaIdsAdmins = listaIdsAdmins;
    }

    public void setListaIdsBloquedUsers(ArrayList<String> listaIdsBloquedUsers) {
        this.listaIdsBloquedUsers = listaIdsBloquedUsers;
    }

    public String getPedidoID() {
        return pedidoID;
    }

    public void setPedidoID(String pedidoID) {
        this.pedidoID = pedidoID;
    }

    public String getNomeSalaPrivada() {
        return nomeSalaPrivada;
    }

    public void setNomeSalaPrivada(String nomeSalaPrivada) {
        this.nomeSalaPrivada = nomeSalaPrivada;
    }

    public String getSalaPrivadaID() {
        return salaPrivadaID;
    }

    public void setSalaPrivadaID(String salaPrivadaID) {
        this.salaPrivadaID = salaPrivadaID;
    }

    public UserObject(String name, String uid){
        this.name = name;
        this.uid = uid;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public String getHoraPedido() {
        return horaPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public void setHoraPedido(String horaPedido) {
        this.horaPedido = horaPedido;
    }

    public String getPrimeiroNick() {
        return primeiroNick;
    }

    public String getRegistoData() {
        return registoData;
    }

    public String getRegistoHora() {
        return registoHora;
    }

    public void setRegistoData(String registoData) {
        this.registoData = registoData;
    }

    public void setRegistoHora(String registoHora) {
        this.registoHora = registoHora;
    }

    public void setPrimeiroNick(String primeiroNick) {
        this.primeiroNick = primeiroNick;
    }

    public ArrayList getSuperAdminsList() {
        return superAdminsList;
    }

    public void setSuperAdminsList(ArrayList<String> superAdminsList) {
        this.superAdminsList = superAdminsList;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public String getImagemPerfilUri() {
        return imagemPerfilUri;
    }

    public void setImagemPerfilUri(String imagemPerfilUri) {
        this.imagemPerfilUri = imagemPerfilUri;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

}
