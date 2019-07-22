package com.hanwin.product.home.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.BaseFragment;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.activity.CertificationActivity;
import com.hanwin.product.home.bean.ImageResultBean;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
import com.hanwin.product.utils.ActivityManager;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.FileStorage;
import com.hanwin.product.utils.FileUploadUtil;
import com.hanwin.product.utils.FileUtil;
import com.hanwin.product.utils.ImageCompressUtils.Luban;
import com.hanwin.product.utils.ImageCompressUtils.OnCompressListener;
import com.hanwin.product.utils.PermissionsActivity;
import com.hanwin.product.utils.PermissionsChecker;
import com.hanwin.product.utils.RegexUtil;
import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.DialogUtil;
import com.hanwin.product.viewutils.PictureChooseDialog;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by zhaopf on 2018/6/23 0023.
 */

public class RegisterFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ILoginView {
    @Bind(R.id.image_picture)
    CircleImageView image_picture;
    @Bind(R.id.edit_person_name)
    EditText edit_person_name;
    @Bind(R.id.edit_phone_number)
    EditText edit_phone_number;
    @Bind(R.id.radiogroup_sex)
    RadioGroup radiogroup_sex;
    @Bind(R.id.edit_code)
    EditText edit_code;
    @Bind(R.id.edit_password)
    EditText edit_password;
    @Bind(R.id.edit_again_password)
    EditText edit_again_password;
    @Bind(R.id.text_code)
    TextView text_code;
    @Bind(R.id.check_imageview)
    ImageButton check_imageview;
    @Bind(R.id.text_register_clause)
    TextView text_register_clause;

    private static final int REQUEST_PICK_IMAGE = 1; //相册选取
    private static final int REQUEST_CAPTURE = 2;  //拍照
    private static final int REQUEST_PICTURE_CUT = 3;  //剪裁图片
    private static final int REQUEST_PERMISSION = 4;  //权限请求
    private boolean isClickCamera;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri imageUri;//原图保存地址
    private String fileName;
    private File newFile;
    private Bitmap bitmap;
    private String imagePath;

    private static final int TASK_TIMER_MESSAGE = 0;
    private static final int TASK_DENIED_MESSAGE = 1;
    private static final int TASK_TIMER_RESET_MESSAGE = 2;
    private Timer mTaskTimer;
    private boolean isTimerStop = false;
    private String sex = "1";
    private boolean isSelect = true;
    private String accoount;
    private String password;
    DialogUtil dialogUtil = new DialogUtil();
    private File compressFile;
    private String avatar = "";
    private LoginHelper loginHelper;
    private User user;
    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        login();
                    }
                }).start();
            } else if (msg.what == 200) {
                compressFile = (File) msg.obj;
                dialogUtil.showLoadingDialog(getActivity(), "");
                uploadFile(compressFile);
            } else if (msg.what == 300) {
                bitmap = BitmapFactory.decodeFile(compressFile.getPath());
                image_picture.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void init() {
        initView();
        initData();
    }

    private void initView() {
        text_register_clause.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        text_register_clause.getPaint().setAntiAlias(true);//抗锯齿
        text_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        text_code.getPaint().setAntiAlias(true);//抗锯齿
    }

    private void initData() {
        mPermissionsChecker = new PermissionsChecker(getActivity());
        radiogroup_sex.setOnCheckedChangeListener(this);//性别监听
        loginHelper = new LoginHelper(this);
    }

    /**
     * 选头像图片
     */
    @OnClick(R.id.image_picture)
    public void select() {
        accoount = edit_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(getActivity(), "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(getActivity(), "请输入正确的手机号码");
        } else {
            selectPhoto();
        }
    }

    /**
     * 性别选择
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radiobtn_man:
                sex = "1";
                break;
            case R.id.radiobtn_woman:
                sex = "0";
                break;
        }
    }

    /**
     * 获取验证码
     */
    @OnClick(R.id.text_code)
    public void code() {
        String accoount = edit_phone_number.getText().toString().trim();
        Long time = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(getActivity(), "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(getActivity(), "请输入正确的手机号码");
        } else {
            params.put("userName", accoount);
            params.put("optionType", "register");
            getCode(params);
            if (edit_code.isFocused()) {
                //已获得焦点
            } else {
                edit_code.requestFocus();//获得焦点
            }
        }
    }

    /**
     * 注册
     */
    @OnClick(R.id.btn_register)
    public void register() {
        String name = edit_person_name.getText().toString().trim();//姓名
        accoount = edit_phone_number.getText().toString().trim();//手机号
        password = edit_password.getText().toString().trim();//密码
        String againpassword = edit_again_password.getText().toString().trim();//确认密码
        String code = edit_code.getText().toString().trim();//验证码
        Map<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(getActivity(), "请输入昵称");
        } else if (RegexUtil.hasEmoji(name)) {
            ToastUtils.show(getActivity(), "不能包含表情符号");
        } else if (TextUtils.isEmpty(accoount)) {
            ToastUtils.show(getActivity(), "请输入手机号码");
        } else if (!RegexUtil.isMobileNO(accoount)) {
            ToastUtils.show(getActivity(), "请输入正确的手机号码");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.show(getActivity(), "请输入密码");
        } else if (TextUtils.isEmpty(againpassword)) {
            ToastUtils.show(getActivity(), "请输入确认密码");
        } else if (!password.equals(againpassword)) {
            ToastUtils.show(getActivity(), "请输入一致密码");
        } else if (TextUtils.isEmpty(code)) {
            ToastUtils.show(getActivity(), "请输入验证码");
        } else if (!isSelect) {
            ToastUtils.show(getActivity(), "请阅读并勾选同意用户协议");
        } else {
            params.put("userName", accoount);
            params.put("name", name);
            params.put("gender", sex);
            params.put("password", password);
            params.put("identifyingCode", code);
            params.put("avatar", avatar);
            login(params);
        }
    }

    private void getCode(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/sendIdentifyingCode", params, new SpotsCallBack<BaseRespMsg>(getActivity()) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(getActivity(), "验证码发送成功");
                        mTaskTimer = new Timer();
                        mTaskTimer.scheduleAtFixedRate(new ConfirmButtonTimerTask(), 0, 1000);
                    } else {
                        ToastUtils.show(getActivity(), baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    private void login(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/register", params, new SpotsCallBack<BaseRespMsg>(getActivity()) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(BaseApplication.getInstance(), "注册成功");
//                        LoginActivity ma = (LoginActivity) getActivity();
//                        ma.changeCurrentItem(0);
                        login();
//                        mhandler.sendEmptyMessage(100);
//                        dialogUtil.showCertificationDialog(getActivity(),"您未完成实名认证，暂不能使用","平台服务，请前往实名认证哦～",false);
//                        dialogUtil.dialog.show();
//                        dialogUtil.setOnClick(new DialogUtil.OnClick() {
//                            @Override
//                            public void leftClick() {
//                                dialogUtil.dialog.dismiss();
//                                mhandler.sendEmptyMessage(100);
//                            }
//
//                            @Override
//                            public void rightClick() {
//                                Intent intent = new Intent(getActivity(), CertificationActivity.class);
//                                intent.putExtra("username",accoount);
//                                intent.putExtra("password",password);
//                                intent.putExtra("isregister",true);
//                                startActivity(intent);
//                                dialogUtil.dialog.dismiss();
//                                getActivity().finish();
//                            }
//                        });
//                        dialogUtil.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                            @Override
//                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                                dialogUtil.dialog.dismiss();
//                                mhandler.sendEmptyMessage(100);
//                                return true;
//                            }
//                        });
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 自动登录
     */
    void login() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", accoount);
        params.put("password", password);
        params.put("jpushId", "");
        customlogin(params);
    }

    private void customlogin(Map<String, Object> params) {
        dialogUtil.showLoadingDialog(getActivity(),"");
        mHttpHelper.post(Contants.BASE_URL + "sign_language/tencent_login", params, new SpotsCallBack<LoginMsgBean>(getActivity(),"msg") {
            @Override
            public void onSuccess(Response response, LoginMsgBean loginMsgBean) {
                if (loginMsgBean != null) {
                    if (loginMsgBean.getCode() >= 0) {
                        user = loginMsgBean.getData();
                        login(user.getUid(), user.getSignature());
                    } else {
                        dialogUtil.dialogLoading.dismiss();
                        ToastUtils.show(BaseApplication.getInstance(), loginMsgBean.getMsg());
                    }
                }
                Looper.loop();
            }
        });
    }

    @OnClick(R.id.check_imageview)
    public void check_imageview() {
        if (!isSelect) {
            isSelect = true;
            check_imageview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.checkbox_checked));
        } else {
            isSelect = false;
            check_imageview.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.checkbox_normal));
        }
    }

    /**
     * 用户注册协议
     */
    @OnClick(R.id.text_register_clause)
    public void text_register_clause() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("title", "用户注册协议");
        intent.putExtra("url", "https://jz.hanwin.com.cn/baseApp/bannixieyi.html");
        intent.putExtra("type", "0");
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTaskTimer != null){
            mTaskTimer.cancel();
        }
    }

    private final Handler timerHandler = new Handler(new Handler.Callback() {
        private int counter = 61;

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == TASK_TIMER_MESSAGE) {
                counter--;
                if (counter == 0) {
                    mTaskTimer.cancel();
                    counter = 61;
                    isTimerStop = true;
                    if(text_code != null) {
                        text_code.setEnabled(true);
                        text_code.setText(getString(R.string.send_code));
                    }
                } else {
                    if(text_code != null){
                        text_code.setEnabled(false);
                        text_code.setText(getString(R.string.btn_hqyzm, counter));
                    }
                }
            } else if (msg.what == TASK_DENIED_MESSAGE) {
            } else if (msg.what == TASK_TIMER_RESET_MESSAGE) {
            }
            return true;
        }
    });

    private class ConfirmButtonTimerTask extends TimerTask {
        public ConfirmButtonTimerTask() {
            timerHandler.sendEmptyMessage(TASK_TIMER_RESET_MESSAGE);
        }

        @Override
        public void run() {
            timerHandler.sendEmptyMessage(TASK_TIMER_MESSAGE);
        }
    }

    private void selectPhoto() {
        PictureChooseDialog pictureChooseDialog = new PictureChooseDialog(getActivity(), R.style.CustomDialog);
        pictureChooseDialog.setOnItemClick(new PictureChooseDialog.OnItemClick() {
            @Override
            public void takeOnClick() {

                //检查权限(6.0以上做权限判断)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
                isClickCamera = true;
            }

            @Override
            public void chooseOnClick() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        startPermissionsActivity();
                    } else {
                        selectFromAlbum();
                    }
                } else {
                    selectFromAlbum();
                }
                isClickCamera = false;
            }
        });

        pictureChooseDialog.show();
    }

    /**
     * 打开系统相机
     */
    private void openCamera() {
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileProvider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            imageUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    /**
     * 从相册选择
     */
    private void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    /**
     * 裁剪
     */
    private void cropPhoto() {
        fileName = "" + System.currentTimeMillis();
        newFile = new FileStorage().createCropFile(fileName);
        Uri outputUri = Uri.fromFile(newFile);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //1 背景墙  2 头像

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), REQUEST_PERMISSION, PERMISSIONS);
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        if (data != null) {
            imageUri = data.getData();
            if (DocumentsContract.isDocumentUri(getActivity(), imageUri)) {
                //如果是document类型的uri,则通过document id处理
                String docId = DocumentsContract.getDocumentId(imageUri);
                if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                    String id = docId.split(":")[1];//解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(imageUri, null);
            } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                //如果是file类型的Uri,直接获取图片路径即可
                imagePath = imageUri.getPath();
            }
            cropPhoto();
        }
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
        cropPhoto();
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE://从相册选择
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                break;
            case REQUEST_CAPTURE://拍照
                if (resultCode == getActivity().RESULT_OK) {
                    cropPhoto();
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                bitmap = null;
                try {
//                    bitmap = BitmapFactory.decodeFile(newFile.getPath());
//                    image_picture.setImageBitmap(bitmap);
//                    mhandler.sendEmptyMessage(200);
                    compressImage(newFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    getActivity().finish();
                } else {
                    if (isClickCamera) {
                        openCamera();
                    } else {
                        selectFromAlbum();
                    }
                }
                break;
        }
    }

    /**
     * 上传图片
     */
    private void uploadFile(final File compressFile) {
        new Thread(new Runnable() {  //开启线程上传文件
            @Override
            public void run() {
                Looper.prepare();
                FileUploadUtil uploadUtil = new FileUploadUtil();
                uploadUtil.setUploadListener(new FileUploadUtil.UploadListener() {
                    @Override
                    public void uploadListener(ImageResultBean respMsg) {
                        dialogUtil.dismiss();
                        if (null != respMsg && !"".equals(respMsg)) {
                            if (respMsg.getCode() >= 0) {
                                ToastUtils.show(getActivity(), "上传成功");
                                avatar = respMsg.getData();
                                mhandler.sendEmptyMessage(300);
                            } else {
                                ToastUtils.show(getActivity(), "上传失败");
                            }
                        } else {
                            ToastUtils.show(getActivity(), "上传失败");
                        }
                        Looper.loop();
                    }

                    @Override
                    public void uploadListener1(BaseRespMsg respMsg) {

                    }
                });
                uploadUtil.uploadFile(compressFile, Contants.BASE_URL + "sign_language/uploadAvatar", accoount);
            }
        }).start();
    }


    /**
     * 登录腾讯im
     * @param uid
     * @param sig
     */
    private void login( String uid, String sig){
        loginHelper.loginSDK(uid,sig);
    }

    @Override
    public void onLoginSuccess() {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录成功 ==== ","ok");
        ToastUtils.show(BaseApplication.getInstance(), "登录成功");
        BaseApplication.getInstance().putUser(user, user.getSessionToken());
        RegisterUtils.initPushMessage();
        if(!Contants.isActivtiesLogin){
            MainActivity.start(getActivity(),null);
        }else{
            ActivityManager.getInstance().finsihActivity("ThirdLoginActivity");
        }
        getActivity().finish();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        dialogUtil.dialogLoading.dismiss();
        Log.e("登录失败 ==== ",errCode + "   "+errMsg);
        ToastUtils.show(BaseApplication.getInstance(), "登录失败");
    }

    /**
     * 压缩图片
     *
     * @param file
     */
    public void compressImage(final File file) {
        Log.e("原图file", "===========" + file.getAbsolutePath().toString());
        try {
            long a = FileUtil.getFileSize(file);
            Log.e("原图file大小：", "===========" + FileUtil.FormetFileSize(a));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Luban.get(getActivity()).load(file) //传要压缩的图片
                .putGear(Luban.THIRD_GEAR)//设定压缩档次，默认三挡
                .setFilename(System.currentTimeMillis() + ".jpg")//设置压缩后图片的名字
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File newfile) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Log.e("newfile", "===========" + newfile.getAbsolutePath().toString());
                        try {
                            long a = FileUtil.getFileSize(newfile);
                            Log.e("newfile大小：", "===========" + FileUtil.FormetFileSize(a));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.obj = newfile;
                        message.what = 200;
                        mhandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
