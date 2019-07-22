package com.hanwin.product.viewutils.pulltorefresh.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class PullableScrollView extends ScrollView implements Pullable {
    //ScrollView向上滑动到顶部的距离
    private int upH;
    StopCall stopCall;
    View mView;

    public void initData(View view) {
        this.mView = view;
    }

    public PullableScrollView(Context context) {
        super(context);
    }

    public PullableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }

    public void setCallback(StopCall c){
        stopCall =c ;
    }

    /**
     * 关键部分在这里，测量当前ScrollView滑动的距离
     * 其中t就是，单位是px哦，不是dp
     * stopCall是一个接口，是为了在Activity中实现设置顶部1/2可不可见
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mView != null){
            if (t >=mView.getTop()) {
                //如果滑动距离>本例中图片高度
                stopCall.stopSlide(true);
                //设置顶部1可见，顶部2不可见
            } else {
                //否则
                stopCall.stopSlide(false);
                //设置顶部1不可见，顶部2可见
            }
        }
    }

    public interface StopCall {

        public void stopSlide(boolean isStop);
    }
}

