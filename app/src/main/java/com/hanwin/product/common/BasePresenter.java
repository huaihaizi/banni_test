package com.hanwin.product.common;

import android.content.Context;


import com.hanwin.product.common.http.OkHttpHelper;

import javax.inject.Inject;


/**
 * Created by Stefan on 2017/3/15.
 */

public class BasePresenter {

    @Inject
    protected OkHttpHelper mHttpHelper;

    protected Context mContext;
    public BasePresenter(Context mContext) {
        BaseApplication.component().inject(this);
        this.mContext=mContext;

    }
}
