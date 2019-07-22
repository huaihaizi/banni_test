package com.hanwin.product.viewutils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hanwin.product.R;

import java.util.List;

/**
 * Created by zhaopf on 2017/9/8 0008.
 */

public class SelectStringAdapter extends RecyclerView.Adapter<SelectStringAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    public List<SelectStringBean> selectStrinlist;
    private Context context;
    private SelectStringBean selectStringBean;
    public SelectStringAdapter(Context context, List<SelectStringBean> selectStrinlist){
        mInflater = LayoutInflater.from(context);
        this.selectStrinlist = selectStrinlist;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_select, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(selectStrinlist != null && selectStrinlist.size() > 0){
            selectStringBean = selectStrinlist.get(position);
            holder.text_canclereason.setText(selectStringBean.getContent());
            if(!selectStringBean.getSelect()){
                holder.text_canclereason.setTextColor(context.getResources().getColor(R.color.gray3));
            }else{
                holder.text_canclereason.setTextColor(context.getResources().getColor(R.color.color_FFAC1F));
            }
        }

        holder.text_canclereason.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(selectStrinlist!=null && selectStrinlist.size() >0){
                    for(int i=0;i<selectStrinlist.size();i++){
                        if (i == position) {
                            selectStrinlist.get(i).setSelect(true);
                        }else{
                            selectStrinlist.get(i).setSelect(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectStrinlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text_canclereason;
        public ViewHolder(View view) {
            super(view);
            text_canclereason = (TextView) view.findViewById(R.id.text_canclereason);
        }
    }
}
