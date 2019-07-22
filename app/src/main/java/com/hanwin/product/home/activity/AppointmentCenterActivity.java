package com.hanwin.product.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 预约中心
 */
public class AppointmentCenterActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_right)
    TextView text_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_center);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("预约中心");
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
