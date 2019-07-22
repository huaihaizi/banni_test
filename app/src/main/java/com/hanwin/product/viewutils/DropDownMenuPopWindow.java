package com.hanwin.product.viewutils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hanwin.product.R;

import java.util.Arrays;

/**
 * Created by zhaopf on 2018/10/16 0016.
 */

public class DropDownMenuPopWindow extends PopupWindow {
    private View view;
    private Context context;
    private MyListViewformesure list_gropdown;
    private LinearLayout lin_list;
    private String first[] = {};
    private GirdDropDownAdapter firstAdapter;
    private int bottom;
    private String str;
    private OnItemClick onItemClick;

    public DropDownMenuPopWindow(Context context,int bottom,String str){
        this.context = context;
        this.bottom = bottom;
        this.str = str;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view= inflater.inflate(R.layout.mydropdownmenu_listdialog,null);
        initView();
        initData();
        setView();

    }

    private void initView(){
        lin_list =(LinearLayout)view.findViewById(R.id.lin_list);
        list_gropdown=(MyListViewformesure)view.findViewById(R.id.list_gropdown);
    }

    private void initData() {
        if(str.equals("1")){
            first = new String[]{"全部领域","日常生活","医疗问诊","银行金融","政府事务","其他领域"};
        } else {
            first = new String[]{"全部状态","空闲状态","离线状态"};
        }
        firstAdapter = new GirdDropDownAdapter(context, Arrays.asList(first),str);
        list_gropdown.setDividerHeight(0);
        list_gropdown.setAdapter(firstAdapter);
        list_gropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                firstAdapter.setCheckItem(position,str);
                onItemClick.onItemClick(first[position],position);
            }
        });
    }

    public interface OnItemClick{
        void onItemClick(String info,int position);
        void onItemDismiss();
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    private void setView(){
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight((int)context.getResources().getDisplayMetrics().heightPixels-bottom);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimTop);
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) context,0.5f);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.setClippingEnabled(false);
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = list_gropdown.getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        onItemClick.onItemDismiss();
                    }
                }
                return true;
            }
        });

    }

    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
