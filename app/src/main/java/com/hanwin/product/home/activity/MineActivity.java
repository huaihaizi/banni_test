package com.hanwin.product.home.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.DialogUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 我的界面
 */
public class MineActivity extends BaseActivity implements ILoginView {
    @Bind(R.id.image_head)
    CircleImageView image_head;
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_sex)
    TextView text_sex;
    @Bind(R.id.text_position)
    TextView text_position;
    @Bind(R.id.text_line)
    TextView text_line;

    @Bind(R.id.rel_consult)
    RelativeLayout rel_consult;
    @Bind(R.id.rel_add)
    RelativeLayout rel_add;//加入手语师
    @Bind(R.id.rel_advice)
    RelativeLayout rel_advice;//意见反馈

    @Bind(R.id.rel_signlanguage_skills)
    RelativeLayout rel_signlanguage_skills;//技能信息
    @Bind(R.id.rel_service_time)
    RelativeLayout rel_service_time;//服务时间
    @Bind(R.id.rel_certification)
    RelativeLayout rel_certification;
    @Bind(R.id.check_consult)
    CheckBox check_consult;

    @Bind(R.id.view_add)
    View view_add;
    @Bind(R.id.view_advice)
    View view_advice;
    @Bind(R.id.view_certification)
    View view_certification;
    @Bind(R.id.view_signlanguage_skills)
    View view_signlanguage_skills;
    @Bind(R.id.view_service_time)
    View view_service_time;

    private LoginHelper loginHelper;
    private User muser;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("userName", BaseApplication.getInstance().getUser().getUserName());
                    params.put("optionType", "offline");
                    params.put("generalUserName", "");
                    params.put("orderNo", "");
                    params.put("updateOrCreateView", "update");
                    sendMsg1(params);
                }
                BaseApplication.getInstance().clearUser();
                loginHelper.logout();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        loginHelper = new LoginHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        User user = BaseApplication.getInstance().getUser();
        if (user != null) {
            String url = user.getAvatar();
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.image_head_man);
            if(!TextUtils.isEmpty(url) && url.contains("http")){
                Glide.with(this).load(url)
                        .apply(options)
                        .into(image_head);
            }else{
                Glide.with(this).load(Contants.BASE_IMAGE + url)
                        .apply(options)
                        .into(image_head);
            }
            text_name.setText(user.getNickName());
            if ("1".equals(user.getGender())) {
                text_sex.setText("男");
            } else if ("0".equals(user.getGender())) {
                text_sex.setText("女");
            }
            //手语咨询师才显示该按钮
            if ("signLanguageConsultant".equals(user.getRole())) {
                rel_consult.setVisibility(View.VISIBLE);
                rel_signlanguage_skills.setVisibility(View.VISIBLE);
                rel_service_time.setVisibility(View.VISIBLE);
                rel_add.setVisibility(View.GONE);
                rel_advice.setVisibility(View.GONE);
                rel_certification.setVisibility(View.GONE);
                view_add.setVisibility(View.GONE);
                view_advice.setVisibility(View.GONE);
                view_certification.setVisibility(View.GONE);
                //判断咨询师是否离线在线
                if ("online".equals(user.getOnlineStatus())) {
                    check_consult.setChecked(true);
                } else {
                    check_consult.setChecked(false);
                }
            } else {
                rel_signlanguage_skills.setVisibility(View.GONE);
                rel_service_time.setVisibility(View.GONE);
                rel_consult.setVisibility(View.GONE);
                rel_add.setVisibility(View.VISIBLE);
                rel_advice.setVisibility(View.VISIBLE);
                view_signlanguage_skills.setVisibility(View.GONE);
                view_service_time.setVisibility(View.GONE);
                if ("1".equals(user.getRealAthenNameSign())) {
                    rel_certification.setVisibility(View.GONE);
                    view_certification.setVisibility(View.GONE);
                } else {
                    rel_certification.setVisibility(View.VISIBLE);
                    view_certification.setVisibility(View.VISIBLE);
                }
            }

            Map<String, Object> params = new HashMap<>();
            params.put("userName", BaseApplication.getInstance().getUser().getUserName());
            getData(params);
        }
    }

    /**
     * 个人资料
     */
    @OnClick(R.id.rel_person_info)
    public void rel_person_info() {
        Intent intent = new Intent(this, PersonInfoActivity.class);
        if(muser != null){
            intent.putExtra("nickName",muser.getNickName());
            intent.putExtra("disabIdenNum",muser.getDisabIdenNum());
            intent.putExtra("picurl",muser.getPicUrl());
        }
        startActivity(intent);
    }

    /**
     * 加入手语师
     */
    @OnClick(R.id.rel_add)
    public void rel_add() {
        if (muser != null) {
            if (!TextUtils.isEmpty(muser.getChannelType()) && "app".equals(muser.getChannelType())) {
                Intent intent = new Intent(this, AddSuccessActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("title", "加入手语师");
                intent.putExtra("url", Contants.JOIN_SIGN_LANGUAGE + "?url=" +Contants.BASE_URL +"&userName="+BaseApplication.getInstance().getUser().getUserName()+"&platform=android"  );
                intent.putExtra("type", "0");
                startActivity(intent);
            }
        }
    }

    /**
     * 意见反馈
     */
    @OnClick(R.id.rel_advice)
    public void rel_advice() {
        Intent intent = new Intent(this, SubmitAdviceActivity.class);
        startActivity(intent);
    }

    /**
     * 实名认证
     */
    @OnClick(R.id.rel_certification)
    public void rel_my_care() {
        Intent intent = new Intent(this, CertificationActivity.class);
        intent.putExtra("username", BaseApplication.getInstance().getUser().getUserName());
        intent.putExtra("isregister", false);
        startActivity(intent);
    }

    /**
     * 手语技能信息
     */
    @OnClick(R.id.rel_signlanguage_skills)
    public void rel_skills() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "手语技能信息");
        intent.putExtra("url", Contants.SKILL_INFO);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    /**
     * 服务时间
     */
    @OnClick(R.id.rel_service_time)
    public void servicetime() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "服务时间段");
        intent.putExtra("url", Contants.SERVICE_TIME);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    private void getData(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/querySignStatus", params, new SpotsCallBack<LoginMsgBean>(this) {
            @Override
            public void onSuccess(Response response, LoginMsgBean loginMsgBean) {
                if (loginMsgBean != null) {
                    if (loginMsgBean.getCode() >= 0) {
                        muser = loginMsgBean.getData();
                        if (muser != null) {
                            text_name.setText(muser.getNickName());
                            if ("online".equals(muser.getOnlineStatus())) {
                                check_consult.setChecked(true);
                            } else {
                                check_consult.setChecked(false);
                            }

                        }
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), loginMsgBean.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 允许咨询
     */
    @OnClick(R.id.check_consult)
    public void check_consult() {
        if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
            Map<String, Object> params = new HashMap<>();
            params.put("userName", BaseApplication.getInstance().getUser().getUserName());
            params.put("generalUserName", "");
            params.put("orderNo", "");
            params.put("updateOrCreateView", "update");
            if (!check_consult.isChecked()) {
                params.put("optionType", "offline");
                sendMsg(params, "offline");
            } else if (check_consult.isChecked()) {
                params.put("optionType", "online");
                sendMsg(params, "online");
            }
        }
    }

    private void sendMsg(Map<String, Object> params, final String type) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/updateStatus", params, new SpotsCallBack<BaseRespMsg>(this, "msg") {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        if ("offline".equals(type)) {
                            User user = BaseApplication.getInstance().getUser();
                            user.setOnlineStatus("offline");
                            BaseApplication.getInstance().putUser(user, BaseApplication.getInstance().getToken());
                            ToastUtils.show(MineActivity.this, "设置离线成功");
                        } else if ("online".equals(type)) {
                            User user = BaseApplication.getInstance().getUser();
                            user.setOnlineStatus("online");
                            BaseApplication.getInstance().putUser(user, BaseApplication.getInstance().getToken());
                            ToastUtils.show(MineActivity.this, "设置在线成功");
                        } else if ("quit".equals(type)) {

                        }
                    } else {
                        ToastUtils.show(MineActivity.this, baseRespMsg.getMsg());
                    }
                }
            }
        });
    }


    private void sendMsg1(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/updateStatus", params, new SpotsCallBack<BaseRespMsg>(this, "msg") {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {

                }
            }
        });
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    @OnClick(R.id.text_quit)
    public void my(View view) {
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            dialogUtil.infoDialog(this, "退出登录", "您确定退出登录吗？", true, true);
            dialogUtil.setOnClick(new DialogUtil.OnClick() {
                @Override
                public void leftClick() {
                    handler.sendEmptyMessage(100);//退出登录，并将咨询师的状态改为离线状态
                    dialogUtil.dialog.dismiss();
//                    Intent intent = new Intent(MineActivity.this, ThirdLoginActivity.class);
//                    startActivity(intent);
                    finish();
                }

                @Override
                public void rightClick() {
                    dialogUtil.dialog.dismiss();
                }
            });
            dialogUtil.dialog.show();
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.e("logout === ", "退出成功");
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        Log.e("logout === ", "退出失败");
    }
}
