package com.hanwin.product.utils;

import android.content.Context;

/**
 * Created by admin on 2018/11/19.
 */

public class UIUtill {
    public static int dp2px(Context context, int dpValue) {
        return (int) context.getResources().getDisplayMetrics().density * dpValue;
    }
}
