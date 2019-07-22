package com.hanwin.product.common.dagger;

import android.app.Application;
import android.content.res.Resources;

import com.hanwin.product.common.BaseApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhaopf on 2018/6/20.
 */

@Module
public class BaseApplicationModule {


    private final BaseApplication bApp;



    public BaseApplicationModule(BaseApplication bApp) {
        this.bApp = bApp;
    }
    @Provides
    @Singleton
    protected Application provideApplication(){
        return bApp;
    }


    @Provides
    @Singleton
    protected Resources provideResources() {
        return bApp.getResources();
    }
}
