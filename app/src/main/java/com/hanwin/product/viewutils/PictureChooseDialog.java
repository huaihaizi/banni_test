package com.hanwin.product.viewutils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hanwin.product.R;

/**
 * Created by zhaopf on 2017/8/9.
 */

public class PictureChooseDialog extends Dialog {
    private View view;
    private Context context;
    public TextView text_take;
    public TextView text_choose;
    private Button btn_dismiss;
    private  OnItemClick onItemClick;

    public PictureChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view= inflater.inflate(R.layout.dialog_picture_choose,null);
        this.context = context;
        text_take =(TextView)view.findViewById(R.id.text_take);

        text_choose=(TextView)view.findViewById(R.id.text_choose);
        btn_dismiss=(Button)view.findViewById(R.id.btn_dismiss);

        text_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.takeOnClick();
                dismiss();

            }
        });

        text_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.chooseOnClick();
                dismiss();
            }
        });

        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setView();
    }

    public interface OnItemClick{
        void takeOnClick();
        void chooseOnClick();
    }

    public void   setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }
    private void setView(){

        view.setAlpha(1.0f);
        this.setContentView(view);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int)context.getResources().getDisplayMetrics().widthPixels; // 宽度
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
