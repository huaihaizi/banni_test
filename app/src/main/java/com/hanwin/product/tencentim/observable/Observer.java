package com.hanwin.product.tencentim.observable;

import java.io.Serializable;

/**
 * 通知观察器
 * @param <T>
 */
public interface Observer<T> extends Serializable {

    /**
     * 通知产生后的回调函数
     * @param t 事件参数
     */
    public void onEvent(T t);
}
