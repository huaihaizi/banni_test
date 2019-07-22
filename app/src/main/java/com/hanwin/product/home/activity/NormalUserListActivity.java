package com.hanwin.product.home.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.SlidingTabLayout;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.FloatImageBean;
import com.hanwin.product.home.bean.FloatImageMsgBean;
import com.hanwin.product.home.bean.OrderBean;
import com.hanwin.product.home.bean.OrderMsgBean;
import com.hanwin.product.home.bean.ServiceTimesBean;
import com.hanwin.product.home.bean.ServiceTimesMsgBean;
import com.hanwin.product.home.bean.VersionBean;
import com.hanwin.product.home.bean.VersionMsgBean;
import com.hanwin.product.home.fragment.SlideNavigationFragment;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.RegexUtil;
import com.hanwin.product.utils.TimeUtils;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.DialogUtil;
import com.hanwin.product.viewutils.MarqueeTextView;
import com.hanwin.product.viewutils.MyDropDownMenu;
import com.hanwin.product.viewutils.pulltorefresh.PullToRefreshLayout;
import com.hanwin.product.viewutils.pulltorefresh.pullableview.PullableScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;


/**
 * 普通用户的视角界面
 */
public class NormalUserListActivity extends BaseActivity implements ILoginView {
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_left)
    TextView text_left;
    @Bind(R.id.text_marked_words)
    TextView text_marked_words;
    @Bind(R.id.image_appointment)
    ImageView image_appointment;
    @Bind(R.id.image_gif_activity)
    GifImageView image_gif_activity;

    @Bind(R.id.marquee_textview)
    MarqueeTextView marquee_textview;
    @Bind(R.id.image_my)
    CircleImageView image_my;
    @Bind(R.id.image_head)
    CircleImageView image_head;
    @Bind(R.id.lin_not_message)
    LinearLayout lin_not_message;
    @Bind(R.id.myDropDownMenu1)
    MyDropDownMenu myDropDownMenu1;

    @Bind(R.id.recycler_counselor)
    RecyclerView recycler_counselor;
    @Bind(R.id.order_scrollview)
    PullableScrollView scrollview;
    @Bind(R.id.refresh_view)
    PullToRefreshLayout mPullToRefreshLayout;

    @Bind(R.id.rel_common)
    RelativeLayout rel_common;
    @Bind(R.id.rel1)
    RelativeLayout rel1;
    @Bind(R.id.lin_mine)
    LinearLayout lin_mine;
    @Bind(R.id.rel2)
    RelativeLayout rel2;

    @Bind(R.id.vp)
    ViewPager vp;
    @Bind(R.id.tl_tab)
    SlidingTabLayout tl_tab;

    private long exitTime = 0;

    Timer timer = new Timer();//定时器
    private LoginHelper loginHelper;
    private int rel_common_height;//导航栏高度
    private int rel1_height;//跑马灯高度
    private int tl_tab_height;//选项菜单高度
    int stateHeght = 0;//状态栏高度

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "常联系", "全部领域", "日常生活"
            , "医疗问诊", "银行金融", "政府事务", "其他领域"
    };
    private MyPagerAdapter mAdapter;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, NormalUserListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);

        ButterKnife.bind(this);
        registerBroadcast();
        initUpdateVersion();//版本更新
        initView();
//        initData();
        //清除通知栏
        clearNotification();

        //状态栏高度获取
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateHeght = getResources().getDimensionPixelSize(resourceId);
        }

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rel_common.measure(w, h);
        rel1.measure(w, h);
        rel2.measure(w, h);
        lin_mine.measure(w, h);
        rel_common_height = rel_common.getMeasuredHeight();
        rel1_height = rel1.getMeasuredHeight();
        tl_tab_height = rel2.getMeasuredHeight();

        for (String title : mTitles) {
            mFragments.add(SlideNavigationFragment.getInstance(title, rel_common_height, rel1_height, tl_tab_height, stateHeght, lin_mine, image_gif_activity, image_appointment));
        }
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        tl_tab.setViewPager(vp);
        vp.setCurrentItem(1);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


    private void initView() {
        getFloatImage();//获取弹窗
        //登录模块初始化
        loginHelper = new LoginHelper(this);
        if (!"".equals(BaseApplication.getInstance().getToken())) {
            User user = BaseApplication.getInstance().getUser();
            if (user != null) {
//                statisticalStartupTimes();//统计已登录用户的启动次数
                if (!TextUtils.isEmpty(user.getUid())) {
                    loginHelper.loginSDK(user.getUid(), user.getSignature());
                } else {
                    BaseApplication.getInstance().clearUser();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        text_title.setText("手语翻译");
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            isTodayFirstLogin();//判断是否每天第一次启动
            String url = "";
            if (RegexUtil.isMobileNO(BaseApplication.getInstance().getUser().getUserName())) {
                url = Contants.BASE_IMAGE + BaseApplication.getInstance().getUser().getAvatar();
            } else {
                url = BaseApplication.getInstance().getUser().getAvatar();
            }
            //是否是实名认证
            if ("1".equals(BaseApplication.getInstance().getUser().getRealAthenNameSign())) {
                lin_mine.setVisibility(View.GONE);
                image_head.setVisibility(View.VISIBLE);
                image_head.setBorderColor(getResources().getColor(R.color.color_f0eff5));
                image_head.setBorderWidth(2);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.mine_image_head);
                Glide.with(this).load(url)
                        .apply(options)
                        .into(image_head);
            } else {
                image_head.setVisibility(View.GONE);
                lin_mine.setVisibility(View.VISIBLE);
                image_my.setBorderColor(getResources().getColor(R.color.color_ffc626));
                image_my.setBorderWidth(2);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.mine_image_head);
                Glide.with(this).load(url)
                        .apply(options)
                        .into(image_my);
            }
//            text_name.setText(BaseApplication.getInstance().getUser().getNickName() != null ? BaseApplication.getInstance().getUser().getNickName() : "");
        } else {
//            text_name.setText("去登录");
            lin_mine.setVisibility(View.GONE);
            image_head.setVisibility(View.VISIBLE);
            image_head.setBorderColor(getResources().getColor(R.color.color_f0eff5));
            image_head.setBorderWidth(2);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.mine_image_head);
            Glide.with(this).load("")
                    .apply(options)
                    .into(image_head);
        }
    }

    /**
     * 未实名认证
     *
     * @param view
     */
    @OnClick(R.id.lin_mine)
    public void my(View view) {
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(NormalUserListActivity.this, MineActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.image_head)
    public void myInfo(View view) {
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(NormalUserListActivity.this, MineActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(NormalUserListActivity.this, ThirdLoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 预约
     *
     * @param view
     */
    @OnClick(R.id.image_appointment)
    public void make_appointment(View view) {
        Intent intent = new Intent(NormalUserListActivity.this, AppointmentCenterActivity.class);
        startActivity(intent);
    }

    /**
     * 活动
     *
     * @param view
     */
    @OnClick(R.id.image_gif_activity)
    public void take_activity(View view) {
        Intent intent = new Intent(NormalUserListActivity.this, ActivitiesListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Nor == =", "onDestroy");
        if (timer != null) {
            timer.cancel();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    //腾讯im登录回调
    @Override
    public void onLoginSuccess() {
        Log.e("登录成功 ==== ", "ok");
        RegisterUtils.initPushMessage();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        Log.e("登录失败 ==== ", errCode + "   " + errMsg);
        ToastUtils.show(BaseApplication.getInstance(), "登录已过期，请重新登录");
//        BaseApplication.getInstance().clearUser();
        Intent intent = new Intent(NormalUserListActivity.this, ThirdLoginActivity.class);
        startActivity(intent);
        finish();
    }

    //============= 以下逻辑为之前首页的逻辑，现放在列表页  =============
    MyReceiver receiver;

    private void registerBroadcast() {
        // 注册广播接收者 接收挂断或者接收视频时发出的广播
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("receiveCall");//接听
        filter.addAction("hangUp");//挂断
        filter.addAction("timeout");//超时未接听
        filter.addAction("busy");//忙线
        filter.addAction("reject");//拒绝
        filter.addAction("cancel");//拒绝
        registerReceiver(receiver, filter);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Map<String, Object> params = new HashMap<>();
//                String account = intent.getStringExtra("account");//此账号为对方的账号
                String account = intent.getStringExtra("account");//此账号为对方的账号
                if (!TextUtils.isEmpty(account)) {
//                    String[] accounts = intent.getStringExtra("account").split("_");
//                    if (accounts != null && accounts.length == 2) {
//                        account = accounts[1];
//                    }
                    account = account.replace("banni_", "");
                }
                params.put("userName", BaseApplication.getInstance().getUser().getUserName());
                params.put("updateOrCreateView", "createView");//更新咨询师状态
                params.put("orderNo", intent.getStringExtra("orderNo"));//订单号
                if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                    //当为专家时，account 为客户的账号
                    params.put("userName", BaseApplication.getInstance().getUser().getUserName());
                    params.put("generalUserName", account);//客户账号
                    params.put("generalSign", "1");
                    if (intent.getAction().equals("receiveCall")) {
                        Long chatID = intent.getLongExtra("chatId", 0);
                        params.put("channelId", "");
                        params.put("optionType", "busy");
                        timer = new Timer();
                        timer.schedule(new RequestTimerTask(context, intent.getStringExtra("orderNo")), 1, 60 * 1000);
                    } else if (intent.getAction().equals("hangUp")) {
                        params.put("optionType", "online");
                        timer.cancel();
                    }
                    sendMsg(params, BaseApplication.getInstance().getUser().getRole());
                } else {
                    //当为客户时，account 为专家的账号
                    String orderNo = intent.getStringExtra("orderNo");//订单号
                    if (intent.getAction().equals("hangUp")) {
                        params.put("userName", account);
                        params.put("generalUserName", BaseApplication.getInstance().getUser().getUserName());//客户账号
                        params.put("optionType", "online");
                        params.put("generalSign", "0");
                        sendMsg(params, BaseApplication.getInstance().getUser().getRole());
                    } else if (intent.getAction().equals("busy")) {
                        upDataOrderStatus(orderNo, "1");//忙
                    } else if (intent.getAction().equals("timeout")) {
                        upDataOrderStatus(orderNo, "3");//无人接听
                    } else if (intent.getAction().equals("reject")) {
                        upDataOrderStatus(orderNo, "2");//拒绝
                    } else if (intent.getAction().equals("cancel")) {
                        upDataOrderStatus(orderNo, "7");//取消呼叫
                    }
                }
            }
        }
    }

    /**
     * 更改咨询师状态接口
     *
     * @param params
     * @param role
     */
    private void sendMsg(final Map<String, Object> params, final String role) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/updateStatus", params, new SpotsCallBack<OrderMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, OrderMsgBean orderMsgBean) {
                if (orderMsgBean != null) {
                    if (orderMsgBean.getCode() >= 0) {
                        //当为在线状态时，说明视频结束，分别在专家和客户端弹出相应的评价界面
                        if ("online".equals(params.get("optionType"))) {
                            OrderBean orderBean = orderMsgBean.getData();
                            if ("signLanguageConsultant".equals(role)) {
                                if (!"CounselorEvaluateActivity".equals(getCurrClassName())) {
//                                    Intent intent2 = new Intent(NormalUserListActivity.this, CounselorEvaluateActivity.class);
//                                    intent2.putExtra("orderNo", (String) params.get("orderNo"));
//                                    intent2.putExtra("consultationTime", orderBean.getConsultationTime());
//                                    Log.d("hhh", "----------------NormalUserListActivity--------111------CounselorEvaluateActivity-->:");
//                                    startActivity(intent2);
                                }
                            } else {
                                if (!"UserEvaluateActivity".equals(getCurrClassName())) {
                                    Intent intent1 = new Intent(NormalUserListActivity.this, UserEvaluateActivity.class);
                                    intent1.putExtra("consultantUserName", orderBean.getConsultantUserName());
                                    intent1.putExtra("name", orderBean.getName());
                                    intent1.putExtra("avatar", orderBean.getAvatar());
                                    intent1.putExtra("consultationTime", orderBean.getConsultationTime());
                                    intent1.putExtra("orderNo", (String) params.get("orderNo"));
                                    startActivity(intent1);
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        });
    }


    /**
     * 更新订单状态
     */
    private void upDataOrderStatus(String orderNo, String callStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("callStatus", callStatus);
        updateOrderStatus(params);
    }


    /**
     * 更新订单状态接口
     *
     * @param params
     */
    private void updateOrderStatus(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/updateOrderLog", params, new SpotsCallBack<BaseRespMsg>(this, "msg") {
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


    /**
     * 版本更新接口
     */
    private void initUpdateVersion() {
        PackageManager pm = NormalUserListActivity.this.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(NormalUserListActivity.this.getPackageName(), 0);
            String versionNmae = pi.versionName;
            Map<String, Object> params = new HashMap<>();
            getversion(params);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 版本更新接口
     *
     * @param params
     */
    private void getversion(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/getLastVersion", params, new SpotsCallBack<VersionMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, VersionMsgBean versionMsgBean) {
                if (versionMsgBean != null) {
                    if (versionMsgBean.getCode() >= 0) {
                        VersionBean versionBean = versionMsgBean.getData();
                        PackageManager pm = NormalUserListActivity.this.getPackageManager();
                        if (versionBean != null) {
                            try {
                                PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
                                float lastVersion = Float.parseFloat(versionBean.getVersion());
                                float localVersion = Float.parseFloat(pi.versionName);
                                if (lastVersion > localVersion) {
                                    updateVersion(versionBean.getForceVersion());
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), versionMsgBean.getMsg());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次返回键将退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 版本更新
     */
    private void updateVersion(String force) {
        final DialogUtil dialogUtil = new DialogUtil();
        if (!TextUtils.isEmpty(force) && "1".equals(force)) {
            dialogUtil.infoDialog(this, "更新", "检测到新版本,请更新", true, false);
        } else {
            dialogUtil.infoDialog(this, "更新", "检测到新版本,请更新", true, true);
        }
        dialogUtil.dialog.setCancelable(false);
        dialogUtil.setOnClick(new DialogUtil.OnClick() {
            @Override
            public void leftClick() {
                Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.hanwin.product");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            }

            @Override
            public void rightClick() {
                dialogUtil.dialog.dismiss();
            }
        });
    }

    /**
     * 定时任务
     */
    class RequestTimerTask extends TimerTask {
        private Context mcontext;
        private String orderNo;

        public RequestTimerTask(Context context, String orderNo) {
            this.mcontext = context;
            this.orderNo = orderNo;
        }

        @Override
        public void run() {
            Log.e("timer ======= ", "隔了一分钟了");
            heartBeat(orderNo);
        }
    }

    /**
     * 心跳接口
     */
    private void heartBeat(String orderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("count", "1");
        heartBeat(params);
    }


    /**
     * 心跳接口
     *
     * @param params
     */
    private void heartBeat(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/checkSignStatus", params, new SpotsCallBack<BaseRespMsg>(this, "msg") {
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

    /**
     * 清除所有通知栏通知
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * 获取当前栈顶Activity的名称
     *
     * @return
     */
    private String getCurrClassName() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String topActivityClassName = cn.getClassName();
        String temp[] = topActivityClassName.split("\\.");
        //栈顶Activity的名称
        String currentclass = temp[temp.length - 1];
        return currentclass;
    }

    /**
     * 保存每次启动app的时间
     */
    private void saveExitTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String time = df.format(new Date());// 获取当前的日期
        SharedPreferences.Editor editor = getSharedPreferences("LastLoginTime", MODE_PRIVATE).edit();
        editor.putString("LoginTime", time);
        editor.commit();
    }

    /**
     * 判断是否是当日第一次登陆
     */
    private void isTodayFirstLogin() {
        SharedPreferences preferences = getSharedPreferences("LastLoginTime", MODE_PRIVATE);
        String lastTime = preferences.getString("LoginTime", "");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String todayTime = df.format(new Date());// 获取当前的日期
        if (lastTime.equals(todayTime)) { //如果两个时间段相等
            Log.e("Time", lastTime);
        } else {
            Log.e("date", lastTime);
            Log.e("todayDate", todayTime);
            getServiceTimes();
            saveExitTime();
        }
    }

    /**
     * 获取服务次数
     */
    private void getServiceTimes() {
        final User user = BaseApplication.getInstance().getUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userName", user.getUserName());
        mHttpHelper.post(Contants.BASE_URL + "sign_language/getUseTimes", params, new SpotsCallBack<ServiceTimesMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, ServiceTimesMsgBean serviceTimesMsgBean) {
                if (serviceTimesMsgBean != null) {
                    if (serviceTimesMsgBean.getCode() >= 0) {
                        ServiceTimesBean serviceTimesBean = serviceTimesMsgBean.getData();
                        if (serviceTimesBean != null) {
                            String times = serviceTimesBean.getUseTimes();
                            String realAthenNameSign = serviceTimesBean.getRealAthenNameSign();
                            boolean flag = true;
                            if ("0".equals(realAthenNameSign)) {
                                try {
                                    if (Integer.parseInt(times) <= 0) {
                                        flag = false;
                                        times = "0";
                                    } else {
                                        flag = true;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                final DialogUtil dialogUtil = new DialogUtil();
                                dialogUtil.showServiceTimesDialog(NormalUserListActivity.this, times, flag);
                                dialogUtil.setOnClick(new DialogUtil.OnClick() {
                                    @Override
                                    public void leftClick() {
                                        dialogUtil.dialog.dismiss();
                                    }

                                    @Override
                                    public void rightClick() {
                                        Intent intent = new Intent(NormalUserListActivity.this, CertificationActivity.class);
                                        intent.putExtra("isregister", false);
                                        intent.putExtra("username", user.getUserName());
                                        startActivity(intent);
                                        dialogUtil.dialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取活动弹窗的图片url
     */
    private void getFloatImage() {
        Map<String, Object> params = new HashMap<>();
        mHttpHelper.post(Contants.BASE_URL + "sign_technology/queryFloatUrl", params, new SpotsCallBack<FloatImageMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, FloatImageMsgBean floatImageMsgBean) {
                if (floatImageMsgBean != null) {
                    final FloatImageBean floatImageBean = floatImageMsgBean.getData();
                    if (floatImageBean != null) {
                        SharedPreferences preferences = getSharedPreferences("week", Context.MODE_PRIVATE);
                        int week = preferences.getInt("week", 0);
                        //每周弹一次活动窗口
                        if (TimeUtils.getWeek() != week) {
                            dialogUtil.showFloatImage(NormalUserListActivity.this, floatImageBean.getFloatUrl());
                            dialogUtil.setOnClick(new DialogUtil.OnClick() {
                                @Override
                                public void leftClick() {
                                    ActivitiesWebActivity.startActivity(NormalUserListActivity.this, floatImageBean.getLinkUrl(), floatImageBean.getTitle(), "1");
                                    dialogUtil.dialog.dismiss();
                                }

                                @Override
                                public void rightClick() {
                                    dialogUtil.dialog.dismiss();
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
