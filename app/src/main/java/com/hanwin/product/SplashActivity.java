package com.hanwin.product;

import android.app.Activity;
import android.os.Bundle;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }
}
