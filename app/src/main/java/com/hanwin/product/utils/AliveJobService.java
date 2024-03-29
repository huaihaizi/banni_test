package com.hanwin.product.utils;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.hanwin.product.WelcomeActivity;

/**
 * Created by zpf on 2019/5/24.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AliveJobService extends JobService {
    private final static String TAG = "KeepAliveService";
    // 告知编译器，这个变量不能被优化 
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑 
            if (SystemUtils.isAPPALive(getApplicationContext(), getPackageName())) {
//                Toast.makeText(getApplicationContext(), "APP活着的", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "APP被杀死，重启...", Toast.LENGTH_SHORT).show();
            }
            // 通知系统任务执行结束 
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "KeepAliveService----->JobService服务被启动...");
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕； 
        // 返回true，系统假定这个任务正要被执行 
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        Log.d(TAG, "KeepAliveService----->JobService服务被关闭");
        return false;
    }
}
