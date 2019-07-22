package com.hanwin.product.home.fragment;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.OkHttpHelper;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.home.activity.CertificationActivity;
import com.hanwin.product.home.activity.CounselorNewDetailActivity;
import com.hanwin.product.home.activity.LoginActivity;
import com.hanwin.product.home.activity.NormalUserListActivity;
import com.hanwin.product.home.activity.ThirdLoginActivity;
import com.hanwin.product.home.adapter.CounselorListAdapter;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.home.bean.CounselorListMsgBean;
import com.hanwin.product.home.bean.CounselorOftenListMsgBean;
import com.hanwin.product.home.bean.OftenPageInfo;
import com.hanwin.product.home.bean.OrderBean;
import com.hanwin.product.home.bean.OrderMsgBean;
import com.hanwin.product.home.bean.PageInfo;
import com.hanwin.product.tencentim.activity.AVChatActivity;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.viewutils.DialogUtil;
import com.hanwin.product.viewutils.pulltorefresh.PullToRefreshLayout;
import com.hanwin.product.viewutils.pulltorefresh.pullableview.PullableScrollView;
import com.liqi.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by admin on 2019/2/15.
 */

public class SlideNavigationFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener, CounselorListAdapter.OnItemClickLitener {
    private TextView textView;

    @Bind(R.id.recycler_counselor)
    RecyclerView recycler_counselor;
    @Bind(R.id.order_scrollview)
    PullableScrollView scrollview;
    @Bind(R.id.refresh_view)
    PullToRefreshLayout mPullToRefreshLayout;

    @Bind(R.id.lin_not_message)
    LinearLayout lin_not_message;

    LinearLayoutManager linearLayoutManager;
    private String transDirect = "0";
    private int pageNo = 1;
    private boolean refresh = false;
    private CounselorListAdapter counselorListAdapter;
    private List<CounselorBean> counselorBeanList;
    private CounselorBean counselorBean;
    private OkHttpHelper okHttpHelper;
    private String mTitle = "";
    private int mrel_common_height;//导航栏高度
    private int mrel1_height;//跑马灯高度
    private int mtl_tab_height;//选项菜单高度
    int stateHeght = 0;//状态栏高度
    LinearLayout lin_mine;
    GifImageView image_gif_activity;
    ImageView image_appointment;

    public static SlideNavigationFragment getInstance(String title, int rel_common_height, int rel1_height, int tl_tab_height, int stateHeght, LinearLayout lin_mine,GifImageView image_gif_activity,ImageView image_appointment) {
        SlideNavigationFragment sf = new SlideNavigationFragment();
        sf.mTitle = title;
        sf.mrel_common_height = rel_common_height;
        sf.mrel1_height = rel1_height;
        sf.mtl_tab_height = tl_tab_height;
        sf.stateHeght = stateHeght;
        sf.lin_mine = lin_mine;
        sf.image_gif_activity = image_gif_activity;
        sf.image_appointment = image_appointment;
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
        initView();
        okHttpHelper = new OkHttpHelper();
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler_counselor.setNestedScrollingEnabled(false);
        recycler_counselor.setLayoutManager(linearLayoutManager);
        switch (mTitle) {
            case "全部领域":
                transDirect = "0";
                break;
            case "日常生活":
                transDirect = "1";
                break;
            case "医疗问诊":
                transDirect = "2";
                break;
            case "银行金融":
                transDirect = "3";
                break;
            case "政府事务":
                transDirect = "4";
                break;
            case "其他领域":
                transDirect = "5";
                break;
            case "常联系":
                transDirect = "6";//常联系为单独接口
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        counselorListAdapter = new CounselorListAdapter(getActivity());
        recycler_counselor.setAdapter(counselorListAdapter);
        counselorListAdapter.setOnItemClickLitener(this);
        mPullToRefreshLayout.setOnRefreshListener(this);
        mPullToRefreshLayout.autoRefresh();
    }

    /**
     * 客户或者无登录的情况下初始化数据
     */
    private void initData() {
        if (!"6".equals(transDirect)) {
            Map<String, Object> params = new HashMap<>();
            params.put("customerUserName", BaseApplication.getInstance().getUser().getUserName());
            params.put("onlineStatus", 0);
            params.put("transDirect", transDirect);
            params.put("pageNum", pageNo);
            params.put("pageSize", 10);
            getDataList(params);
        } else {
            if (!TextUtils.isEmpty(BaseApplication.getInstance().getUser().getUserName())) {
                Map<String, Object> params = new HashMap<>();
                params.put("customerUserName", BaseApplication.getInstance().getUser().getUserName());
                params.put("pageNum", pageNo);
                params.put("pageSize", 10);
                getList(params);
            } else {
                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }
    }


    /**
     * 接口
     *
     * @param params
     */
    private void getDataList(Map<String, Object> params) {
        okHttpHelper.post(Contants.BASE_URL + "sign_technology/sortSign1", params, new SpotsCallBack<CounselorListMsgBean>(getActivity(), "list") {
            @Override
            public void onSuccess(Response response, CounselorListMsgBean counselorListMsgBean) {
                if (counselorListMsgBean != null) {
                    if (counselorListMsgBean.getCode() >= 0) {
                        PageInfo pageInfo = counselorListMsgBean.getData();
                        if (pageInfo != null && pageInfo.getData() != null && pageInfo.getData().size() > 0) {
                            counselorBeanList = pageInfo.getData().get(0);
                            List<CounselorBean> bannerList = new ArrayList<>();
                            if (pageInfo.getData().size() > 1) {
                                bannerList = pageInfo.getData().get(1);
                            }
                            if (counselorBeanList != null && counselorBeanList.size() > 0) {
                                recycler_counselor.setVisibility(View.VISIBLE);
                                lin_not_message.setVisibility(View.GONE);
                                if (refresh) {
                                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    pageNo++;
                                    if (counselorListAdapter.counselorBeanList != null) {
                                        counselorListAdapter.counselorBeanList.clear();
                                    }

                                    if ("0".equals(transDirect) && bannerList != null && bannerList.size() > 0) {
                                        //当是全部领域时，banner 里有数据时，在列表list中添加一条，占位展示banner数据
                                        if(counselorBeanList.size() >= 3){
                                            counselorBeanList.add(2, counselorBeanList.get(2));
                                        }else if(counselorBeanList.size() == 2){
                                            counselorBeanList.add(counselorBeanList.get(0));
                                        }
                                    }
                                    counselorListAdapter.counselorBeanList = counselorBeanList;
                                } else {
                                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                    pageNo++;
                                    if (counselorListAdapter.counselorBeanList == null) {
                                        counselorListAdapter.counselorBeanList = new ArrayList<CounselorBean>();
                                    }
                                    counselorListAdapter.counselorBeanList.addAll(pageInfo.getData().get(0));
                                }
                                counselorListAdapter.transDirect = transDirect;
                                counselorListAdapter.bannerList = bannerList;
                                counselorListAdapter.notifyDataSetChanged();
                                //显示引导层
                                if ("0".equals(transDirect)) {
                                    showGuideLabel();
                                }
                            } else {
                                if (mPullToRefreshLayout != null) {
                                    if (refresh) {
                                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                        counselorListAdapter.counselorBeanList = pageInfo.getData().get(0);
                                        counselorListAdapter.notifyDataSetChanged();
                                    } else {
                                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.DONE);
                                    }
                                }
                            }
                        }
                    } else {
                        if (mPullToRefreshLayout != null) {
                            if (refresh) {
                                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            } else {
                                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                            }
                        }
                        ToastUtils.show(BaseApplication.getInstance(), counselorListMsgBean.getMsg());
                    }
                } else {
                    if (mPullToRefreshLayout != null) {
                        if (refresh) {
                            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                        } else {
                            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                ToastUtils.show(BaseApplication.getInstance(), "请求失败，请稍后重试");
            }

            @Override
            public void onServerError(Response response, int code, String errmsg) {
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                ToastUtils.show(BaseApplication.getInstance(), "请求失败，请稍后重试");
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
            }
        });
    }

    /**
     * 常联系的接口
     *
     * @param params
     */
    private void getList(Map<String, Object> params) {
        okHttpHelper.post(Contants.BASE_URL + "sign_technology/oftenTouch", params, new SpotsCallBack<CounselorOftenListMsgBean>(getActivity(), "list") {
            @Override
            public void onSuccess(Response response, CounselorOftenListMsgBean counselorListMsgBean) {
                if (counselorListMsgBean != null) {
                    if (counselorListMsgBean.getCode() >= 0) {
                        OftenPageInfo oftenPageInfo = counselorListMsgBean.getData();
                        if (oftenPageInfo != null) {
                            counselorBeanList = oftenPageInfo.getData();
                            if (counselorBeanList != null && counselorBeanList.size() > 0) {
                                recycler_counselor.setVisibility(View.VISIBLE);
                                lin_not_message.setVisibility(View.GONE);
                                if (refresh) {
                                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    pageNo++;
                                    if (counselorListAdapter.counselorBeanList != null) {
                                        counselorListAdapter.counselorBeanList.clear();
                                    }
                                    counselorListAdapter.counselorBeanList = counselorBeanList;
                                } else {
                                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                    pageNo++;
                                    if (counselorListAdapter.counselorBeanList == null) {
                                        counselorListAdapter.counselorBeanList = new ArrayList<CounselorBean>();
                                    }
                                    counselorListAdapter.counselorBeanList.addAll(oftenPageInfo.getData());
                                }
                                counselorListAdapter.transDirect = transDirect;
                                counselorListAdapter.notifyDataSetChanged();
                            } else {
                                if (mPullToRefreshLayout != null) {
                                    if (refresh) {
                                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                        counselorListAdapter.counselorBeanList = oftenPageInfo.getData();
                                        counselorListAdapter.notifyDataSetChanged();
                                    } else {
                                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.DONE);
                                    }
                                }
                            }
                        }
                    } else {
                        if (mPullToRefreshLayout != null) {
                            if (refresh) {
                                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            } else {
                                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                            }
                        }
                        ToastUtils.show(BaseApplication.getInstance(), counselorListMsgBean.getMsg());
                    }
                } else {
                    if (mPullToRefreshLayout != null) {
                        if (refresh) {
                            mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                        } else {
                            mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                ToastUtils.show(BaseApplication.getInstance(), "请求失败，请稍后重试");
            }

            @Override
            public void onServerError(Response response, int code, String errmsg) {
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
                ToastUtils.show(BaseApplication.getInstance(), "请求失败，请稍后重试");
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                if (mPullToRefreshLayout != null) {
                    if (refresh) {
                        mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                    } else {
                        mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        refresh = true;
        pageNo = 1;
        toRefresh();
        mPullToRefreshLayout = pullToRefreshLayout;
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        refresh = false;
        toRefresh();
        mPullToRefreshLayout = pullToRefreshLayout;
    }

    private void toRefresh() {
        initData();
    }

    //如果要让BaseFragment内部去监听点击事件，那么请在控件里面设置android:clickable="true"
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public int setLiayoutId() {
        return R.layout.home_slide_fragment;
    }

    @Override
    public void onItemClick(View view, int position) {
        CounselorBean counselorBean = counselorListAdapter.counselorBeanList.get(position);
        if (counselorBean != null) {
            Intent intent = new Intent(getActivity(), CounselorNewDetailActivity.class);
            intent.putExtra("userName", counselorBean.getUserName());
            intent.putExtra("isHide", counselorBean.getIsHide());
            intent.putExtra("uid", counselorBean.getUid());
            startActivity(intent);
        }
    }

    @Override
    public void onCallClick(View view, int position) {
        if (TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(getActivity(), ThirdLoginActivity.class);
            startActivity(intent);
        } else {
            counselorBean = counselorListAdapter.counselorBeanList.get(position);
            if (counselorBean != null) {
                //判断咨询师在线，并不被屏蔽的情况下才能呼叫
                if ("online".equals(counselorBean.getOnlineStatus()) && !"1".equals(counselorBean.getIsHide())) {
                    if (!TextUtils.isEmpty(counselorBean.getUid())) {
                        User user = BaseApplication.getInstance().getUser();
                        Log.e("sig =======  ", user.getSignature());
                        creatOrder(counselorBean,position);
                    } else {
                        ToastUtils.show(getActivity(), "手语师uid不存在，不能呼叫");
                    }
                } else if ("busy".equals(counselorBean.getOnlineStatus())) {
                    ToastUtils.show(getActivity(), "手语师忙碌中，请稍后重试");
                } else if ("offline".equals(counselorBean.getOnlineStatus())) {
                    ToastUtils.show(getActivity(), "手语师已离线，请稍后重试");
                }
            }
        }
    }

    @Override
    public void onFollowClick(View view, int position) {

    }


    /**
     * 创建订单，成功后发起视频聊天
     *
     * @param counselorBean
     */
    public void creatOrder(CounselorBean counselorBean, int position) {
        Map<String, Object> params = new HashMap<>();
        params.put("consultantUserName", counselorBean.getUserName());
        params.put("customerUserName", BaseApplication.getInstance().getUser().getUserName());
        getOrderNo(params,position);
    }

    //获取订单号接口
    private void getOrderNo(Map<String, Object> params, final int position) {
        okHttpHelper.post(Contants.BASE_URL + "sign_language/createSignOrder", params, new SpotsCallBack<OrderMsgBean>(getActivity()) {
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
                            dialogUtil.showServiceTimesDialog(getActivity(), times, flag);
                            final boolean finalFlag = flag;
                            dialogUtil.setOnClick(new DialogUtil.OnClick() {
                                @Override
                                public void leftClick() {
                                    if(finalFlag){
                                        String roomId = orderBean.getRoomId();
                                        AVChatActivity.outgoingCall(getActivity(), orderBean.getOrderNo(), roomId, counselorBean.getUid(), counselorBean.getName(), counselorBean.getAvatar());
                                    }
                                    dialogUtil.dialog.dismiss();
                                }

                                @Override
                                public void rightClick() {
                                    Intent intent = new Intent(getActivity(), CertificationActivity.class);
                                    intent.putExtra("isregister", false);
                                    intent.putExtra("username", BaseApplication.getInstance().getUser().getUserName());
                                    startActivity(intent);
                                    dialogUtil.dialog.dismiss();
                                }
                            });
                        }else{
                            if (orderBean != null) {
                                String roomid = orderBean.getRoomId();
                                AVChatActivity.outgoingCall(getActivity(), orderBean.getOrderNo(), roomid, counselorBean.getUid(), counselorBean.getName(), counselorBean.getAvatar());
                            }
                        }
                    } else if(orderMsgBean.getCode() == -1000){
                        counselorListAdapter.counselorBeanList.get(position).setOnlineStatus("busy");
                        counselorListAdapter.notifyDataSetChanged();
                        ToastUtils.show(getActivity(), orderMsgBean.getMsg());
                    } else {
                        ToastUtils.show(getActivity(), orderMsgBean.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 展示单个的引导层
     */
    private void showSingleGuideLable(){
        if(getActivity() == null){
            return;
        }
        final View decorView = getActivity().getWindow().getDecorView();
        NewbieGuide.with(this)
                .anchor(decorView)
                .setLabel("view_guide1")
                .alwaysShow(false)
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(lin_mine, HighLight.Shape.CIRCLE, 25)
                        .setLayoutRes(R.layout.view_guide_five, R.id.close)
                        .setEverywhereCancelable(false)
                ).show();
    }


    /**
     * 展示引导层
     */
    private void showGuideLabel() {
        if(getActivity() == null){
            return;
        }
        final int allheight = mrel_common_height + mrel1_height + mtl_tab_height;
        recycler_counselor.post(new Runnable() {
            @Override
            public void run() {
                final RectF rectF1 = new RectF();//列表的第三个个item的位置
                final RectF rectF2 = new RectF();//立即呼叫按钮的位置
                TextView text_call = null;
                rectF1.left = 7;
                rectF1.right = Utils.getScreenWidth(getActivity()) - 7;

                View firstItem = recycler_counselor.getChildAt(0);
                View thirdItem = recycler_counselor.getChildAt(2);

                if (firstItem != null) {
                    rectF1.top = allheight + stateHeght + firstItem.getHeight()*2;
                    rectF1.bottom = allheight + stateHeght + firstItem.getHeight()*2 + thirdItem.getHeight();
                    text_call = firstItem.findViewById(R.id.text_call);
                    int[] locations = new int[2];
                    if (text_call != null) {
                        text_call.getLocationInWindow(locations);
                        Log.e("location", locations[0] + "=== bbbb===" + locations[1] + "  ee ");

//                      rectF2.left = Utils.getScreenWidth(NormalUserListActivity.this) - text_call.getWidth() - Utils.px2dip(NormalUserListActivity.this,50);
//                      rectF2.right = Utils.getScreenWidth(NormalUserListActivity.this) - Utils.px2dip(NormalUserListActivity.this,30);
                        rectF2.top = allheight + stateHeght - Utils.px2dip(getActivity(), 60);
                        rectF2.left = locations[0];
                        rectF2.right = locations[0] + text_call.getWidth();
                        rectF2.bottom = allheight + stateHeght + text_call.getHeight() * 2 - Utils.px2dip(getActivity(), 30);
                    }
                }

                if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
                    //没有实名认证展示
                    if(!"1".equals(BaseApplication.getInstance().getUser().getRealAthenNameSign())) {
                        if(Contants.flag){
                            //登录后未实名认证展示
                            showSingleGuideLable();
                        }else{
                            show(rectF1,true);
                        }
                    }else{
                        show(rectF1,false);
                    }
                }else{
                    show(rectF1,false);
                    Contants.flag = true;
                }
            }
        });
    }

    private void show( RectF rectF1,boolean bool){
        final View decorView = getActivity().getWindow().getDecorView();
        if(bool){
            NewbieGuide.with(getActivity())
                    .anchor(decorView)
                    .setLabel("view_guide")
                    .alwaysShow(false)
                    .addGuidePage(GuidePage.newInstance()
                            //getChildAt获取的是屏幕中可见的第一个，并不是数据中的position
                            .addHighLight(rectF1, HighLight.Shape.ROUND_RECTANGLE, 20, new RelativeGuide(R.layout.view_guide_two, Gravity.TOP))
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(image_gif_activity, HighLight.Shape.CIRCLE, 25)
                            .setLayoutRes(R.layout.view_guide_three)
                            .setEverywhereCancelable(true)
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(image_appointment, HighLight.Shape.CIRCLE, 25)
                            .setLayoutRes(R.layout.view_guide_four, R.id.close)
                            .setEverywhereCancelable(false)
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(lin_mine, HighLight.Shape.CIRCLE, 25)
                            .setLayoutRes(R.layout.view_guide_five, R.id.close)
                            .setEverywhereCancelable(false)
                    )
                    .show();
        }else{
            NewbieGuide.with(getActivity())
                    .anchor(decorView)
                    .setLabel("view_guide")
                    .alwaysShow(false)
                    .addGuidePage(GuidePage.newInstance()
                            //getChildAt获取的是屏幕中可见的第一个，并不是数据中的position
                            .addHighLight(rectF1, HighLight.Shape.ROUND_RECTANGLE, 20, new RelativeGuide(R.layout.view_guide_two, Gravity.TOP))
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(image_gif_activity, HighLight.Shape.CIRCLE, 25)
                            .setLayoutRes(R.layout.view_guide_three)
                            .setEverywhereCancelable(true)
                    )
                    .addGuidePage(GuidePage.newInstance()
                            .addHighLight(image_appointment, HighLight.Shape.CIRCLE, 25)
                            .setLayoutRes(R.layout.view_guide_four, R.id.close)
                            .setEverywhereCancelable(false)
                    ).show();
        }
    }
}
