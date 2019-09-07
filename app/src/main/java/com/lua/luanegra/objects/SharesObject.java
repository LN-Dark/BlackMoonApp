package com.lua.luanegra.objects;

public class SharesObject {

    private final String sharesText;
    private final String uid;
    private final String data;
    private final String hora;
    private final String shareLink;
    private final String shareCreator;
    private String SHareUiDCreator;
    private final String shareImageCreator;

    public SharesObject(String shareLink, String uid, String data, String hora, String sharesText, String shareCreator, String shareImageCreator){
        this.sharesText = sharesText;
        this.uid = uid;
        this.data = data;
        this.hora = hora;
        this.shareLink = shareLink;
        this.shareCreator = shareCreator;
        this.shareImageCreator = shareImageCreator;
    }

    public String getSHareUiDCreator() {
        return SHareUiDCreator;
    }

    public void setSHareUiDCreator(String SHareUiDCreator) {
        this.SHareUiDCreator = SHareUiDCreator;
    }

    public String getUid() {
        return uid;
    }

    public String getShareImageCreator() {
        return shareImageCreator;
    }

    public String getShareCreator() {
        return shareCreator;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getShareLink() {
        return shareLink;
    }

    public String getSharesText() {
        return sharesText;
    }

}
