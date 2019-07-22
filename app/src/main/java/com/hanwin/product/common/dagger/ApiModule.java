package com.hanwin.product.common.dagger;


import com.hanwin.product.common.http.OkHttpHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by stefan on 16/12/3.
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    protected OkHttpHelper provideOkHttpHelper() {
        return new OkHttpHelper();
    }

}
