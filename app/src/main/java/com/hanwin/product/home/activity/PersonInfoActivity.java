package com.hanwin.product.home.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.User;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.ImageResultBean;
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
import com.hanwin.product.viewutils.PictureChooseDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 个人资料
 */
public class PersonInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, FileUploadUtil.UploadListener {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.image_picture)
    CircleImageView image_picture;
    @Bind(R.id.text_phone_number)
    TextView text_phone_number;
    @Bind(R.id.edit_person_name)
    EditText edit_person_name;
    @Bind(R.id.edit_nick_name)
    EditText edit_nick_name;
    @Bind(R.id.edit_deformit_num)
    EditText edit_deformit_num;


    @Bind(R.id.radiogroup_sex)
    RadioGroup radiogroup_sex;
    @Bind(R.id.radiobtn_man)
    RadioButton radiobtn_man;
    @Bind(R.id.radiobtn_woman)
    RadioButton radiobtn_woman;

    @Bind(R.id.rel_deformit_num)
    RelativeLayout rel_deformit_num;
    @Bind(R.id.view_tag)
    View view_tag;
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
    private File file;
    private Bitmap bitmap;
    private String imagePath;

    private String sex = "1";
    private File compressFile;
    private File compressFile1;//残疾证照片
    private String type = "1";//1 是选择头像   2 选择残疾证照片
    private User user;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                compressFile = (File) msg.obj;
                dialogUtil.showLoadingDialog(PersonInfoActivity.this, "");
                new Thread(new Runnable() {  //开启线程上传文件
                    @Override
                    public void run() {
                        Looper.prepare();
                        FileUploadUtil uploadUtil = new FileUploadUtil();
                        uploadUtil.setUploadListener(PersonInfoActivity.this);
                        uploadUtil.uploadFile(compressFile, Contants.BASE_URL + "sign_language/uploadAvatar", BaseApplication.getInstance().getUser().getUserName());
                    }
                }).start();
            } else if (msg.what == 111) {
                compressFile1 = (File) msg.obj;
                bitmap = BitmapFactory.decodeFile(compressFile1.getPath());
                image_disability_certificate.setImageBitmap(bitmap);
            } else if (msg.what == 200) {
                bitmap = BitmapFactory.decodeFile(compressFile.getPath());
                image_picture.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();
        initData();

    }

    private void initView() {
        user = BaseApplication.getInstance().getUser();
        String nickName = getIntent().getStringExtra("nickName");
        String disabIdenNum = getIntent().getStringExtra("disabIdenNum");
        String picurl = getIntent().getStringExtra("picurl");
        text_title.setText("个人资料");
        mPermissionsChecker = new PermissionsChecker(this);
        edit_nick_name.setText(nickName);
        if (!"signLanguageConsultant".equals(user.getRole())) {
            view_tag.setVisibility(View.VISIBLE);
            image_disability_certificate.setVisibility(View.VISIBLE);
            rel_deformit_num.setVisibility(View.VISIBLE);
            edit_deformit_num.setText(disabIdenNum);
            RequestOptions options1 = new RequestOptions()
                    .placeholder(R.drawable.image_disability_certificate);
            Glide.with(this).load(Contants.BASE_IMAGE + picurl)
                    .apply(options1)
                    .into(image_disability_certificate);
        }
    }


    private void initData() {
        radiogroup_sex.setOnCheckedChangeListener(this);//性别监听
        if ("1".equals(user.getGender())) {
            radiobtn_man.setChecked(true);
        } else if ("0".equals(user.getGender()) || "2".equals(user.getGender())) {
            radiobtn_woman.setChecked(true);
        }
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.image_head_man);
        if(RegexUtil.isMobileNO(user.getUserName())){
            text_phone_number.setText(user.getUserName());
            Glide.with(this).load(Contants.BASE_IMAGE + user.getAvatar())
                    .apply(options)
                    .into(image_picture);
        }else{
            text_phone_number.setText("");
            Glide.with(this).load(user.getAvatar())
                    .apply(options)
                    .into(image_picture);
        }
        edit_person_name.setText(user.getName());
    }

    /**
     * 选头像图片
     */
    @OnClick(R.id.image_picture)
    public void image_picture() {
        type = "1";
        selectPhoto();
    }

    /**
     * 选残疾证照片
     */
    @OnClick(R.id.image_disability_certificate)
    public void picture() {
        type = "2";
        selectPhoto();
    }

    @OnClick(R.id.btn_save)
    public void btn_save() {
        String name = edit_person_name.getText().toString().trim();
        String nick_name = edit_nick_name.getText().toString().trim();
        String deformit_num = edit_deformit_num.getText().toString().trim();
        if (TextUtils.isEmpty(nick_name)) {
            ToastUtils.show(this, "昵称不能为空");
        } else {
            final Map<String, String> params = new HashMap<>();
            params.put("userName", BaseApplication.getInstance().getUser().getUserName());
            params.put("name", name);
            params.put("gender", sex);
            params.put("nickName", nick_name);
            params.put("disableNum", deformit_num);
//            updateUserInfo(params);
            dialogUtil.showLoadingDialog(PersonInfoActivity.this, "");
            new Thread(new Runnable() {  //开启线程上传文件
                @Override
                public void run() {
                    Looper.prepare();
                    FileUploadUtil uploadUtil = new FileUploadUtil();
                    uploadUtil.setUploadListener(PersonInfoActivity.this);
                    uploadUtil.uploadFile1(compressFile1, Contants.BASE_URL + "sign_language/updateUserInfo", params);
                }
            }).start();
        }
    }

    private void updateUserInfo(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_language/updateUserInfo", params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        ToastUtils.show(PersonInfoActivity.this, "保存成功");
                        User user = BaseApplication.getInstance().getUser();
                        user.setName(edit_person_name.getText().toString().trim());
                        user.setGender(sex);
                        BaseApplication.getInstance().putUser(user, user.getSessionToken());
                        finish();
                    } else {
                        ToastUtils.show(BaseApplication.getInstance(), baseRespMsg.getMsg());
                    }
                }
            }
        });
    }

    @OnClick(R.id.imgbtn_back)
    public void backtopre(View view) {
        finish();
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
            if ("1".equals(type)) {
                cropPhoto();//头像进行裁剪
            } else {
                //证件照片不进行裁剪
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
    }

    private void handleImageBeforeKitKat(Intent intent) {
        imageUri = intent.getData();
        imagePath = getImagePath(imageUri, null);
        if ("1".equals(type)) {
            cropPhoto();//头像进行裁剪
        } else {
            //证件照片不进行裁剪
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
                    if ("1".equals(type)) {
                        cropPhoto();//头像进行裁剪
                    } else {
                        compressImage(file);//证件照片不进行裁剪
                    }
                }
                break;
            case REQUEST_PICTURE_CUT://裁剪完成
                bitmap = null;
                try {
//                    bitmap = BitmapFactory.decodeFile(newFile.getPath());
//                    image_picture.setImageBitmap(bitmap);
//                    handler.sendEmptyMessage(100);
                    compressImage(newFile);
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

    @Override
    public void uploadListener(ImageResultBean respMsg) {
        dialogUtil.dismiss();
        if (null != respMsg && !"".equals(respMsg)) {
            if (respMsg.getCode() >= 0) {
                ToastUtils.show(PersonInfoActivity.this, "上传成功");
                handler.sendEmptyMessage(200);
                User user = BaseApplication.getInstance().getUser();
                user.setAvatar(respMsg.getData());
                BaseApplication.getInstance().putUser(user, user.getSessionToken());
            } else {
                ToastUtils.show(PersonInfoActivity.this, "上传失败");
            }
        } else {
            ToastUtils.show(PersonInfoActivity.this, "上传失败");
        }
        Looper.loop();
    }

    @Override
    public void uploadListener1(BaseRespMsg respMsg) {
        dialogUtil.dismiss();
        if (respMsg != null) {
            if (respMsg.getCode() >= 0) {
                ToastUtils.show(PersonInfoActivity.this, "保存成功");
                User user = BaseApplication.getInstance().getUser();
                user.setNickName(edit_nick_name.getText().toString().trim());
                user.setGender(sex);
                BaseApplication.getInstance().putUser(user, user.getSessionToken());
                finish();
            } else {
                ToastUtils.show(PersonInfoActivity.this, respMsg.getMsg());
            }
        } else {
            ToastUtils.show(PersonInfoActivity.this, "保存失败");
        }
        Looper.loop();
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
        Luban.get(this).load(file) //传要压缩的图片
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
                        if ("1".equals(type)) {
                            message.what = 100;//头像
                        } else {
                            message.what = 111;//残疾证照片
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
