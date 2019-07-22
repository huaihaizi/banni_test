
package com.hanwin.product.common.model;

import java.io.Serializable;


public class BaseRespMsg implements Serializable {
    public final static int STATUS_SUCCESS =1;
    public final static int TO_LOGIN = -99;
    public final static int ERROR = -1;
    public final static int HAS_EXIT = -4;
    public final static int NOT_EXIT=-5;
    public final static int WRONG_CODE=-6;
    public final static int TIMES_MORE=-7;
    public final static int POINTS_OVER=-8;
    public final static int WRONG_LEVEL=-9;
    public final static int NOT_ACTIVE=-10;

    protected int code;
    protected String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
