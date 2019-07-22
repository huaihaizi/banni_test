package com.hanwin.product.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import com.hanwin.product.User;
import com.hanwin.product.common.http.OkHttpHelper;
import com.hanwin.product.home.activity.LoginActivity;
import com.hanwin.product.home.activity.ThirdLoginActivity;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.StatusBarUtil;
import com.hanwin.product.viewutils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;

/**
 */
public class BaseActivity extends AppCompatActivity {
    private View.OnClickListener onBackListener;
    protected static final String TAG = BaseActivity.class.getSimpleName();
    @Inject
    public OkHttpHelper mHttpHelper;
    public Context mContext;
    public DialogUtil dialogUtil;
    private static final boolean LOG_DEBUG = true;
    public static BaseActivity instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        instance = this;
        BaseApplication.component().inject(this);
        //设置activity 的状态栏颜色和字体颜色
        StatusBarUtil.setStatusBar(this, true, false);
        StatusBarUtil.setStatusTextColor(true, this);

        ActivityManager.getInstance().addActivity(this);
        dialogUtil = new DialogUtil();

    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().remove(this);
    }

    public void startActivity(Intent intent, boolean isNeedLogin) {


        if (isNeedLogin) {

            User user = BaseApplication.getInstance().getUser();
            if (user != null && !user.getUserId().equals(0) && !"".equals(BaseApplication.getInstance().getToken())) {
                super.startActivity(intent);
            } else {
//                BaseApplication.getInstance().putIntent(intent);
//                Intent loginIntent = new Intent(this
//                        , LoginActivity.class);
//                super.startActivity(loginIntent);
            }
//
        } else {
            super.startActivity(intent);
        }

    }


    public void startActivityForResult(Intent intent, boolean isNeedLogin, int requestCode) {


        if (isNeedLogin) {
            User user = BaseApplication.getInstance().getUser();
            if (user != null && !user.getUserId().equals(0) && !"".equals(BaseApplication.getInstance().getToken())) {
                super.startActivityForResult(intent, requestCode);
            } else {
                BaseApplication.getInstance().putIntent(intent);
//                Intent loginIntent = new Intent(mContext, LoginActivity.class);
//                super.startActivityForResult(loginIntent,requestCode);
            }

        } else {
            super.startActivityForResult(intent, requestCode);
        }

    }


    protected void setupToolbar(Toolbar mToolbar, boolean homeIconVisible) {
//        if (mToolbar == null) {
//            throw new RuntimeException("toolbar cannot be null!");
//        }
//        if (null == getSupportActionBar()) {
//            setSupportActionBar(mToolbar);
//        }
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(homeIconVisible);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        mToolbar.setNavigationIcon(R.drawable.fanhui);
    }

    protected void setupToolbar(Toolbar mToolbar, boolean homeIconVisible, View.OnClickListener onBackListener) {
        setupToolbar(mToolbar, homeIconVisible);
        this.onBackListener = onBackListener;
    }

    /**
     * 重写Actionbar返回上一级的动画,以及避免重复创建实例
     *
     * @param item 菜单项
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (onBackListener != null) {
                onBackListener.onClick(null);
            } else {
                finish();
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void showToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Toast toast = Toast.makeText(BaseApplication.getInstance(), content, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 全局dialog
     * 被踢下线后，重新登录
     */
    public static void againLogin() {
        final DialogUtil dialogUtil = new DialogUtil();
        BaseApplication.getInstance().clearUser();
        dialogUtil.showDialog(ActivityManager.getInstance().getCurrentActivity(), "提示", "您的账号在另一台设备上登录，请重新登录");
        dialogUtil.setOnClick(new DialogUtil.OnClick() {
            @Override
            public void leftClick() {
                Intent intent = new Intent(BaseApplication.getInstance(), ThirdLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                BaseApplication.getInstance().startActivity(intent);
                ActivityManager.getInstance().keepActivtiy();
                dialogUtil.dialog.dismiss();
            }

            @Override
            public void rightClick() {

            }
        });
        dialogUtil.dialog.show();
    }
}
