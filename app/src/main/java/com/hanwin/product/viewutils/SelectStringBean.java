package com.hanwin.product.viewutils;

/**
 * Created by zhaopf on 2018/6/22 0022.
 */

public class SelectStringBean {
    private int id;
    private String content;
    private Boolean isSelect;

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
