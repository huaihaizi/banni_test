package com.hanwin.product;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.activity.CounselorHomeActivity;
import com.hanwin.product.home.activity.NormalUserListActivity;
import com.hanwin.product.utils.AppUtils;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class MainActivity extends BaseActivity {
    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        AppUtils.setJiPushTags(context);
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        if (!"".equals(BaseApplication.getInstance().getToken())) {
            User user = BaseApplication.getInstance().getUser();
            if (user != null) {
                statisticalStartupTimes();//统计已登录用户的启动次数
                if ("signLanguageConsultant".equals(user.getRole())) {
                    CounselorHomeActivity.start(this, null);
                } else {
                    NormalUserListActivity.start(this, null);
                }
            }
        } else {
            NormalUserListActivity.start(this, null);
        }
        finish();
    }

    //统计已登录用户的启动次数
    private void statisticalStartupTimes() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", BaseApplication.getInstance().getUser().getUserName());
        params.put("type", "1");
        params.put("appVersion", Utils.getVersionName(this));
        params.put("deviceTypeName", Utils.getPhoneModel());
        params.put("deviceSystemVersion", Utils.getBuildVersion());
        params.put("platform", "android");
        mHttpHelper.post(Contants.BASE_URL + "sign_technology/dataStatistics", params, new SpotsCallBack<BaseRespMsg>(this, "msg") {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {

                    } else {
                    }
                }
            }
        });
    }
}
