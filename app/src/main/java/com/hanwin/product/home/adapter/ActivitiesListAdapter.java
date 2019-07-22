package com.hanwin.product.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.home.activity.CounselorNewDetailActivity;
import com.hanwin.product.home.bean.ActivitiesBean;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.home.bean.SignDialectBean;
import com.hanwin.product.home.bean.TransFieldBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.GridViewInScrollView;
import com.hanwin.product.viewutils.StarBarView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import org.raphets.roundimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2017/12/14.
 */

public class ActivitiesListAdapter extends RecyclerView.Adapter<ActivitiesListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    public List<ActivitiesBean> activitiesBeanList = new ArrayList<>();

    public ActivitiesListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_activities_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String startTIme = "";
        String endTIme = "";
        if (activitiesBeanList != null && activitiesBeanList.size() > 0) {
            ActivitiesBean activitiesBean = activitiesBeanList.get(position);
            if (activitiesBean != null) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.activities_image.getLayoutParams());
                int width = Utils.getScreenWidth(context);
                float scalaHeight = (float) 230 / 690 * width;
                lp.height = (int) scalaHeight;
                lp.width = (int) (width - context.getResources().getDimension(R.dimen.ui_60));
                holder.activities_image.setLayoutParams(lp);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.banner_icon);
                Glide.with(context).load(Contants.BASE_IMAGE + activitiesBean.getImgUrl())
                        .apply(options)
                        .into(holder.activities_image);
                if (!TextUtils.isEmpty(activitiesBean.getActiveStartTime())) {
                    startTIme = activitiesBean.getActiveStartTime();
                }
                if (!TextUtils.isEmpty(activitiesBean.getActiveEndTime())) {
                    endTIme = activitiesBean.getActiveEndTime();
                }
                holder.text_time.setText(startTIme + "  -  " + endTIme);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activitiesBeanList.size();
//        return 6;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RoundImageView activities_image;
        private TextView text_time;

        public ViewHolder(View view) {
            super(view);
            activities_image = view.findViewById(R.id.activities_image);
            text_time = view.findViewById(R.id.text_start_end_time);
        }
    }

}
