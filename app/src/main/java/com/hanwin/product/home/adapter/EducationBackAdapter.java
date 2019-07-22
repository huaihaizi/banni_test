package com.hanwin.product.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.home.bean.EducationBackBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2018/8/17.
 */

public class EducationBackAdapter extends RecyclerView.Adapter<EducationBackAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private OnItemClickLitener mOnItemClickLitener;
    public List<EducationBackBean> list = new ArrayList<>();
    public Context context;
    public boolean isEdit = false;
    public boolean isSelected = false;

    public EducationBackAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void isSelected(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_time;
        TextView text_school_major;

        public ViewHolder(View view) {
            super(view);
            text_time = (TextView) view.findViewById(R.id.text_time);
            text_school_major = (TextView) view.findViewById(R.id.text_school_major);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(int position);

        void isChecked(boolean isChecked, int position);
    }


    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = mInflater.inflate(R.layout.item_educationback_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (list != null && list.size() > 0) {
            EducationBackBean educationBackBean = list.get(position);
            if (educationBackBean != null) {
                holder.text_time.setText(educationBackBean.getStartDate() + " - " + educationBackBean.getEndDate());
                holder.text_school_major.setText(educationBackBean.getSchool() + " | " + educationBackBean.getDegree());
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (list != null && list.size() > 0) {
            count = list.size();
        } else {
            count = 0;
        }
        return count;
    }
}

