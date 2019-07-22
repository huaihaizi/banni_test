package com.hanwin.product.viewutils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.hanwin.product.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaopf on 2018/6/22 0022.
 */

public class SelectItemDialog extends Dialog {
    private View view;
    private Button btn_sure;
    private RecyclerView recyclerview;
    private Context context;
    private List<SelectStringBean> list;
    private SelectStringAdapter selectStringAdapter;
    private BtnOnClick btnOnClick;
    private String type;
    private  String[] reasonStr;
    public SelectItemDialog(@NonNull Context context, @StyleRes int themeResId,String type) {
        super(context,themeResId);
        this.context = context;
        this.type = type;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_layout,null);
        recyclerview = (RecyclerView)view.findViewById(R.id.recycler_canclereason);
        btn_sure=(Button)view.findViewById(R.id.btn_sure);
        initData();
        setView();
    }

    private void initData() {
        list = new ArrayList<>();
        if("1".equals(type)){//学历
            reasonStr = new String[]{"未知","小学","初中","高中","技工学校","中专", "大专","本科", "硕士","博士"};
        }else if("2".equals(type)){//居住时长
            reasonStr = new String[]{"半年内","半年到一年","一年到三年","三年以上"};
        }else if("3".equals(type)){//婚姻状态
            reasonStr = new String[]{"未婚","已婚","离婚","丧偶"};
        }else if("4".equals(type)){//直系关系
            reasonStr = new String[]{"其他","夫妻","父母","子女","兄弟","姐妹","朋友"};
        }
        for(int i =0;i<reasonStr.length;i++){
            SelectStringBean selectStringBean = new SelectStringBean();
            selectStringBean.setContent(reasonStr[i]);
            selectStringBean.setSelect(false);
            selectStringBean.setId(i);
            list.add(i,selectStringBean);
        }
        selectStringAdapter = new SelectStringAdapter(context,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(selectStringAdapter);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOnClick.btnOnClick(selectStringAdapter.selectStrinlist,type);
                dismiss();
            }
        });
    }

    public interface BtnOnClick{
        void btnOnClick(List<SelectStringBean> info,String type);
    }

    public void setBtnOnClice(BtnOnClick btnOnClick){
        this.btnOnClick  = btnOnClick;

    }

    private void setView() {
        view.setAlpha(1.0f);
        this.setContentView(view);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
        dialogWindow.setAttributes(lp);
        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
