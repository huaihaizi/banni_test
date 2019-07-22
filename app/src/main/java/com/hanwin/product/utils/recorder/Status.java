package com.hanwin.product.utils.recorder;

/**
 * Created by admin on 2019/5/7.
 */

/**
 * 录音对象的状态
 */
public enum Status {
    //未开始
    STATUS_NO_READY,
    //预备
    STATUS_READY,
    //录音
    STATUS_START,
    //暂停
    STATUS_PAUSE,
    //停止
    STATUS_STOP
}