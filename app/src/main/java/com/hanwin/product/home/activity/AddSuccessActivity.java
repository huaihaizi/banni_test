package com.hanwin.product.home.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddSuccessActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_success);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("提交成功");
    }


    private void initData() {

    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }
}
