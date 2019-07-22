package com.hanwin.product.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.utils.Contants;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;

import org.raphets.roundimageview.RoundImageView;

import java.util.List;

/**
 * 无限循环的Adapter
 * Created by zhaopf on 2017/7/31.
 */

public class TestNormalAdapter extends StaticPagerAdapter {
    private List<CounselorBean> bannerInfos;
    private Context mcontext;
    public TestNormalAdapter(RollPagerView viewPager,List<CounselorBean> bannerInfos, Context context) {
//        super(viewPager);
        this.bannerInfos = bannerInfos;
        this.mcontext = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        RoundImageView roundImageView = new RoundImageView(container.getContext());
        roundImageView.setType(RoundImageView.TYPE_ROUND);
        roundImageView.setCornerRadius(10);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.banner_icon);
        Glide.with(mcontext).load(Contants.BASE_IMAGE + bannerInfos.get(position).getImg_url())
                .apply(options)
                .into(roundImageView);
        roundImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        roundImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return roundImageView;
    }

//    @Override
//    public int getRealCount() {
//        return bannerInfos.size();
//    }

    @Override
    public int getCount() {
        return bannerInfos.size();
    }
}
