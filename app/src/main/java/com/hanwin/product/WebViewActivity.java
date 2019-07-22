package com.hanwin.product;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.home.activity.ActivitiesWebActivity;
import com.hanwin.product.home.activity.AddSuccessActivity;
import com.hanwin.product.home.activity.ThirdLoginActivity;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity {
    @Bind(R.id.webview)
    WebView webView;
    @Bind(R.id.text_title)
    TextView text_title;
    @Bind(R.id.progressBar1)
    ProgressBar progressBar1;

    private String url;
    private String title;
    private String type;// 1 :传参数 2 ：不传参，普通查看
    private String name;
    String baseurl = "";

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public ValueCallback<Uri> mUploadMessage;
    public final static int FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 2;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;// 表单的结果回调

    public static void startActivity(Context context, String url, String title, String type) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
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
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);// 设置与Js交互的权限
        webView.addJavascriptInterface(new AndroidtoJs(), "aj");
        webView.loadUrl(url);
        if ("1".equals(type)) {
            webView.setWebViewClient(new WebViewClient() {
                //覆写shouldOverrideUrlLoading实现内部显示网页
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar1.setProgress(newProgress);//设置进度值
                    if (newProgress == 100) {
                        webView.evaluateJavascript("javascript:init_params('" + name + "','" + Contants.BASE_URL + "')", new ValueCallback<String>() {
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
        } else {
            webView.setWebViewClient(new WebViewClient() {
                //覆写shouldOverrideUrlLoading实现内部显示网页
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar1.setProgress(newProgress);//设置进度值
                    if (newProgress == 100) {
                        progressBar1.setVisibility(View.GONE);//加载完网页进度条消失
                    }
                }

                //Android < 5.0
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    openFileChooserImpl(uploadMsg);
                }

                //API >= 21(Android 5.0.1)回调此方法
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                    onenFileChooseImpleForAndroid(filePathCallback);
                    return true;
                }
            });
        }
    }

    /**
     * android 5.0 以下开启图片选择（原生）
     * 可以自己改图片选择框架。
     */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    /**
     * android 5.0(含) 以上开启图片选择（原生）
     * 可以自己改图片选择框架。
     */
    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
        switch (requestCode) {
            case FILE_CHOOSER_RESULT_CODE:  //android 5.0以下 选择图片回调
                if (null == mUploadMessage)
                    return;
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                break;
            case FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5:  //android 5.0(含) 以上 选择图片回调
                if (null == mUploadMessageForAndroid5)
                    return;
                if (result != null) {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
                } else {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                }
                mUploadMessageForAndroid5 = null;
                break;
        }
    }

    @OnClick(R.id.imgbtn_back)
    public void back() {
        finish();
    }

    // 继承自Object类
    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        Handler handler = new Handler();

        @JavascriptInterface
        public void submitSuccess(final int type) {
            handler.post(new Runnable() {
                public void run() {
                    // 对传递过来的参数进行处理
                    if (type == 1) {
                        Intent intent = new Intent(WebViewActivity.this, AddSuccessActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            });
        }
    }
}
