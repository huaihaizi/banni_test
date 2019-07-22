package com.hanwin.product.tencentim.event;

/**
 * Created by admin on 2018/11/22.
 */

public class MessageReceiverBean {
    private String sound_uri;
    private String ext;
    private String msgid;

    public String getSound_uri() {
        return sound_uri;
    }

    public void setSound_uri(String sound_uri) {
        this.sound_uri = sound_uri;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
