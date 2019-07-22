package com.hanwin.product.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * TODO 权限管理工具
 *
 * @acthor weiang
 * 2019/4/29 3:42 PM
 */
public class PermissionUtil {

    public static final int RequestCode_Permission_Location = 2001;
    public static final int RequestCode_Permission_Camera = 2002;
    public static final int RequestCode_Permission_SMS = 2003;
    public static final int RequestCode_Permission_ReadStorage = 2004;
    public static final int RequestCode_Permission_CameraReadStorage = 2005;
    public static final int RequestCode_Permission_AdapterStorage = 2006;
    public static final int RequestCode_Permission_PhoneLinkMan = 2007;
    public static final int RequestCode_Permission_Phone = 2008;
    public static final int REQUESTCODE_PERMISSION_VIDEO = 2009;

    //权限被允许
    public static final int Permission_Result_Allow = 1;
    //权限被拒绝
    public static final int Permission_Result_Refuse = 2;
    //非请求权限
    private static final int Permission_Result_No = 3;


    /**
     * TODO:检查视频首页所需权限
     *
     * @acthor weiang
     * 2019/4/29 4:03 PM
     */
    public static boolean permissionVideo(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            int audioStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
            int phoneStatusStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            if (readStorage != PackageManager.PERMISSION_GRANTED || writeStorage != PackageManager.PERMISSION_GRANTED ||
                    cameraStorage != PackageManager.PERMISSION_GRANTED || audioStorage != PackageManager.PERMISSION_GRANTED || phoneStatusStorage != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.READ_PHONE_STATE
                }, REQUESTCODE_PERMISSION_VIDEO);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    /**
     * TODO:检查视频首页所需权限
     *
     * @param permissions  权限集合
     * @param grantResults *
     * @return 布尔值
     */
    public static boolean getPermissionVideoResult(String permissions[], int[] grantResults) {
        return permissions[0].equals(Manifest.permission.READ_CONTACTS)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && permissions[1].equals(Manifest.permission.WRITE_CONTACTS)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && permissions[2].equals(Manifest.permission.CAMERA)
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && permissions[3].equals(Manifest.permission.RECORD_AUDIO)
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && permissions[4].equals(Manifest.permission.READ_PHONE_STATE)
                && grantResults[4] == PackageManager.PERMISSION_GRANTED;
    }

}
