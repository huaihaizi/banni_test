package com.hanwin.product.utils;

import android.content.Context;
import android.content.Intent;

import java.util.TimerTask;

/**
 * Created by zhaopf on 2018/7/13 0013.
 */

public class RequestTimerTask extends TimerTask{
    private Context mcontext;
    public RequestTimerTask(Context context) {
        this.mcontext = context;
    }

    @Override
    public void run() {
        Intent intent1 = new Intent();
        intent1.setAction("send_heartbeat");
        mcontext.sendBroadcast(intent1);
    }
}
