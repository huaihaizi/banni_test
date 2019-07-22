package com.hanwin.product.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.home.activity.CounselorNewDetailActivity;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.StarBarView;

import org.raphets.roundimageview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2017/12/14.
 */

public class CounselorNewListAdapter extends RecyclerView.Adapter<CounselorNewListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    public List<CounselorBean> mbannerList = new ArrayList<>();
    private Context context;
    CounselorBean counselorBean;

    public CounselorNewListAdapter(Context context, List<CounselorBean> bannerList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mbannerList = bannerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_counselor_new_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        position % list.size  得到相应的位置
        if (mbannerList != null && mbannerList.size() > 0) {
            counselorBean = mbannerList.get(position % mbannerList.size());
            if (counselorBean != null) {
//                if (!TextUtils.isEmpty(counselorBean.getMultiAverage())) {
//                    holder.starBarView.setStarMark(Float.parseFloat(counselorBean.getMultiAverage()));
//                }
//                holder.text_name.setText(counselorBean.getName());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.banner_image.getLayoutParams());
                int width = Utils.getScreenWidth(context);
                float scalaWidth = (float) width/750 * 565;
                float scalaHeight = (float) 278/565 * scalaWidth;
                lp.height = (int)scalaHeight;
                lp.width = (int)scalaWidth;;
                holder.banner_image.setLayoutParams(lp);
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.banner_icon);
                Glide.with(context).load(Contants.BASE_IMAGE + counselorBean.getImg_url())
                        .apply(options)
                        .into(holder.banner_image);
//                TransFieldBean transFieldBean = counselorBean.getTransField();
//                if (transFieldBean != null) {
//                    String transDirectStr = transFieldBean.getTransDirect();
//                    String[] transDirectList = transDirectStr.split(",");
//                    if(transDirectList != null && transDirectList.length > 0){
//                        if(transDirectList.length == 1){
//                            holder.text_item_name1.setVisibility(View.VISIBLE);
//                            holder.text_item_name2.setVisibility(View.GONE);
//                            holder.text_item_name1.setText(mateToStr(transDirectList[0]));
//                        }else if(transDirectList.length >= 2){
//                            holder.text_item_name1.setVisibility(View.VISIBLE);
//                            holder.text_item_name2.setVisibility(View.VISIBLE);
//                            holder.text_item_name1.setText(mateToStr(transDirectList[0]));
//                            holder.text_item_name2.setText(mateToStr(transDirectList[1]));
//                        }
//                    }else{
//                        holder.text_item_name1.setVisibility(View.GONE);
//                        holder.text_item_name2.setVisibility(View.GONE);
//                    }
//                }
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counselorBean = mbannerList.get(position % mbannerList.size());
                if (counselorBean != null) {
                    if("0".equals(counselorBean.getImg_url_type())){//手语师
                        Intent intent = new Intent(context, CounselorNewDetailActivity.class);
                        intent.putExtra("userName", counselorBean.getUser_name());
                        context.startActivity(intent);
                    }else{
                        //活动界面
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra("title", counselorBean.getTitle());
                        intent.putExtra("url", counselorBean.getLink_url());
                        context.startActivity(intent);
                    }
                }
            }
        });

    }

    /**
     * 匹配数据
     *
     * @param type
     * @return
     */
    private String mateToStr(String type) {
        String transDirectStr = "";
        if ("1".equals(type)) {
            transDirectStr = "日常生活";
        } else if ("2".equals(type)) {
            transDirectStr = "医疗问诊";
        } else if ("3".equals(type)) {
            transDirectStr = "银行金融";
        } else if ("4".equals(type)) {
            transDirectStr = "政府事务";
        } else if ("5".equals(type)) {
            transDirectStr = "其他领域";
        }
        return transDirectStr;
    }

    @Override
    public int getItemCount() {
//        return counselorBeanList.size();
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image_head;
        private TextView text_name;
        private StarBarView starBarView;
        private TextView text_item_name1;
        private TextView text_item_name2;
        private RoundImageView banner_image;

        public ViewHolder(View view) {
            super(view);
//            image_head = (CircleImageView) view.findViewById(R.id.image_head);
            banner_image = view.findViewById(R.id.banner_image);
//            text_name = (TextView) view.findViewById(R.id.text_name);
//            text_item_name1 = (TextView) view.findViewById(R.id.text_item_name1);
//            text_item_name2 = (TextView) view.findViewById(R.id.text_item_name2);
//            starBarView = (StarBarView) view.findViewById(R.id.starBar);
        }
    }

}
