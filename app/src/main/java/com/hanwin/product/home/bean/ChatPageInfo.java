package com.hanwin.product.home.bean;

import java.util.List;

/**
 * Created by zhaopf on 2018/10/24 0024.
 */

public class ChatPageInfo {
    private int pageNum;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private List<ChatRecordBean> data;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<ChatRecordBean> getData() {
        return data;
    }

    public void setData(List<ChatRecordBean> data) {
        this.data = data;
    }
}
