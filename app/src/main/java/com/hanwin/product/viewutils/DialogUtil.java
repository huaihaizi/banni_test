package com.hanwin.product.viewutils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.GuideActivity;
import com.hanwin.product.R;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.TimeUtils;
import com.hanwin.product.utils.Utils;


/**
 * Created by zhoux on 2017/8/7.
 */

public class DialogUtil {

    private OnClick onClick;
    public Dialog dialog;
    public Dialog dialogLoading;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void leftClick();

        void rightClick();
    }

    public void infoDialog(Context context, String title, String info, boolean leftIsShow, boolean rightIsShow) {
        dialog = new Dialog(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dv = inflater.inflate(R.layout.layout_infomind, null);
        TextView text_title = (TextView) dv.findViewById(R.id.text_title);
        TextView text_info = (TextView) dv.findViewById(R.id.text_info);
        TextView text_sure = (TextView) dv.findViewById(R.id.text_sure);
        TextView text_dismiss = (TextView) dv.findViewById(R.id.text_dismiss);
        text_title.setText(title);
        text_info.setText(info);
        if (leftIsShow) {
            text_sure.setVisibility(View.VISIBLE);
        } else {
            text_sure.setVisibility(View.GONE);
        }
        if (rightIsShow) {
            text_dismiss.setVisibility(View.VISIBLE);
        } else {
            text_dismiss.setVisibility(View.GONE);
        }
        text_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.rightClick();
            }
        });
        text_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });
        dialog.setContentView(dv);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    public void showDialog(Context context, String title, String content) {
        dialog = new Dialog(context, R.style.CustomDialog);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dv = inflater.inflate(R.layout.dialog_info, null);
        TextView text_title = dv.findViewById(R.id.text_title);
        TextView text_info = dv.findViewById(R.id.text_info);
        TextView text_sure = (TextView) dv.findViewById(R.id.text_sure);
        text_title.setText(title);
        text_info.setText(content);
        text_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });
        dialog.setContentView(dv);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    /**
     * 实名认证弹窗
     *
     * @param context
     */
    public void showCertificationDialog(Context context, String info1, String info2, boolean flag) {
        dialog = new Dialog(context, R.style.CustomDialog);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dv = inflater.inflate(R.layout.dialog_certification_info, null);
        ImageView image_close = (ImageView) dv.findViewById(R.id.image_close);
        ImageView image_title = (ImageView) dv.findViewById(R.id.image_title);
        TextView text_title = (TextView) dv.findViewById(R.id.text_title);
        TextView text_info1 = (TextView) dv.findViewById(R.id.text_info1);
        TextView text_info2 = (TextView) dv.findViewById(R.id.text_info2);
        TextView text_cancel = (TextView) dv.findViewById(R.id.text_cancel);
        TextView text_certification = (TextView) dv.findViewById(R.id.text_certification);
        text_info1.setText(info1);
        text_info2.setText(info2);
        if (flag) {
            image_title.setVisibility(View.GONE);
            text_title.setVisibility(View.GONE);
            text_info1.setTextColor(context.getResources().getColor(R.color.gray3));
            text_info2.setTextColor(context.getResources().getColor(R.color.gray3));
        }
        text_certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.rightClick();
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });

        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });

        dialog.setContentView(dv);
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    /**
     * 显示剩余服务次数的弹窗
     * @param context
     * @param times
     * @param flag
     */
    public void showServiceTimesDialog(Context context, String times, boolean flag) {
        dialog = new Dialog(context, R.style.CustomDialog);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dv = inflater.inflate(R.layout.dialog_service_times, null);
        ImageView image_close = (ImageView) dv.findViewById(R.id.image_close);
        ImageView image_title = (ImageView) dv.findViewById(R.id.image_title1);
        TextView text_info1 = (TextView) dv.findViewById(R.id.text_info1);
        TextView text_info2 = (TextView) dv.findViewById(R.id.text_info2);
        View tag_view = dv.findViewById(R.id.tag_view);
        TextView text_cancel = (TextView) dv.findViewById(R.id.text_cancel);
        TextView text_certification = (TextView) dv.findViewById(R.id.text_certification);

        if (flag) {
            text_info1.setText("您尚未完成实名认证");
            String result = "平共可试用平台服务 <font color='#fc8a10'>%s</font> 次";
            result = String.format(result, times);
            text_info2.setText(Html.fromHtml(result));
            image_close.setVisibility(View.INVISIBLE);
            switch (times) {
                case "1":
                    image_title.setImageDrawable(context.getResources().getDrawable(R.drawable.one_times));
                    break;
                case "2":
                    image_title.setImageDrawable(context.getResources().getDrawable(R.drawable.two_times));
                    break;
                case "3":
                    image_title.setImageDrawable(context.getResources().getDrawable(R.drawable.three_times));
                    break;
            }
        } else {
            text_info1.setText("您的试用次数已用完");
            text_info2.setText("完成实名认证可继续使用");
            image_close.setVisibility(View.VISIBLE);
            tag_view.setVisibility(View.GONE);
            text_cancel.setVisibility(View.GONE);
            text_certification.setTextSize(19);
            image_title.setImageDrawable(context.getResources().getDrawable(R.drawable.zero_times));
        }
        text_certification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.rightClick();
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });

        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });

        dialog.setContentView(dv);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    /**
     * 展示活动弹窗
     * @param context
     * @param url
     */
    public void showFloatImage(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences("week", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("week", TimeUtils.getWeek());
        editor.commit();
        dialog = new Dialog(context, R.style.CustomDialog);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dv = inflater.inflate(R.layout.dialog_float_image, null);
        ImageView image_float = dv.findViewById(R.id.image_float);
        ImageView show_image = dv.findViewById(R.id.show_image);
        ImageView image_close = dv.findViewById(R.id.image_close);
        Glide.with(context).load(Contants.BASE_IMAGE + url)
                .into(show_image);
        image_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.leftClick();
            }
        });
        image_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.rightClick();
            }
        });
        dialog.setContentView(dv);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.show();
    }

    /**
     * 显示loading弹窗
     *
     * @param context
     * @param msg
     * @return
     */
    public void showLoadingDialog(Context context, String msg) {
        dialogLoading = new Dialog(context, R.style.CustomDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.rotating);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        dialogLoading.setContentView(v);
        dialogLoading.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialogLoading.show();
    }

    public void dismiss() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
        }
    }

}
