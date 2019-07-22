package com.hanwin.product.tencentim.bean;

/**
 * Created by zhaopf on 2019/4/12.
 */

public class TranslateWordBean {
    private String name;//名字
    private String content;//翻译字幕
    private String startTime;//一句话开始时间
    private String endTime;//一句话结束时间
    private String videoStartTime;//视频开始时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVideoStartTime() {
        return videoStartTime;
    }

    public void setVideoStartTime(String videoStartTime) {
        this.videoStartTime = videoStartTime;
    }
}
