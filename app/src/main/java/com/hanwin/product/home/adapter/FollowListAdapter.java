package com.hanwin.product.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.viewutils.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2017/12/14.
 */

public class FollowListAdapter extends RecyclerView.Adapter<FollowListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    public List<CounselorBean> counselorBeanList = new ArrayList<>();
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;

    public FollowListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_follow_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (counselorBeanList != null && counselorBeanList.size() > 0) {
            CounselorBean counselorBean = counselorBeanList.get(position);
            if(counselorBean != null){
                holder.text_name.setText(counselorBean.getName());
                if("1".equals(counselorBean.getGender())){
                    holder.text_sex.setText("男");
                }else if("2".equals(counselorBean.getGender())){
                    holder.text_sex.setText("女");
                }
                holder.text_position.setText("上海");
                holder.text_time.setText(counselorBean.getWorkingYears()+"年翻译年限");
                if("online".equals(counselorBean.getOnlineStatus())){
                    holder.text_call.setText("立即呼叫");
                    holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.green_corner_bg));
                }else  if("offline".equals(counselorBean.getOnlineStatus()) || "busy".equals(counselorBean.getOnlineStatus())){
                    holder.text_call.setText("忙线中");
                    holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.gray_corner_bg));
                }
                String url = counselorBean.getAvatar();
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.image_head_man);
                Glide.with(context).load(Contants.BASE_IMAGE + url)
                        .apply(options)
                        .into(holder.image_head);
            }
        }


        holder.text_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onCallClick(holder.text_call,position);
            }
        });

        holder.check_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return counselorBeanList.size();
//        return 6;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onCallClick(View view, int position);
        void onFollowClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_name;
        private TextView text_sex;
        private TextView text_position;
        private TextView text_time;
        private TextView text_call;
        private CircleImageView image_head;
        private CheckBox check_follow;
        public ViewHolder(View view) {
            super(view);
            image_head = (CircleImageView) view.findViewById(R.id.image_head);
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_sex = (TextView) view.findViewById(R.id.text_sex);
            text_position = (TextView) view.findViewById(R.id.text_position);
            text_time = (TextView) view.findViewById(R.id.text_time);
            text_call = (TextView) view.findViewById(R.id.text_call);
            check_follow = (CheckBox) view.findViewById(R.id.check_follow);
        }
    }

}
