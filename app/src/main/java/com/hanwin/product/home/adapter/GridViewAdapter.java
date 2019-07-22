package com.hanwin.product.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanwin.product.R;

/**
 * Created by admin on 2019/1/7.
 */

public class GridViewAdapter extends android.widget.BaseAdapter{
    private Context mcontext;
    private LayoutInflater mInflater;
    private ViewHolder viewHolder;
    private String[] transDirectList;
    public GridViewAdapter(Context context,String[] transDirectList) {
        this.mcontext = context;
        mInflater = LayoutInflater.from(context);
        this.transDirectList = transDirectList;
    }

    @Override
    public int getCount() {
        return transDirectList != null ? transDirectList.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return transDirectList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.layout_grid_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(transDirectList != null && transDirectList.length > 0){
            String type = transDirectList[position];
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
            viewHolder.text_item_name.setText(transDirectStr);
        }
        return convertView;
    }


    class ViewHolder{
        private TextView text_item_name;
        public ViewHolder(View view) {
            text_item_name = (TextView) view.findViewById(R.id.text_item_name);
        }
    }

}

