package com.hanwin.product.home.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class CounselorEvaluateActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_gender)
    TextView text_gender;
    @Bind(R.id.text_video_time)
    TextView text_video_time;

    @Bind(R.id.image_head)
    ImageView image_head;

    @Bind(R.id.checkbox_connection)
    CheckBox checkbox_connection;
    @Bind(R.id.checkbox_video_carton)
    CheckBox checkbox_video_carton;
    @Bind(R.id.checkbox_other)
    CheckBox checkbox_other;

    @Bind(R.id.edit_content)
    EditText edit_content;
    @Bind(R.id.submit_evaluate)
    TextView submit_evaluate;

    private User user;
    private String orderNo;
    private String signComment = "0";
    private String consultationTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_counselor);
        // 4.4及以上版本开启
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//        }
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setNavigationBarTintEnabled(true);
//        // 自定义颜色
//        tintManager.setTintColor(Color.parseColor("#FFC626"));
        ButterKnife.bind(this);
        initView();
        initData();

    }

    private void initView() {
        text_title.setText("服务结束");
        orderNo = getIntent().getStringExtra("orderNo");
        consultationTime = getIntent().getStringExtra("consultationTime");
        user = BaseApplication.getInstance().getUser();

        text_name.setText(user.getName());
        RequestOptions options  = new RequestOptions().placeholder(R.drawable.image_head_man);
        if ("1".equals(user.getGender())) {
            text_gender.setText("性别：男");
            options = new RequestOptions().placeholder(R.drawable.image_head_man);
        } else if ("0".equals(user.getGender())) {
            text_gender.setText("性别：女");
            options = new RequestOptions().placeholder(R.drawable.image_head_woman);
        }
        Glide.with(this).load(Contants.BASE_IMAGE + user.getAvatar())
                .apply(options)
                .into(image_head);
        int time = Integer.parseInt(consultationTime);
        if(time >= 60){
            if(time % 60 == 0){
                text_video_time.setText("视频时长："+ time/60 + "分钟");
            }else {
                text_video_time.setText("视频时长："+ (time/60 + 1) + "分钟");
            }
        }else{
            text_video_time.setText("视频时长："+ consultationTime + "秒");
        }
    }


    private void initData() {
        checkbox_connection.setOnCheckedChangeListener(this);
        checkbox_video_carton.setOnCheckedChangeListener(this);
        checkbox_other.setOnCheckedChangeListener(this);
    }

    @OnClick(R.id.submit_evaluate)
    public void submitEvaluate(View view) {
        if(TextUtils.isEmpty(signComment)){
            ToastUtils.show(this,"请选择一项评价内容");
        }else {
            Map<String, Object> params = new HashMap<>();
            params.put("signUserName", user.getUserName());
            params.put("content", edit_content.getText().toString().trim());
            params.put("orderNo", orderNo);
            params.put("signComment", signComment);
            submit(params);
        }
    }

    /**
     * 提交
     * @param params
     */
    private void submit(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/signLanguageAddComment", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(CounselorEvaluateActivity.this, "评价成功");
                        finish();
                    } else {
                        ToastUtils.show(CounselorEvaluateActivity.this, baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkbox_connection:
                if(isChecked){
                    checkbox_video_carton.setChecked(false);
                    checkbox_other.setChecked(false);
                    signComment = "0";
                }else{
                    signComment = "";
                }
                break;
            case R.id.checkbox_video_carton:
                if(isChecked){
                    checkbox_connection.setChecked(false);
                    checkbox_other.setChecked(false);
                    signComment = "1";
                }else{
                    signComment = "";
                }
                break;
            case R.id.checkbox_other:
                if(isChecked){
                    checkbox_connection.setChecked(false);
                    checkbox_video_carton.setChecked(false);
                    signComment = "2";
                }else{
                    signComment = "";
                }
                break;
        }
    }
}
