package com.hanwin.product.home.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.EvaluateHeartLin;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 用户评论界面
 * Created by zhaopf on 2018/10/16 0004.
 */

public class UserEvaluateActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_time)
    TextView text_time;
    @Bind(R.id.text_anonymous)
    TextView text_anonymous;
    @Bind(R.id.text_leve)
    TextView text_leve;

    @Bind(R.id.edit_evaluate)
    EditText edit_evaluate;
    @Bind(R.id.image_head)
    ImageView image_head;
    @Bind(R.id.heartlin1)
    EvaluateHeartLin heartlin1;

    private int commentEvaluate = 5;
    private String orderNo;
    private String isHiddenName = "0";
    private CounselorBean counselorBean;
    private String consultationTime;
    private String consultantUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_user);
        ButterKnife.bind(this);
        text_title.setText("我的评价");
        initData();

    }

    private void initData() {
        String name = getIntent().getStringExtra("name");
        String avatar = getIntent().getStringExtra("avatar");
        consultantUserName = getIntent().getStringExtra("consultationTime");
        consultationTime = getIntent().getStringExtra("consultationTime");
        orderNo = getIntent().getStringExtra("orderNo");
        text_leve.setText("超赞");
        heartlin1.setOnClick(new EvaluateHeartLin.OnClick() {
            @Override
            public void onItemClice(int heart) {
                commentEvaluate = heart;
                if (heart == 1) {
                    text_leve.setText("差");
//                    edit_evaluate.setText("感觉欠佳  需要再加油");
                } else if (heart == 2) {
                    text_leve.setText("一般");
//                    edit_evaluate.setText("感觉一般  有待改善");
                } else if (heart == 3) {
                    text_leve.setText("还行");
//                    edit_evaluate.setText("还可以接受");
                } else if (heart == 4) {
                    text_leve.setText("满意");
//                    edit_evaluate.setText("很满意 下次会咨询");
                } else if (heart == 5) {
                    text_leve.setText("超赞");
//                    edit_evaluate.setText("感受百分百满意  绝对会关注");
                }
            }
        });
        int time = Integer.parseInt(consultationTime);
        if (time >= 60) {
            if (time % 60 == 0) {
                text_time.setText("视频时长：" + time / 60 + "分钟");
            } else {
                text_time.setText("视频时长：" + (time / 60 + 1) + "分钟");
            }
        } else {
            text_time.setText("视频时长：" + consultationTime + "秒");
        }
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_head_man);
        Glide.with(this).load(Contants.BASE_IMAGE + avatar)
                .apply(options)
                .into(image_head);
        text_name.setText(name);

    }

    @OnClick(R.id.imgbtn_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.text_submit)
    public void submit() {
        String evaluate = edit_evaluate.getText().toString();
        if (commentEvaluate == 0) {
            ToastUtils.show(this, "请选择评论星级");
        } else {
            if (TextUtils.isEmpty(evaluate)) {
                evaluate = "该用户暂没有评价";
            }
            Map<String, Object> params = new HashMap<>();
            params.put("userName", BaseApplication.getInstance().getUser().getUserName());
            params.put("signUserName", consultantUserName);
            params.put("commentEvaluate", commentEvaluate);
            params.put("content", evaluate);
            params.put("serviceMethod", 0);
            params.put("orderNo", orderNo);
            params.put("isHiddenName", isHiddenName);
            params.put("lengthTime", consultationTime);
            submit(params);
        }
    }

    /**
     * 提交
     *
     * @param params
     */
    private void submit(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/userAddComment", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(UserEvaluateActivity.this, "评价成功");
                        finish();
                    } else {
                        ToastUtils.show(UserEvaluateActivity.this, baseRespMsg.getMsg());
                    }
                }
            }
        });
    }
}
