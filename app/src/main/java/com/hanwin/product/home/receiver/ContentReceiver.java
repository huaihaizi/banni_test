package com.hanwin.product.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hanwin.product.WelcomeActivity;

/**
 * Created by zhaopf on 2018/8/29 0029.
 */

public class ContentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in =new Intent(context, WelcomeActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }
}
