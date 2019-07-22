package com.hanwin.product.viewutils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自定义GridView
 * Created by zhaopf on 2019/1/7.
 */

public class GridViewInScrollView extends GridView {
    public GridViewInScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GridViewInScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
