package com.hanwin.product.viewutils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hanwin.product.R;

/**
 * Created by zhaopf on 2018/8/2.
 */

public class EvaluateHeartLin extends LinearLayout implements View.OnClickListener {


    private View view;
    private Context context;
    private LinearLayout lin_heart;
    private ImageView checkBox1;
    private ImageView checkBox2;
    private ImageView checkBox3;
    private ImageView checkBox4;
    private ImageView checkBox5;
    private OnClick onClick;

    public EvaluateHeartLin(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.evaluate_heart, this, true);
        this.context = context;
        lin_heart = (LinearLayout) findViewById(R.id.lin_heart);
        checkBox1 = (ImageView) findViewById(R.id.img_heart1);
        checkBox1.setTag(1);
        checkBox2 = (ImageView) findViewById(R.id.img_heart2);
        checkBox2.setTag(2);
        checkBox3 = (ImageView) findViewById(R.id.img_heart3);
        checkBox3.setTag(3);
        checkBox4 = (ImageView) findViewById(R.id.img_heart4);
        checkBox4.setTag(4);
        checkBox5 = (ImageView) findViewById(R.id.img_heart5);
        checkBox5.setTag(5);
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox4.setOnClickListener(this);
        checkBox5.setOnClickListener(this);
        setCheckBox(5);
    }


    private void setCheckBox(int position) {
        for (int i = 0; i < 5; i++) {
            ImageView imageView = (ImageView) lin_heart.getChildAt(i);
            if (i < position) {
                imageView.setImageResource(R.drawable.heart_checked);
            } else {
                imageView.setImageResource(R.drawable.heart_normal);
            }
        }

    }

    @Override
    public void onClick(View view) {
        setCheckBox((Integer) view.getTag());
        onClick.onItemClice((Integer) view.getTag());
    }

    public interface OnClick{
        void onItemClice(int heart);
    }

    public void setOnClick(OnClick onClick){
        this.onClick = onClick;
    }
}
