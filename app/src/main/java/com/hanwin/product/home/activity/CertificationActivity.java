package com.hanwin.product.home.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanwin.product.MainActivity;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.ImageResultBean;
import com.hanwin.product.home.bean.LoginMsgBean;
import com.hanwin.product.tencentim.event.RegisterUtils;
import com.hanwin.product.tencentim.presenter.LoginHelper;
import com.hanwin.product.tencentim.view.ILoginView;
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
import com.hanwin.product.viewutils.PictureChooseDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 实名认证界面
 * Created by zhaopf on 2018/10/17 0017.
 */

public class CertificationActivity extends BaseActivity implements FileUploadUtil.UploadListener, ILoginView {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.text_tag)
    TextView text_tag;
    @Bind(R.id.text_phone_number)
    TextView text_phone_number;
    @Bind(R.id.edit_name)
    EditText edit_name;
    @Bind(R.id.edit_phone_number)
    EditText edit_phone_number;
    @Bind(R.id.edit_id_card)
    EditText edit_id_card;
    @Bind(R.id.edit_disability_certificate)
    EditText edit_disability_certificate;

    @Bind(R.id.image_disability_certificate)
    ImageView image_disability_certificate;

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
    private String imagePath;
    private File compressFile;
    private File file;

    private String username;
    private String password;
    private boolean isregister;

    private LoginHelper loginHelper;
    private User user;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                compressFile = (File) msg.obj;
                if (compressFile != null) {
                    Bitmap bitmap1 = BitmapFactory.decodeFile(compressFile.getPath());
                    image_disability_certificate.setImageBitmap(bitmap1);
                } else {
                    ToastUtils.show(CertificationActivity.this, "请重新选择照片");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("实名认证");
        loginHelper = new LoginHelper(this);
        mPermissionsChecker = new PermissionsChecker(this);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        isregister = getIntent().getBooleanExtra("isregister", false);
        if(RegexUtil.isMobileNO(BaseApplication.getInstance().getUser().getUserName())){
            edit_phone_number.setText(username);
            edit_phone_number.setEnabled(false);
        }else{
            edit_phone_number.setText("");
            edit_phone_number.setEnabled(true);
        }
        String str = "<font color='#ffc626'>*</font>    如果您是残障人士，请务必填写残疾证号码并上传残疾证以便享受“伴你”平台的优惠服务";
        text_tag.setTextSize(15);
        text_tag.setText(Html.fromHtml(str));
    }

    private void initData() {

    }

    @OnClick(R.id.image_disability_certificate)
    public void photo(View view) {
        selectPhoto();
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
    }

    @OnClick(R.id.submit)
    public void submit(View view) {
        String name = edit_name.getText().toString().trim();
        String idcard = edit_id_card.getText().toString().trim();
        String disabilityNum = edit_disability_certificate.getText().toString().trim();
        String phoneNo = edit_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(this, "姓名不能为空");
        } else if (TextUtils.isEmpty(idcard)) {
            ToastUtils.show(this, "身份证号不能为空");
        } else if (!RegexUtil.isValidate18Idcard(idcard)) {
            ToastUtils.show(this, "请输入正确的身份证号");
        } else if (!RegexUtil.isMobileNO(phoneNo)) {
            ToastUtils.show(this, "请输入手机号");
//        } else if (compressFile == null) {
//            ToastUtils.show(this, "请上传残疾证照片");
        } else {
            final Map<String, String> params = new HashMap<>();
            params.put("phoneNo", BaseApplication.getInstance().getUser().getUserName());
            params.put("tel",phoneNo);
            params.put("name", name);
            params.put("identityNum", idcard);
            params.put("disabIdenNum", disabilityNum);
            dialogUtil.showLoadingDialog(CertificationActivity.this, "");
            new Thread(new Runnable() {  //开启线程上传文件
                @Override
                public void run() {
                    Looper.prepare();
                    FileUploadUtil uploadUtil = new FileUploadUtil();
                    uploadUtil.setUploadListener(CertificationActivity.this);
                    uploadUtil.uploadFile1(compressFile, Contants.BASE_URL + "sign_language/signPost", params);
                }
            }).start();
        }
    }

    /**
     * 自动登录
     */
    void login() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", username);
        params.put("password", password);
        params.put("jpushId", "");
        customlogin(params);
    }

    private void customlogin(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/tencent_login", params, new SpotsCallBack<LoginMsgBean>(this, "msg") {
            @Override
            public void onSuccess(Response response, LoginMsgBean loginMsgBean) {
                if (loginMsgBean != null) {
                    if (loginMsgBean.getCode() >= 0) {
//                        ToastUtils.show(BaseApplication.getInstance(), "登录成功");
                        User user = loginMsgBean.getData();
                        BaseApplication.getInstance().putUser(user, user.getSessionToken());
                        login(user.getUid(), user.getSignature());
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), loginMsgBean.getMsg());
                    }
                }
            }
        });
    }

    private void selectPhoto() {
        PictureChooseDialog pictureChooseDialog = new PictureChooseDialog(this, R.style.CustomDialog);
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
        file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);//通过FileProvider创建一个content类型的Uri
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

        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);

        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_PICTURE_CUT);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION, PERMISSIONS);
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        if (data != null) {
            imageUri = data.getData();
            if (DocumentsContract.isDocumentUri(this, imageUri)) {
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
//            cropPhoto();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    compressImage(new File(imagePath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
//        cropPhoto();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                compressImage(new File(imagePath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection老获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
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
                if (resultCode == RESULT_OK) {
//                    cropPhoto();
                    compressImage(file);
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                try {
                    File newfile = FileUtil.compressImage(this, newFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PERMISSION://权限请求
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
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
     * 压缩图片
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
        Luban.get(this).load(file) //传要压缩的图片
                .putGear(Luban.THIRD_GEAR)//设定压缩档次，默认三挡
                .setFilename(System.currentTimeMillis() + ".jpg")//设置压缩后图片的名字
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始前调用，可以在方法内启动 loading UI
                        dialogUtil.showLoadingDialog(CertificationActivity.this, "");
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
                        message.what = 100;
                        handler.sendMessage(message);
                        dialogUtil.dialogLoading.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    @Override
    public void uploadListener(ImageResultBean respMsg) {

    }

    @Override
    public void uploadListener1(BaseRespMsg respMsg) {
        dialogUtil.dismiss();
        if (respMsg != null) {
            if (respMsg.getCode() >= 0) {
                ToastUtils.show(CertificationActivity.this, "实名认证成功");
                if (isregister) {
                    login();
                } else {
                    user = BaseApplication.getInstance().getUser();
                    user.setName(edit_name.getText().toString());
                    user.setRealAthenNameSign("1");
                    BaseApplication.getInstance().putUser(user,user.getSessionToken());
                    finish();
                }
            } else {
                ToastUtils.show(CertificationActivity.this, respMsg.getMsg());
            }
        } else {
            ToastUtils.show(CertificationActivity.this, "实名认证失败");
        }
        Looper.loop();
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
        Log.e("登录成功 ==== ","ok");
        ToastUtils.show(this, "登录成功");
        RegisterUtils.initPushMessage();
        MainActivity.start(this,null);
        finish();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        Log.e("登录失败 ==== ",errCode + "   "+errMsg);
        ToastUtils.show(this, "登录失败");
    }

}
