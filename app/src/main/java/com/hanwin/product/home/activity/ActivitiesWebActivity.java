package com.hanwin.product.home.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.utils.Contants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivitiesWebActivity extends BaseActivity {
    @Bind(R.id.webview)
    WebView webView;
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.progressBar1)
    ProgressBar progressBar1;

    // 1 :传参数
    public static final String TYPE_HAS_PARAMETER = "1";
    // 2 ：不传参，普通查看
    public static final String TYPE_NO_PARAMETER = "2";
    // 3 ：需要拼接基础地址的类型
    public static final String TYPE_REACTIVE_URL = "3";
    // 4 ：从广播入口跳转过来的类型
    public static final String TYPE_RECEIVER_URL = "4";

    private String url;
    private String title;
    // 1 :传参数 2 ：不传参，普通查看
    private String type;
    private String name;
    String plaform = "android";

    public static void startActivity(Context context, String url, String title, String type) {
        Intent intent = new Intent(context, ActivitiesWebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        if (TYPE_RECEIVER_URL.equals(type)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
//      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        initData();
    }

    /**
     * 初始化
     */
    private void initData() {
        type = getIntent().getStringExtra("type");
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        name = BaseApplication.getInstance().getUser().getUserName();
        text_title.setText(title);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setJavaScriptEnabled(true);// 设置与Js交互的权限
        webView.addJavascriptInterface(new AndroidtoJs(), "aj");
        if (url == null) {
            url = "";
        }
        if (TYPE_REACTIVE_URL.equals(type)) {
            url = Contants.BASE_WEB_URL + url;
        }
        if (!url.startsWith("http:") && !url.startsWith("https:")) {
            url = Contants.BASE_WEB_URL + url;
        }
        webView.loadUrl(url);
//      if("1".equals(type)){
        webView.setWebViewClient(new WebViewClient() {
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //开始加载网页时显示进度条BASE_URL
                progressBar1.setVisibility(View.VISIBLE);
                //设置进度值
                progressBar1.setProgress(newProgress);
                if (newProgress == 100) {
                    //String.format()
                    webView.evaluateJavascript("javascript:init_param('" + name + "','" + plaform + "','" + Contants.TRANSMIT_WEB_URL + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //此处为 js 返回的结果
                            Log.e("v", value);
                        }
                    });
                    progressBar1.setVisibility(View.GONE);//加载完网页进度条消失
                }
                super.onProgressChanged(view, newProgress);
            }
        });
//        }else{
//            webView.setWebViewClient(new WebViewClient(){
//                //覆写shouldOverrideUrlLoading实现内部显示网页
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    view.loadUrl(url);
//                    return true;
//                }
//            });
//        }
    }

    @OnClick(R.id.imgbtn_back)
    public void back() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

    }

    // 继承自Object类
    public class AndroidtoJs extends Object {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        Handler handler = new Handler();

        @JavascriptInterface
        public void goTo(final String title, final String url) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // 对传递过来的参数进行处理
                    if ("我的时长".equals(title) || "收奖地址".equals(title)) {
                        if (TextUtils.isEmpty(BaseApplication.getInstance().getToken())) {
                            Intent intent = new Intent(ActivitiesWebActivity.this, ThirdLoginActivity.class);
                            intent.putExtra("type", 1);
                            startActivity(intent);
                        } else {
                            ActivitiesWebActivity.startActivity(ActivitiesWebActivity.this, Contants.BASE_WEB_URL + url, title, "1");
                        }
                    } else if ("往期名单".equals(title)) {
                        ActivitiesWebActivity.startActivity(ActivitiesWebActivity.this, Contants.BASE_WEB_URL + url, title, "1");
                    }
                }
            });
        }
    }


    //android webview点击返回键返回上一个html
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            // 返回前一个页面
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
