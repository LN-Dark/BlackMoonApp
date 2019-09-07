package com.lua.luanegra.objects;

public class ChatObject {
    private final String chatID;
    private String userName;
    private String imagemPerfilUri;
    private String partenrUid;
    private String lastmessage;
    private String lastmessageData;
    private String lastMessageHora;
    private String imageToShare;
    private String textToShare;
    private String partnerNotificationKey;
    private String partnerUserImageUri;
    private String currentUserImageUri, currentUserName;

    public String getCurrentUserImageUri() {
        return currentUserImageUri;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getPartnerUserImageUri() {
        return partnerUserImageUri;
    }

    public void setPartnerUserImageUri(String partnerUserImageUri) {
        this.partnerUserImageUri = partnerUserImageUri;
    }

    public String getPartnerNotificationKey() {
        return partnerNotificationKey;
    }

    public void setCurrentUserImageUri(String currentUserImageUri) {
        this.currentUserImageUri = currentUserImageUri;
    }

    public void setPartnerNotificationKey(String partnerNotificationKey) {
        this.partnerNotificationKey = partnerNotificationKey;
    }

    public String getTextToShare() {
        return textToShare;
    }

    public void setTextToShare(String textToShare) {
        this.textToShare = textToShare;
    }

    public void setImagemPerfilUri(String imagemPerfilUri) {
        this.imagemPerfilUri = imagemPerfilUri;
    }

    public String getImageToShare() {
        return imageToShare;
    }

    public void setImageToShare(String imageToShare) {
        this.imageToShare = imageToShare;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public String getLastmessageData() {
        return lastmessageData;
    }

    public String getLastMessageHora() {
        return lastMessageHora;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public void setLastmessageData(String lastmessageData) {
        this.lastmessageData = lastmessageData;
    }

    public void setLastMessageHora(String lastMessageHora) {
        this.lastMessageHora = lastMessageHora;
    }

    public String getPartenrUid() {
        return partenrUid;
    }

    public void setPartenrUid(String partenrUid) {
        this.partenrUid = partenrUid;
    }

    public String getImagemPerfilUri() {
        return imagemPerfilUri;
    }

    public ChatObject(String chatID){
        this.chatID = chatID;

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getChatID() {
        return chatID;
    }

}
