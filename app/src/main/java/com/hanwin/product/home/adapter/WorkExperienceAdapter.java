package com.hanwin.product.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hanwin.product.R;
import com.hanwin.product.home.bean.WorkExperienceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2018/8/17.
 */

public class WorkExperienceAdapter extends RecyclerView.Adapter<WorkExperienceAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private OnItemClickLitener mOnItemClickLitener;
    public List<WorkExperienceBean> list = new ArrayList<>();
    public Context context;
    public boolean isEdit = false;
    public boolean isSelected = false;

    public WorkExperienceAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void isSelected(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_time;
        CheckBox checkbox_selected;
        TextView text_company_name;
        TextView text_detail;
        public ViewHolder(View view) {
            super(view);
            text_time = (TextView) view.findViewById(R.id.text_time);
            text_company_name = (TextView) view.findViewById(R.id.text_company_name);
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
        view = mInflater.inflate(R.layout.item_workexperience_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (list != null && list.size() > 0) {
            WorkExperienceBean workExperienceBean = list.get(position);
            if (workExperienceBean != null) {
                holder.text_time.setText(workExperienceBean.getStartDate() + " - " + workExperienceBean.getEndDate());
                holder.text_company_name.setText(workExperienceBean.getPosition() + " , " + workExperienceBean.getCompany());
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

