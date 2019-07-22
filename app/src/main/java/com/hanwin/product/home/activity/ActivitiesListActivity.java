package com.hanwin.product.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.home.adapter.ActivitiesListAdapter;
import com.hanwin.product.home.bean.ActivitiesBean;
import com.hanwin.product.home.bean.ActivitiesMsgBean;
import com.hanwin.product.utils.Contants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * 活动列表
 * Created by zhaopf on 2019/6/12.
 */

public class ActivitiesListActivity extends BaseActivity implements ActivitiesListAdapter.OnItemClickLitener {
    @Bind(R.id.text_title)
    TextView text_title;

    @Bind(R.id.recycler_activites)
    RecyclerView recycler_activites;

    LinearLayoutManager linearLayoutManager;
    ActivitiesListAdapter activitiesListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        text_title.setText("活动中心");
        linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recycler_activites.setNestedScrollingEnabled(false);
        recycler_activites.setLayoutManager(linearLayoutManager);
        activitiesListAdapter = new ActivitiesListAdapter(this);
        activitiesListAdapter.setOnItemClickLitener(this);
        recycler_activites.setAdapter(activitiesListAdapter);
    }

    private void initData() {
        Map<String, Object> params = new HashMap<>();
        getList(params);
    }

    @OnClick(R.id.imgbtn_back)
    public void back(){
        finish();
    }

    private void getList(Map<String, Object> params) {
        mHttpHelper.post(Contants.BASE_URL + "sign_technology/queryBannerList", params, new SpotsCallBack<ActivitiesMsgBean>(this) {
            @Override
            public void onSuccess(Response response, ActivitiesMsgBean activitiesMsgBean) {
                if (activitiesMsgBean != null) {
                    List<ActivitiesBean> activitiesBeanList = activitiesMsgBean.getData();
                    activitiesListAdapter.activitiesBeanList = activitiesBeanList;
                    activitiesListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        ActivitiesBean activitiesBean = activitiesListAdapter.activitiesBeanList.get(position);
        String url = "";
        if (activitiesBean != null) {
            if(!TextUtils.isEmpty(activitiesBean.getLinkUrl())){
                url = activitiesBean.getLinkUrl();
            }
            ActivitiesWebActivity.startActivity(this,url,activitiesBean.getTitle(),"1");
        }
    }
}
