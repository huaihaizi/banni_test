package com.hanwin.product.viewutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanwin.product.R;

import java.util.List;


public class GirdDropDownAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int checkItemPosition = 0;
    int isSeletceNum = 0;
    boolean isSeletce = false;
    String flag;

    public void setCheckItem(int position, String flag) {
        checkItemPosition = position;
        isSeletce = true;
        if ("1".equals(flag)) {
            SharedPreferences pref = context.getSharedPreferences("TRANSLATION", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("isSeletceNum", position);
            editor.commit();
        } else {
            SharedPreferences pref = context.getSharedPreferences("ONLINESTATE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("isSeletceNum", position);
            editor.commit();
        }
        notifyDataSetChanged();
    }

    public GirdDropDownAdapter(Context context, List<String> list, String flag) {
        this.context = context;
        this.list = list;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_drop_down, null);
            viewHolder = new ViewHolder();
            viewHolder.mText = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));
        if ("1".equals(flag)) {
            SharedPreferences preferences = context.getSharedPreferences("TRANSLATION", Context.MODE_PRIVATE);
            isSeletceNum = preferences.getInt("isSeletceNum", 0);
        } else {
            SharedPreferences preferences = context.getSharedPreferences("ONLINESTATE", Context.MODE_PRIVATE);
            isSeletceNum = preferences.getInt("isSeletceNum", 0);
        }
        if (isSeletce) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.color_ffc626));
                viewHolder.mText.setCompoundDrawablePadding(10);
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.check_mark), null);
            } else {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.gray3));
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        } else

        {
            if (isSeletceNum == position) {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.color_ffc626));
                viewHolder.mText.setCompoundDrawablePadding(10);
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.check_mark), null);
            } else {
                viewHolder.mText.setTextColor(context.getResources().getColor(R.color.gray3));
                viewHolder.mText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }
    }

    class ViewHolder {

        TextView mText;


    }
}
