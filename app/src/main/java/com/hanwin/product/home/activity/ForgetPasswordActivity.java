package com.hanwin.product.home.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.fragment.RegisterFragment;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.RegexUtil;
import com.hanwin.product.utils.ToastUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 忘记密码界面
 */
public class ForgetPasswordActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.edit_phone_number)
    EditText edit_phone_number;
    @Bind(R.id.edit_code)
    EditText edit_code;
    @Bind(R.id.edit_password)
    EditText edit_password;
    @Bind(R.id.edit_again_password)
    EditText edit_again_password;
    @Bind(R.id.text_code)
    TextView text_code;

    @Bind(R.id.view_line)
    View view_line;
    private static final int TASK_TIMER_MESSAGE = 0;
    private static final int TASK_DENIED_MESSAGE = 1;
    private static final int TASK_TIMER_RESET_MESSAGE = 2;
    private Timer mTaskTimer;
    private boolean isTimerStop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initView() {
        text_title.setText("");
        text_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        text_code.getPaint().setAntiAlias(true);//抗锯齿
        view_line.setVisibility(View.GONE);
    }


    private void initData() {

    }

    @OnClick(R.id.text_code)
    public void text_code() {
        String accoount = edit_phone_number.getText().toString().trim();
        Long time = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(this, "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(this, "请输入正确的手机号码");
        } else {
            params.put("userName", accoount);
            params.put("optionType", "getBackPassword");
            getCode(params);
            if (edit_code.isFocused()) {
                //已获得焦点
            } else {
                edit_code.requestFocus();//获得焦点
            }
        }
    }

    private void getCode(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/sendIdentifyingCode", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(ForgetPasswordActivity.this, "验证码发送成功");
                        mTaskTimer = new Timer();
                        mTaskTimer.scheduleAtFixedRate(new ConfirmButtonTimerTask(), 0, 1000);
                    } else {
                        ToastUtils.show(ForgetPasswordActivity.this, baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    private final Handler timerHandler = new Handler(new Handler.Callback() {
        private int counter = 61;

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TASK_TIMER_MESSAGE) {
                counter--;
                if (counter == 0) {
                    mTaskTimer.cancel();
                    counter = 61;
                    isTimerStop = true;
                    text_code.setEnabled(true);
                    text_code.setText(getString(R.string.send_code));
                } else {
                    text_code.setEnabled(false);
                    text_code.setText(getString(R.string.btn_hqyzm, counter));
                }
            } else if (msg.what == TASK_DENIED_MESSAGE) {
            } else if (msg.what == TASK_TIMER_RESET_MESSAGE) {
            }
            return true;
        }
    });

    private class ConfirmButtonTimerTask extends TimerTask {
        public ConfirmButtonTimerTask() {
            timerHandler.sendEmptyMessage(TASK_TIMER_RESET_MESSAGE);
        }

        @Override
        public void run() {
            timerHandler.sendEmptyMessage(TASK_TIMER_MESSAGE);
        }
    }



    @OnClick(R.id.btn_resetting_password)
    public void btn_resetting_password() {
        String accoount = edit_phone_number.getText().toString().trim();//手机号
        String password = edit_password.getText().toString().trim();//密码
        String againpassword = edit_again_password.getText().toString().trim();//确认密码
        String code = edit_code.getText().toString().trim();//验证码
        Map<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(this, "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(this, "请输入正确的手机号码");
        } else if (TextUtils.isEmpty(code)) {
            ToastUtils.show(this, "请输入验证码");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.show(this, "请输入新密码");
        } else if (TextUtils.isEmpty(againpassword)) {
            ToastUtils.show(this, "请输入确认密码");
        } else if (!password.equals(againpassword)) {
            ToastUtils.show(this, "请输入一致密码");
        } else {
            params.put("userName", accoount);
            params.put("password", password);
            params.put("identifyingCode", code);
            resetpass(params);
        }
    }
    private void resetpass(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/resetPassword", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(ForgetPasswordActivity.this,"密码重置成功");
                        finish();
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }
}
