package com.hanwin.product.viewutils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanwin.product.R;

import org.json.JSONArray;

/**
 * Created by zhaopf on 2018/10/16 0016.
 */

public class MyDropDownMenu extends LinearLayout implements View.OnClickListener {
    private Context context;
    private RelativeLayout lin_translation;
    private LinearLayout lin_online_state;
    private LinearLayout lin_top;
    private TextView text_translation;
    private TextView text_online_state;

    private DropDownMenuPopWindow dropDownMenuPopWindow;
    private boolean isDropDownMenu = false;//是否打开下拉弹窗
    private SubMitInfo subMitInfo;
    private String translationType = "0";
    private String onlineStateId = "0";
    public MyDropDownMenu(Context context,@Nullable AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.dropdownmenu_item, this, true);
        lin_translation = findViewById(R.id.lin_translation);
        lin_online_state = findViewById(R.id.lin_online_state);
        lin_top = findViewById(R.id.lin_top);
        text_online_state = findViewById(R.id.text_online_state);
        text_translation = findViewById(R.id.text_translation);

        text_translation.setOnClickListener(this);
        text_online_state.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_translation:
                if(isDropDownMenu){
                    closeDropDownMenu();
                }else{
                    openDropDownMenu();
                }
                break;
            case R.id.text_online_state:
                if(isDropDownMenu){
                    closeDropDownMenu1();
                }else{
                    openDropDownMenu1();
                }
                break;
        }
    }

    private void openDropDownMenu() {
        Drawable drawable = getResources().getDrawable(R.drawable.up_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        text_translation.setCompoundDrawables(null, null, drawable, null);
        int[] location = new int[2];
        lin_top.getLocationInWindow(location);
        int aa = lin_top.getHeight();
        dropDownMenuPopWindow = new DropDownMenuPopWindow(context, location[1] + lin_top.getHeight(),"1");
        dropDownMenuPopWindow.setOnItemClick(new DropDownMenuPopWindow.OnItemClick() {
            @Override
            public void onItemClick(String info, int position) {
                text_translation.setText(info);
                translationType = position+"";
                setInfo();
                isDropDownMenu = !isDropDownMenu;
                closeDropDownMenu();
            }

            @Override
            public void onItemDismiss() {
                closeDropDownMenu();
            }
        });
        isDropDownMenu = !isDropDownMenu;
        dropDownMenuPopWindow.showAsDropDown(lin_top);
    }

    private void closeDropDownMenu() {
        dropDownMenuPopWindow.dismiss();
        Drawable drawable = getResources().getDrawable(R.drawable.down_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        text_translation.setCompoundDrawables(null, null, drawable, null);
        isDropDownMenu = !isDropDownMenu;

    }

    private void openDropDownMenu1() {
        Drawable drawable = getResources().getDrawable(R.drawable.up_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        text_online_state.setCompoundDrawables(null, null, drawable, null);
        int[] location = new int[2];
        lin_top.getLocationInWindow(location);
        int aa = lin_top.getHeight();
        dropDownMenuPopWindow = new DropDownMenuPopWindow(context, location[1] + lin_top.getHeight(),"2");
        dropDownMenuPopWindow.setOnItemClick(new DropDownMenuPopWindow.OnItemClick() {
            @Override
            public void onItemClick(String info, int position) {
                text_online_state.setText(info);
                onlineStateId = position +"";
                setInfo();
                isDropDownMenu = !isDropDownMenu;
                closeDropDownMenu1();
            }

            @Override
            public void onItemDismiss() {
                closeDropDownMenu();
            }
        });
        isDropDownMenu = !isDropDownMenu;
        dropDownMenuPopWindow.showAsDropDown(lin_top);
    }

    private void closeDropDownMenu1() {
        dropDownMenuPopWindow.dismiss();
        Drawable drawable = getResources().getDrawable(R.drawable.down_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        text_online_state.setCompoundDrawables(null, null, drawable, null);
        isDropDownMenu = !isDropDownMenu;
    }


    private void setInfo() {
        subMitInfo.subMitInfo(translationType, onlineStateId);
    }

    public interface SubMitInfo {
        void subMitInfo(String translationType, String onlineStateId);
    }

    public void setSubMitInfo(SubMitInfo subMitInfo) {
        this.subMitInfo = subMitInfo;
    }
}
