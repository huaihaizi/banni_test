package com.hanwin.product.home.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.home.adapter.EducationBackAdapter;
import com.hanwin.product.home.adapter.TestNormalAdapter;
import com.hanwin.product.home.adapter.WorkExperienceAdapter;
import com.hanwin.product.home.bean.CertificatesBean;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.home.bean.CounselorDetailBean;
import com.hanwin.product.home.bean.CounselorDetailMsgBean;
import com.hanwin.product.home.bean.EducationBackBean;
import com.hanwin.product.home.bean.OrderBean;
import com.hanwin.product.home.bean.OrderMsgBean;
import com.hanwin.product.home.bean.WorkExperienceBean;
import com.hanwin.product.tencentim.activity.AVChatActivity;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.DialogUtil;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 咨询详情界面
 */
public class CounselorDetailActivity extends BaseActivity {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_position_year)
    TextView text_position_year;
    @Bind(R.id.text_person_info)
    TextView text_person_info;
    @Bind(R.id.text_see_more)
    TextView text_see_more;
    @Bind(R.id.text_call)
    TextView text_call;
    @Bind(R.id.person_image_bg)
    ImageView person_image_bg;

    @Bind(R.id.recycler_work_exp)
    RecyclerView recycler_work_exp;
    @Bind(R.id.recycler_education_bg)
    RecyclerView recycler_education_bg;
    @Bind(R.id.sliderLayout)
    RollPagerView sliderLayout;

    @Bind(R.id.lin_person)
    LinearLayout lin_person;
    @Bind(R.id.lin_workexp)
    LinearLayout lin_workexp;
    @Bind(R.id.lin_certificates)
    LinearLayout lin_certificates;
    @Bind(R.id.lin_education)
    LinearLayout lin_education;

    @Bind(R.id.view1)
    View view1;
    @Bind(R.id.view2)
    View view2;
    @Bind(R.id.view3)
    View view3;
    @Bind(R.id.view4)
    View view4;

    private String userName;
    private WorkExperienceAdapter workExperienceAdapter;
    private EducationBackAdapter educationBackAdapter;
    private CounselorDetailBean counselorDetailBean;
    private boolean isCheck = false;
    private String isHide;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("个人信息");
        text_position_year.getBackground().setAlpha(100);
        userName = getIntent().getStringExtra("userName");
        isHide = getIntent().getStringExtra("isHide");
        uid = getIntent().getStringExtra("uid");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance());
        recycler_work_exp.setNestedScrollingEnabled(false);
        recycler_work_exp.setLayoutManager(linearLayoutManager);
        workExperienceAdapter = new WorkExperienceAdapter(this);
        recycler_work_exp.setAdapter(workExperienceAdapter);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(BaseApplication.getInstance());
        recycler_education_bg.setNestedScrollingEnabled(false);
        recycler_education_bg.setLayoutManager(linearLayoutManager1);
        educationBackAdapter = new EducationBackAdapter(this);
        recycler_education_bg.setAdapter(educationBackAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        getDataInfo(params);
    }

    private void getDataInfo(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/getSignUserInfo", params, new SpotsCallBack<CounselorDetailMsgBean>(this, "list") {
            @Override
            public void onSuccess(Response response, CounselorDetailMsgBean counselorDetailMsgBean) {
                if (counselorDetailMsgBean != null) {
                    counselorDetailBean = counselorDetailMsgBean.getData();
                    if (counselorDetailBean != null) {
//                        RequestOptions options = new RequestOptions()
//                                .placeholder(R.drawable.counsor_detail_normal);
//                        Glide.with(CounselorDetailActivity.this).load(Contants.BASE_IMAGE + counselorDetailBean.getPicUrl())
//                                .apply(options)
//                                .into(person_image_bg);
//                        text_title.setText(counselorDetailBean.getName());
                        text_name.setText(counselorDetailBean.getName());
                        text_position_year.setText(counselorDetailBean.getAddress() + "  " + counselorDetailBean.getWorkingYears() + "年工作经验");
                        if(!TextUtils.isEmpty(counselorDetailBean.getSuccessCase())){
                            text_person_info.setText(counselorDetailBean.getSuccessCase());
                        }else{
                            lin_person.setVisibility(View.INVISIBLE);
                            view1.setVisibility(View.GONE);
                        }
                        if ("online".equals(counselorDetailBean.getOnlineStatus()) && !"1".equals(isHide)) {


                        }else if ("busy".equals(counselorDetailBean.getOnlineStatus())){
                            text_call.setBackground(getResources().getDrawable(R.drawable.gray_corner_bg));
                            text_call.setText("忙线中");
                            text_call.setTextColor(getResources().getColor(R.color.gray));
                        }else if ("offline".equals(counselorDetailBean.getOnlineStatus())){
                            text_call.setBackground(getResources().getDrawable(R.drawable.gray_corner_bg));
                            text_call.setText("已离线");
                            text_call.setTextColor(getResources().getColor(R.color.gray));
                        }
                        List<CertificatesBean> certificates = counselorDetailBean.getCertificates();
                        if(certificates != null && certificates.size() > 0){
                            initSlider(counselorDetailBean.getCertificates());
                        }else {
                            lin_certificates.setVisibility(View.GONE);
                            view3.setVisibility(View.GONE);
                        }
                        List<WorkExperienceBean> workExperienceBeanList = counselorDetailBean.getWorkExperience();
                        if(workExperienceBeanList != null && workExperienceBeanList.size() > 0){
                            workExperienceAdapter.list = counselorDetailBean.getWorkExperience();
                            workExperienceAdapter.notifyDataSetChanged();
                        }else{
                            lin_workexp.setVisibility(View.GONE);
                            view2.setVisibility(View.GONE);
                        }
                        List<EducationBackBean> educationBackBeanList = counselorDetailBean.getEducation();
                        if(educationBackBeanList != null && educationBackBeanList.size() > 0){
                            educationBackAdapter.list = counselorDetailBean.getEducation();
                            educationBackAdapter.notifyDataSetChanged();
                        }else{
                            lin_education.setVisibility(View.GONE);
                            view4.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }


    private void initSlider(List<CertificatesBean> certificatesBeanList) {
        if (counselorDetailBean.getCertificates() != null && counselorDetailBean.getCertificates().size() > 0) {
            sliderLayout.setVisibility(View.VISIBLE);
        } else {
            sliderLayout.setVisibility(View.GONE);
        }
        if (sliderLayout != null) {
            sliderLayout.setPlayDelay(3000);
            //设置透明度
            sliderLayout.setAnimationDurtion(500);
            //设置适配器
//            sliderLayout.setAdapter(new TestNormalAdapter(certificatesBeanList, this));
            if (null != certificatesBeanList && certificatesBeanList.size() > 1) {
                sliderLayout.setHintView(new ColorPointHintView(BaseApplication.getInstance(), getResources().getColor(R.color.color_f0bb28), getResources().getColor(R.color.gray)));
            } else {
                sliderLayout.setHintView(null);
            }
            sliderLayout.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    @OnClick(R.id.text_see_more)
    public void text_see_more(View view) {
        if (!isCheck) {
            isCheck = true;
            text_person_info.setMaxLines(200);
            text_person_info.setEllipsize(null);
            text_see_more.setText("收起");
            Drawable drawable = getResources().getDrawable(R.drawable.up);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            text_see_more.setCompoundDrawables(null, null, drawable, null);
        } else {
            isCheck = false;
            text_person_info.setMaxLines(3);
            text_person_info.setEllipsize(TextUtils.TruncateAt.END);
            text_see_more.setText("点击查看更多");
            Drawable drawable = getResources().getDrawable(R.drawable.down);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            text_see_more.setCompoundDrawables(null, null, drawable, null);
        }
    }


    @OnClick(R.id.text_call)
    public void call(View view) {
        if (TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (counselorDetailBean != null) {
//                if ("1".equals(BaseApplication.getInstance().getUser().getRealAthenNameSign())) {
                    //判断咨询师在线，并不被屏蔽的情况下才能呼叫
                    if ("online".equals(counselorDetailBean.getOnlineStatus()) && !"1".equals(isHide)) {
                        if (!TextUtils.isEmpty(uid)) {
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
//                            Intent intent = new Intent(CounselorDetailActivity.this, CertificationActivity.class);
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
                        OrderBean orderBean = orderMsgBean.getData();
                        if (orderBean != null) {
                            String roomid = 1 + (int) (Math.random() * 1000000) + "";
                            AVChatActivity.outgoingCall(CounselorDetailActivity.this, orderBean.getOrderNo(), roomid, uid, counselorDetailBean.getName(), counselorDetailBean.getAvatar());
                        }
                    } else {
                        ToastUtils.show(CounselorDetailActivity.this, orderMsgBean.getMsg());
                    }
                }
            }
        });
    }
}
