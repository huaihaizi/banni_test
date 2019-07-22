package com.hanwin.product.home.bean;

/**
 * TODO:
 *
 * @acthor weiang
 * 2019/7/8 5:18 PM
 */
public class VoiceListBean {

    String text;

    int type = 0;

    boolean isPlay = false;

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
