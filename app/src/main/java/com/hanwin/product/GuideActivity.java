package com.hanwin.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanwin.product.common.BaseActivity;
import com.hanwin.product.viewutils.DotIndicator;

import java.util.ArrayList;

/**
 * Created by zhaopf on 2018/11/19.
 */

public class GuideActivity extends BaseActivity {
    private ViewPager mViewPager;
    private ArrayList<View> myView;
    private GestureDetector gd;// 用于捕捉touch的详细手势(gesture)
    private DotIndicator dotIndicator;
    private boolean isLastPage = false;
    private boolean isDragPage = false;
    private boolean isIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_show);
        mViewPager = (ViewPager) findViewById(R.id.firstpager);
        myView = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        View v1 = inflater.inflate(R.layout.loading_first_show,
                new LinearLayout(this), false);
        View v2 = inflater.inflate(R.layout.loading_second_show, new LinearLayout(
                this), false);
        View v3 = inflater.inflate(R.layout.loading_third_show,
                new RelativeLayout(this), false);
//        View v4 = inflater.inflate(R.loading_first_show.lording_showfour,
//                new LinearLayout(this), false);
//        View v5 = inflater.inflate(R.loading_first_show.lording_showfive,
//                new LinearLayout(this), false);
        myView.add(v1);
        myView.add(v2);
        myView.add(v3);
//        myView.add(v4);
//        myView.add(v5);
        TextView button = (TextView) v3.findViewById(R.id.image_into);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = GuideActivity.this.getSharedPreferences("VERSION_CODE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("versionCode", BuildConfig.VERSION_CODE);
                editor.commit();
                Intent myIntent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        mViewPager.setAdapter(new GuidePageAdapter());

        initView();
    }

    private void initView() {
        dotIndicator = (DotIndicator) findViewById(R.id.dot_indicator);
        dotIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isLastPage && isDragPage && positionOffsetPixels == 0 && position == 2 && !isIn) {
                    isIn = true;//防止二次进入判断
//                    startActivity(new Intent(GuideActivity.this, NormalUserListActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                    SharedPreferences pref = GuideActivity.this.getSharedPreferences("ISFIRSTIN", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putBoolean("isFirstIn", false);
//                    editor.commit();
//                    finish();
                }
            }

            @Override
            public void onPageSelected(int position) {
                isLastPage = position == myView.size() - 1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isDragPage = state == 1;
            }
        });
    }

    private class GuidePageAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(myView.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            return myView.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(myView.get(arg1));
            return myView.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

    }
}
