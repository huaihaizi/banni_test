package com.hanwin.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.tencentim.bean.CustomeModel;
import com.hanwin.product.utils.JobSchedulerManager;
import com.hanwin.product.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private int versionCode = 0; // 首次启动标记
    private CustomeModel customeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("VERSION_CODE", Context.MODE_PRIVATE);
        versionCode = preferences.getInt("versionCode", 0);
        Intent intent = getIntent();
        if (intent != null) {
            customeModel = (CustomeModel) intent.getSerializableExtra("customeModel");
        }
        //申请权限
        requestBasicPermission();
        //  启动系统任务 保活
        JobSchedulerManager mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            customeModel = (CustomeModel) intent.getSerializableExtra("customeModel");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showActivity() {
        String version = Utils.getVersionCode(this);
        if (Integer.parseInt(version) != versionCode) {
            //如果是第一次进入
            Intent intent1 = new Intent(this, GuideActivity.class);
            startActivity(intent1);
        } else {
            Intent intent = new Intent();
            if(customeModel != null){
                intent.putExtra("customeModel",customeModel);
            }
            MainActivity.start(WelcomeActivity.this, intent);
//            Intent intent = new Intent(this, Text2VoiceActivity.class);
//            startActivity(intent);
        }
        finish();
    }

    /**
     * 权限申请
     */
    private void requestBasicPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(android.Manifest.permission.CAMERA);
            if ((checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(android.Manifest.permission.READ_PHONE_STATE);
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                permissionsList.add(android.Manifest.permission.RECORD_AUDIO);
            }
            if (permissionsList.size() == 0) {
                showActivity();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            showActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults != null && grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == -1) {
                            Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    showActivity();
                } else {
                    showActivity();
                }
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    showActivity();
//                } else {
//                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
//                    finish();
//                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
