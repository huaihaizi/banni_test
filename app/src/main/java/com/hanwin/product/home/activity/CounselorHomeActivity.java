package com.hanwin.product.home.activity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.adapter.ChatRecordAdapter;
import com.hanwin.product.home.adapter.CounselorListAdapter;
import com.hanwin.product.home.bean.ChatPageInfo;
import com.hanwin.product.home.bean.ChatRecordBean;
import com.hanwin.product.home.bean.ChatRecordMsgBean;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.home.bean.CounselorListMsgBean;
import com.hanwin.product.home.bean.ImageResultBean;
import com.hanwin.product.home.bean.OrderBean;
import com.hanwin.product.home.bean.OrderMsgBean;
import com.hanwin.product.home.bean.PageInfo;
import com.hanwin.product.home.bean.VersionBean;
import com.hanwin.product.home.bean.VersionMsgBean;
import com.hanwin.product.tencentim.activity.AVChatActivity;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.FileUploadUtil;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.utils.recorder.AudioRecorder;
import com.hanwin.product.utils.recorder.Status;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.DialogUtil;
import com.hanwin.product.viewutils.MarqueeTextView;
import com.hanwin.product.viewutils.MyDropDownMenu;
import com.hanwin.product.viewutils.pulltorefresh.PullToRefreshLayout;
import com.hanwin.product.viewutils.pulltorefresh.pullableview.PullableScrollView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 手语师服务列表界面
 */
public class CounselorHomeActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, ILoginView, FileUploadUtil.UploadListener {
    @Bind(R.id.text_name)
    TextView text_name;
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.text_left)
    TextView text_left;
    @Bind(R.id.text_marked_words)
    TextView text_marked_words;

    @Bind(R.id.marquee_textview)
    MarqueeTextView marquee_textview;
    @Bind(R.id.image_my)
    CircleImageView image_my;
    //    @Bind(R.id.image_head)
//    CircleImageView image_head;
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

    private int pageNo = 1;
    private boolean refresh = false;
    private CounselorListAdapter counselorListAdapter;
    private ChatRecordAdapter chatRecordAdapter;
    private List<CounselorBean> counselorBeanList;

    private long exitTime = 0;
    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private CounselorBean counselorBean;
    private String onlineStatus = "0";
    private String transDirect = "0";

    Timer timer = new Timer();//定时器
    private LoginHelper loginHelper;
    LinearLayoutManager linearLayoutManager;
    //导航栏高度
    private int rel_common_height;
    //跑马灯高度
    private int rel1_height;
    //选项菜单高度
    private int myDropDownMenu1_height;
    int stateHeght = 0;//状态栏高度

    private String status = "1";
    AudioRecorder audioRecorder = new AudioRecorder();

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, CounselorHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor);

        ButterKnife.bind(this);
        registerBroadcast();
        initUpdateVersion();//版本更新
        initView();
//        initData();
        initTitleStatus();
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
        myDropDownMenu1.measure(w, h);
        lin_mine.measure(w, h);
        rel_common_height = rel_common.getMeasuredHeight();
        rel1_height = rel1.getMeasuredHeight();
        myDropDownMenu1_height = myDropDownMenu1.getMeasuredHeight();
    }

    /**
     * 初始化导航菜单状态
     */
    @SuppressLint("ApplySharedPref")
    private void initTitleStatus() {
        SharedPreferences pref = this.getSharedPreferences("TRANSLATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("isSeletceNum", 0);
        editor.commit();

        SharedPreferences pref1 = this.getSharedPreferences("ONLINESTATE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref1.edit();
        editor1.putInt("isSeletceNum", 0);
        editor1.commit();
    }

    private void initView() {
        //登录模块初始化
        loginHelper = new LoginHelper(this);
        if (!"".equals(BaseApplication.getInstance().getToken())) {
            User user = BaseApplication.getInstance().getUser();
            if (user != null) {
                if (!TextUtils.isEmpty(user.getUid())) {
                    loginHelper.loginSDK(user.getUid(), user.getSignature());
                } else {
                    BaseApplication.getInstance().clearUser();
                }
            }
        }

        linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler_counselor.setNestedScrollingEnabled(false);
        recycler_counselor.setLayoutManager(linearLayoutManager);

        myDropDownMenu1.setSubMitInfo(new MyDropDownMenu.SubMitInfo() {
            @Override
            public void subMitInfo(String translationType, String onlineStateId) {
                refresh = true;
                transDirect = translationType;
                onlineStatus = onlineStateId;
                pageNo = 1;
            }
        });
    }

    /**
     * 咨询师的情况下获取数据
     */
    private void getRecord() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", BaseApplication.getInstance().getUser().getUserName());
        params.put("pageNum", pageNo);
        params.put("pageSize", 10);
        getRecordList(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contants.isInRoom = false;
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            text_name.setText(BaseApplication.getInstance().getUser().getNickName() != null ? BaseApplication.getInstance().getUser().getNickName() : "");
            if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                if (counselorListAdapter != null) {
                    counselorListAdapter = null;
                }
                if (chatRecordAdapter == null) {
                    ((SimpleItemAnimator) recycler_counselor.getItemAnimator()).setSupportsChangeAnimations(false);//避免刷新加载图片闪烁的问题
                    chatRecordAdapter = new ChatRecordAdapter(this);
                    recycler_counselor.setAdapter(chatRecordAdapter);
                }
                text_left.setText("服务记录");
                marquee_textview.setVisibility(View.GONE);
                myDropDownMenu1.setVisibility(View.GONE);
                lin_not_message.setVisibility(View.VISIBLE);//聊天记录界面显示该控件，后面根据情况再隐藏
            }
            mPullToRefreshLayout.setOnRefreshListener(this);
            mPullToRefreshLayout.autoRefresh();
            image_my.setBorderColor(getResources().getColor(R.color.color_ffc626));
            image_my.setBorderWidth(2);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.mine_image_head);
            Glide.with(this).load(Contants.BASE_IMAGE + BaseApplication.getInstance().getUser().getAvatar())
                    .apply(options)
                    .into(image_my);
        } else {
            Intent intent = new Intent(CounselorHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 聊天记录接口
     *
     * @param params
     */
    private void getRecordList(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/searchDisabAllUsers", params, new SpotsCallBack<ChatRecordMsgBean>(this, "list") {
            @Override
            public void onSuccess(Response response, ChatRecordMsgBean chatRecordMsgBean) {
                if (chatRecordMsgBean != null) {
                    if (chatRecordMsgBean.getCode() >= 0) {
                        ChatPageInfo pageInfo = chatRecordMsgBean.getData();
                        List<ChatRecordBean> chatRecordBeanList = pageInfo.getData();
                        if (chatRecordBeanList != null && chatRecordBeanList.size() > 0) {
                            recycler_counselor.setVisibility(View.VISIBLE);
                            lin_not_message.setVisibility(View.GONE);
                            if (refresh) {
                                mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                pageNo++;
                                if (chatRecordAdapter.chatRecordBeanList != null) {
                                    chatRecordAdapter.chatRecordBeanList.clear();
                                }
                                chatRecordAdapter.chatRecordBeanList = chatRecordBeanList;
                            } else {
                                mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                                pageNo++;
                                if (chatRecordAdapter.chatRecordBeanList == null) {
                                    chatRecordAdapter.chatRecordBeanList = new ArrayList<ChatRecordBean>();
                                }
                                chatRecordAdapter.chatRecordBeanList.addAll(pageInfo.getData());
                            }
                            chatRecordAdapter.notifyDataSetChanged();
                        } else {
                            if (mPullToRefreshLayout != null) {
                                if (refresh) {
                                    mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                                    chatRecordAdapter.chatRecordBeanList = pageInfo.getData();
                                    chatRecordAdapter.notifyDataSetChanged();
                                } else {
                                    mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.DONE);
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
                        ToastUtils.show(BaseApplication.getInstance(), chatRecordMsgBean.getMsg());
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
        if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
            getRecord();
        }
    }

    @OnClick(R.id.lin_mine)
    public void my(View view) {
        if (!TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
            Intent intent = new Intent(CounselorHomeActivity.this, MineActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(CounselorHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
//        getConversationList();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        Log.e("登录失败 ==== ", errCode + "   " + errMsg);
        ToastUtils.show(BaseApplication.getInstance(), "登录已过期，请重新登录");
//        BaseApplication.getInstance().clearUser();
        Intent intent = new Intent(CounselorHomeActivity.this, ThirdLoginActivity.class);
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

    @Override
    public void uploadListener(ImageResultBean respMsg) {
        if (respMsg != null) {
            if (respMsg.getCode() >= 0) {
                Log.e("upload =======  ", " ======== 字幕文件上传成功");
            }
        }
    }

    @Override
    public void uploadListener1(BaseRespMsg respMsg) {

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
                String orderId = intent.getStringExtra("orderNo");
                params.put("orderNo", orderId);//订单号
                if ("signLanguageConsultant".equals(BaseApplication.getInstance().getUser().getRole())) {
                    //当为专家时，account 为客户的账号
                    params.put("userName", BaseApplication.getInstance().getUser().getUserName());
                    params.put("generalUserName", account);//客户账号
                    if (intent.getAction().equals("receiveCall")) {
                        Long chatID = intent.getLongExtra("chatId", 0);
                        params.put("channelId", "");
                        params.put("optionType", "busy");
                        timer = new Timer();
                        timer.schedule(new RequestTimerTask(context, orderId), 1, 60 * 1000);
                    } else if (intent.getAction().equals("hangUp")) {
                        params.put("optionType", "online");
                        timer.cancel();
                    }
                    final File file = new File(Environment.getExternalStorageDirectory().toString() + "/banni/" + orderId + ".json");
                    if (file != null) {
                        Log.e("file  =====   ", file.getPath() + "  === " + file.getName());
                        new Thread() {
                            //开启线程上传文件
                            @Override
                            public void run() {
                                super.run();
                                FileUploadUtil uploadUtil = new FileUploadUtil();
                                uploadUtil.setUploadListener(CounselorHomeActivity.this);
                                uploadUtil.uploadFile(file, Contants.BASE_URL + "sign_language/subtitleDocument", BaseApplication.getInstance().getUser().getUserName(), "subTitle");
                            }
                        }.start();
                    }

                    sendMsg(params, BaseApplication.getInstance().getUser().getRole());
                } else {
                    //当为客户时，account 为专家的账号
//                    String orderNo = intent.getStringExtra("orderNo");//订单号
//                    if (intent.getAction().equals("hangUp")) {
//                        params.put("userName", account);
//                        params.put("generalUserName", BaseApplication.getInstance().getUser().getUserName());//客户账号
//                        params.put("optionType", "online");
//                        sendMsg(params, BaseApplication.getInstance().getUser().getRole());
//                    } else if (intent.getAction().equals("busy")) {
//                        upDataOrderStatus(orderNo, "1");//忙
//                    } else if (intent.getAction().equals("timeout")) {
//                        upDataOrderStatus(orderNo, "3");//无人接听
//                    } else if (intent.getAction().equals("reject")) {
//                        upDataOrderStatus(orderNo, "2");//拒绝
//                    } else if (intent.getAction().equals("cancel")) {
//                        upDataOrderStatus(orderNo, "7");//取消呼叫
//                    }
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
                                    Intent intent2 = new Intent(CounselorHomeActivity.this, CounselorEvaluateActivity.class);
                                    intent2.putExtra("orderNo", (String) params.get("orderNo"));
                                    intent2.putExtra("consultationTime", orderBean.getConsultationTime());
                                    startActivity(intent2);
                                    Log.d("hhh", "----------------CounselorHomeActivity--------------CounselorEvaluateActivity-->:");
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
        PackageManager pm = CounselorHomeActivity.this.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(CounselorHomeActivity.this.getPackageName(), 0);
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
                        PackageManager pm = CounselorHomeActivity.this.getPackageManager();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按返回键不杀生应用
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String topActivityClassName = cn.getClassName();
        String temp[] = topActivityClassName.split("\\.");
        //栈顶Activity的名称
        String currentclass = temp[temp.length - 1];
        return currentclass;
    }
}
