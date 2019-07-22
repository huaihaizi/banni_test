package com.hanwin.product.home.bean;

import java.util.List;

public class OftenPageInfo {
    private int pageNum;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private List<CounselorBean> data;

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

    public List<CounselorBean> getData() {
        return data;
    }

    public void setData(List<CounselorBean> data) {
        this.data = data;
    }
}
