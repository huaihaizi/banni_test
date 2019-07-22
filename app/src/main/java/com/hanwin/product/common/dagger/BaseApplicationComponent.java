package com.hanwin.product.common.dagger;

import com.hanwin.product.common.BaseApplication;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by stefan on 16/12/3.
 */
@Singleton
@Component(modules = {BaseApplicationModule.class,ApiModule.class})
public interface BaseApplicationComponent extends Graphi{

    final class Initializer {
        private Initializer() {
        } // No instances.

        // 初始化组件
        public static BaseApplicationComponent init(BaseApplication app) {
            return DaggerBaseApplicationComponent.builder().apiModule(new ApiModule()).build();
        }
    }
}
