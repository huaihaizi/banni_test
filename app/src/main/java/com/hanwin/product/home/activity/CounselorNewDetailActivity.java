package com.hanwin.product.home.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.home.adapter.GridViewAdapter;
import com.hanwin.product.home.bean.CounselorDetailBean;
import com.hanwin.product.home.bean.CounselorDetailMsgBean;
import com.hanwin.product.home.bean.OrderBean;
import com.hanwin.product.home.bean.OrderMsgBean;
import com.hanwin.product.home.bean.SignDialectBean;
import com.hanwin.product.home.bean.TransFieldBean;
import com.hanwin.product.tencentim.activity.AVChatActivity;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.DialogUtil;
import com.hanwin.product.viewutils.GridViewInScrollView;
import com.hanwin.product.viewutils.StarBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by admin on 2019/1/7.
 */

public class CounselorNewDetailActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.image_head)
    CircleImageView image_head;
    @Bind(R.id.starBar)
    StarBarView starBarView;
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_position_year)
    TextView text_position_year;
    @Bind(R.id.text_person_info)
    TextView text_person_info;
    @Bind(R.id.grid_view)
    GridViewInScrollView grid_view;

    @Bind(R.id.text_sign_language)
    TextView text_sign_language;
    @Bind(R.id.text_call)
    TextView text_call;

    private String userName;
    private CounselorDetailBean counselorDetailBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_newdetail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("个人信息");
        userName = getIntent().getStringExtra("userName");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("generalUserName",BaseApplication.getInstance().getUser().getUserName());
        params.put("userName", userName);
        getDataInfo(params);
    }

    private void getDataInfo(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_technology/findSignDetail", params, new SpotsCallBack<CounselorDetailMsgBean>(this, "list") {
            @Override
            public void onSuccess(Response response, CounselorDetailMsgBean counselorDetailMsgBean) {
                if (counselorDetailMsgBean != null) {
                    counselorDetailBean = counselorDetailMsgBean.getData();
                    if (counselorDetailBean != null) {
//                        text_title.setText(counselorDetailBean.getName());
                        text_name.setText(counselorDetailBean.getName());
                        if(!TextUtils.isEmpty(counselorDetailBean.getMultiAverage())){
                            starBarView.setStarMark(Float.parseFloat(counselorDetailBean.getMultiAverage()));
                        }
                        String str = counselorDetailBean.getAddress()  + "   <font color='#dfdfdf'>|</font>   "+ counselorDetailBean.getWorkingYears() + "年翻译经验";
                        text_position_year.setText(Html.fromHtml(str));
                        image_head.setBorderColor(getResources().getColor(R.color.color_f0eff5));
                        image_head.setBorderWidth(2);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.image_head_man);
                        Glide.with(CounselorNewDetailActivity.this).load(Contants.BASE_IMAGE + counselorDetailBean.getAvatar())
                                .apply(options)
                                .into(image_head);
                        TransFieldBean transFieldBean = counselorDetailBean.getTransField();
                        if (transFieldBean != null) {
                            String transDirectStr = transFieldBean.getTransDirect();
                            if (!TextUtils.isEmpty(transDirectStr)) {
                                String[] transDirectList = transDirectStr.split(",");
                                GridViewAdapter gridViewAdapter = new GridViewAdapter(CounselorNewDetailActivity.this, transDirectList);
                                grid_view.setAdapter(gridViewAdapter);
                            }
                        }
                        if(!TextUtils.isEmpty(counselorDetailBean.getSuccessCase())){
                            text_person_info.setText(counselorDetailBean.getSuccessCase());
                        }else{
                            text_person_info.setText("暂无经历");
                        }

                        SignDialectBean signDialectBean = counselorDetailBean.getSignDialect();
                        StringBuffer sb = new StringBuffer();
//                        sb.append("手语方言：");
                        if (signDialectBean != null) {
                            String dialectStr = signDialectBean.getDialect();
                            String dialectDesc = signDialectBean.getDialectDesc();
                            if (!TextUtils.isEmpty(dialectStr)) {
                                String[] dialectList = dialectStr.split(",");
                                if(dialectList != null && dialectList.length == 2){
                                    if ("1".equals(dialectList[1])) {
                                        if (!TextUtils.isEmpty(dialectDesc)) {
                                            String des = dialectDesc.replace(",", "  |  ");
                                            sb.append(des + "");
                                        }
                                    }
                                    if ("0".equals(dialectList[0])) {
//                                        sb.append("通用手语");
                                    }
                                }else if(dialectList != null && dialectList.length == 1){
                                    if ("0".equals(dialectList[0])) {
//                                        sb.append("通用手语");
                                    } else if ("1".equals(dialectList[0])) {
                                        if (!TextUtils.isEmpty(dialectDesc)) {
                                            String des = dialectDesc.replace(",", "  |  ");
                                            sb.append(des);
                                        }
                                    }
                                }
                            }
                            text_sign_language.setText(sb);
                        }

                        if ("busy".equals(counselorDetailBean.getOnlineStatus())){
                            text_call.setBackground(getResources().getDrawable(R.drawable.button_busy_bg));
                            text_call.setText("忙线中");
                            text_call.setTextColor(getResources().getColor(R.color.navigation_bar_bg));
                            text_call.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规
                        }else if ("offline".equals(counselorDetailBean.getOnlineStatus())){
                            text_call.setBackground(getResources().getDrawable(R.drawable.button_gray_bg));
                            text_call.setText("已离线");
                            text_call.setTextColor(getResources().getColor(R.color.gray));
                            text_call.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规

                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    @OnClick(R.id.text_call)
    public void call(View view) {
        if (TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(this, ThirdLoginActivity.class);
            startActivity(intent);
        } else {
            if (counselorDetailBean != null) {
//                if ("1".equals(BaseApplication.getInstance().getUser().getRealAthenNameSign())) {
                    //判断咨询师在线，并不被屏蔽的情况下才能呼叫
                    if ("online".equals(counselorDetailBean.getOnlineStatus()) && !"1".equals(counselorDetailBean.getIsHide())) {
                        if (!TextUtils.isEmpty(counselorDetailBean.getUid())) {
                            User user = BaseApplication.getInstance().getUser();
                            Log.e("sig =======  ", user.getSignature());
                            creatOrder();
                        } else {
                            ToastUtils.show(this, "手语师uid不存在，不能呼叫");
                        }
                    } else if ("busy".equals(counselorDetailBean.getOnlineStatus())){
                        ToastUtils.show(this, "手语师忙碌中，请稍后重试");
                    } else if ("offline".equals(counselorDetailBean.getOnlineStatus())){
                        ToastUtils.show(this, "手语师已离线，请稍后重试");
                    }
//                } else {
//                    dialogUtil.showCertificationDialog(this, "您未能完成实名认证，暂不能", "使用该功能，请完成实名认证", true);
//                    dialogUtil.dialog.show();
//                    dialogUtil.setOnClick(new DialogUtil.OnClick() {
//                        @Override
//                        public void leftClick() {
//                            dialogUtil.dialog.dismiss();
//                        }
//
//                        @Override
//                        public void rightClick() {
//                            Intent intent = new Intent(CounselorNewDetailActivity.this, CertificationActivity.class);
//                            intent.putExtra("username", BaseApplication.getInstance().getUser().getUserName());
//                            intent.putExtra("isregister", false);
//                            startActivity(intent);
//                            dialogUtil.dialog.dismiss();
//                        }
//                    });
//                }
            }
        }
    }

    /**
     * 创建订单，成功后发起视频聊天
     */
    public void creatOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("consultantUserName", counselorDetailBean.getUserName());
        params.put("customerUserName", BaseApplication.getInstance().getUser().getUserName());
        getOrderNo(params);
    }

    //获取订单号接口
    private void getOrderNo(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/createSignOrder", params, new SpotsCallBack<OrderMsgBean>(this) {
            @Override
            public void onSuccess(Response response, OrderMsgBean orderMsgBean) {
                if (orderMsgBean != null) {
                    if (orderMsgBean.getCode() >= 0) {
                        //客户呼叫专家时，将订单号orderNo传给对方
                        final OrderBean orderBean = orderMsgBean.getData();
                        String times = orderBean.getUseTimes();
                        String realAthenNameSign = orderBean.getRealAthenNameSign();
                        boolean flag = true;
                        if ("0".equals(realAthenNameSign)) {
                            if (Integer.parseInt(times) <= 0) {
                                flag = false;
                                times = "0";
                            } else {
                                flag = true;
                            }
                            final DialogUtil dialogUtil = new DialogUtil();
                            dialogUtil.showServiceTimesDialog(CounselorNewDetailActivity.this, times, flag);
                            final boolean finalFlag = flag;
                            dialogUtil.setOnClick(new DialogUtil.OnClick() {
                                @Override
                                public void leftClick() {
                                    if (finalFlag && orderBean != null) {
                                        String roomid  = orderBean.getRoomId();;
                                        AVChatActivity.outgoingCall(CounselorNewDetailActivity.this, orderBean.getOrderNo(), roomid, counselorDetailBean.getUid(), counselorDetailBean.getName(), counselorDetailBean.getAvatar());
                                    }
                                    dialogUtil.dialog.dismiss();
                                }

                                @Override
                                public void rightClick() {
                                    Intent intent = new Intent(CounselorNewDetailActivity.this, CertificationActivity.class);
                                    intent.putExtra("isregister", false);
                                    intent.putExtra("username", BaseApplication.getInstance().getUser().getUserName());
                                    startActivity(intent);
                                    dialogUtil.dialog.dismiss();
                                }
                            });
                        }else{
                            if (orderBean != null) {
                                String roomid  = orderBean.getRoomId();;
                                AVChatActivity.outgoingCall(CounselorNewDetailActivity.this, orderBean.getOrderNo(), roomid, counselorDetailBean.getUid(), counselorDetailBean.getName(), counselorDetailBean.getAvatar());
                            }
                        }
                    } else {
                        ToastUtils.show(CounselorNewDetailActivity.this, orderMsgBean.getMsg());
                    }
                }
            }
        });
    }
}
