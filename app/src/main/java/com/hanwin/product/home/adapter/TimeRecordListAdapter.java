package com.hanwin.product.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.home.bean.ChatRecordTimeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示时间记录的adapter
 * Created by zhaopf on 2018/10/17 0017.
 */

public class TimeRecordListAdapter extends RecyclerView.Adapter<TimeRecordListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    public List<ChatRecordTimeBean> timeRecordBeanList = new ArrayList<>();
    private Context context;
    public TimeRecordListAdapter(Context context,List<ChatRecordTimeBean> timeRecordBeanList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.timeRecordBeanList = timeRecordBeanList;
    }

    @Override
    public TimeRecordListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_time_record_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        if(timeRecordBeanList != null && timeRecordBeanList.size() > 0){
            ChatRecordTimeBean chatRecordTimeBean = timeRecordBeanList.get(position);
            if(chatRecordTimeBean != null){
               holder.text_time1.setText(chatRecordTimeBean.getStartVideoTime());
               if(!TextUtils.isEmpty(chatRecordTimeBean.getConsultation())){
                   int time = Integer.parseInt(chatRecordTimeBean.getConsultation());
                   if(time >= 60){
                       if(time % 60 == 0){
                           holder.text_time3.setText(time/60 + "分钟");
                       }else {
                           holder.text_time3.setText((time/60 + 1) + "分钟");
                       }
                   }else{
                       holder.text_time3.setText(chatRecordTimeBean.getConsultation() + "秒");
                   }
               }
            }
        }
    }

    @Override
    public int getItemCount() {
        return timeRecordBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_time1;
        private TextView text_time2;
        private TextView text_time3;
        public ViewHolder(View view) {
            super(view);
            text_time1 = (TextView) view.findViewById(R.id.text_time1);
            text_time2 = (TextView) view.findViewById(R.id.text_time2);
            text_time3 = (TextView) view.findViewById(R.id.text_time3);
        }
    }
}
