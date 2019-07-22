package com.hanwin.product.common.dagger;


import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseFragment;
import com.hanwin.product.common.BasePresenter;

/**
 * Dagger2的图接口
 * <p/>
 * Created by stefan on 16/11/2.
 */
public interface Graphi {

    void inject(BaseActivity mainActivity); // 注入BaseActivity

    void inject(BaseFragment baseFragment); // 注入BaseFragment

//    void inject(Pager pager); // 注入Pager

    void inject(BasePresenter pager); // 注入BasePresenter
}