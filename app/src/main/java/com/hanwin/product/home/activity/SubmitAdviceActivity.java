package com.hanwin.product.home.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 提交意见反馈
 */
public class SubmitAdviceActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_right)
    TextView text_right;

    @Bind(R.id.edit_suggestion)
    EditText edit_suggestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_advice);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("意见反馈");
        text_right.setText("提交");
    }


    private void initData() {

    }

    @OnClick(R.id.text_right)
    public void submit(View view) {
        String suggestion = edit_suggestion.getText().toString();
        if(!TextUtils.isEmpty(suggestion)){
            Map<String, Object> params = new HashMap<>();
            params.put("userName", BaseApplication.getInstance().getUser().getUserName());
            params.put("suggestion",suggestion);
            getData(params);
        }else{
            ToastUtils.show(this,"请输入意见内容");
        }

    }


    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    private void getData(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_technology/userSuggestion", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(BaseApplication.getInstance(), "提交成功");
                        finish();
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), baseRespMsg.getMsg());
                    }
                }
            }
        });
    }
}
