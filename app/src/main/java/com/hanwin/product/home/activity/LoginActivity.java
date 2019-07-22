package com.hanwin.product.home.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.home.adapter.ViewPagerAdapter;
import com.hanwin.product.home.fragment.LoginFragment;
import com.hanwin.product.home.fragment.RegisterFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shihao.library.XRadioGroup;

public class LoginActivity extends BaseActivity implements ViewPager.OnPageChangeListener, XRadioGroup.OnCheckedChangeListener {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.view_line)
    View view_line;

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.radio_register)
    RadioButton radio_register;
    @Bind(R.id.radio_login)
    RadioButton radio_login;
    @Bind(R.id.radiogroup_message)
    XRadioGroup radiogroup_message;

    private ArrayList<Object> items = new ArrayList<Object>();
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initView() {
        int currentItem = getIntent().getIntExtra("item", 0);
        view_line.setVisibility(View.GONE);
        if (currentItem == 0) {
//            text_title.setText("登录");
        } else {
//            text_title.setText("注册");
            radio_register.setChecked(true);
            radio_login.setTextSize(19);
            radio_register.setTextSize(24);
            radio_login.getPaint().setFakeBoldText(false);
            radio_register.getPaint().setFakeBoldText(true);
        }
        items.add(new LoginFragment());
        items.add(new RegisterFragment());

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), items);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem, false);
        viewPager.setOffscreenPageLimit(items.size());
        viewPager.setOnPageChangeListener(this);
        radiogroup_message.setOnCheckedChangeListener(this);
    }


    private void initData() {

    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        BaseApplication.getInstance().clearUser();
//        MainActivity.start(this,null);
        finish();
    }

    public void changeCurrentItem(int currentItem) {
        viewPager.setCurrentItem(currentItem, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                radio_login.setChecked(true);
                radio_login.setTextSize(24);
                radio_login.getPaint().setFakeBoldText(true);
                radio_register.setTextSize(19);
                radio_register.getPaint().setFakeBoldText(false);
                break;
            case 1:
                radio_register.setChecked(true);
                radio_login.setTextSize(19);
                radio_register.setTextSize(24);
                radio_login.getPaint().setFakeBoldText(false);
                radio_register.getPaint().setFakeBoldText(true);
                break;
            default:
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(XRadioGroup xRadioGroup, int i) {
        switch (i) {
            case R.id.radio_register:
                viewPager.setCurrentItem(1);
                break;
            case R.id.radio_login:
                viewPager.setCurrentItem(0);
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseApplication.getInstance().clearUser();
//        MainActivity.start(this,null);
        finish();
    }
}
