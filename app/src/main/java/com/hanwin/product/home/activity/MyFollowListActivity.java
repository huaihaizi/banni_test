package com.hanwin.product.home.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.viewutils.pulltorefresh.PullToRefreshLayout;
import com.hanwin.product.viewutils.pulltorefresh.pullableview.PullableScrollView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 我的关注界面
 */
public class MyFollowListActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_right)
    TextView text_right;

    @Bind(R.id.recycler_counselor)
    RecyclerView recycler_counselor;
    @Bind(R.id.order_scrollview)
    PullableScrollView scrollview;
    @Bind(R.id.refresh_view)
    PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("我的关注");
    }

    /**
     * 初始化数据
     */
    private void initData() {
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

}
