package com.hanwin.product.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.WebViewActivity;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.home.activity.ActivitiesWebActivity;
import com.hanwin.product.home.activity.CounselorNewDetailActivity;
import com.hanwin.product.home.activity.NormalUserListActivity;
import com.hanwin.product.home.bean.CounselorBean;
import com.hanwin.product.home.bean.SignDialectBean;
import com.hanwin.product.home.bean.TransFieldBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.Utils;
import com.hanwin.product.viewutils.AutoPollRecyclerView;
import com.hanwin.product.viewutils.CircleImageView;
import com.hanwin.product.viewutils.GridViewInScrollView;
import com.hanwin.product.viewutils.StarBarView;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhaopf on 2017/12/14.
 */

public class CounselorListAdapter extends RecyclerView.Adapter<CounselorListAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    public List<CounselorBean> counselorBeanList = new ArrayList<>();
    public List<CounselorBean> bannerList = new ArrayList<>();
    public String transDirect = "0";
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;

    public CounselorListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_counselor_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (counselorBeanList != null && counselorBeanList.size() > 0) {
            CounselorBean counselorBean = counselorBeanList.get(position);
            if (position == 2 && transDirect.equals("0") && bannerList != null && bannerList.size() > 0) {
                //展示banner 在列表的第三个item 只有全部领域展示
                holder.rel_lay.setVisibility(View.GONE);
                holder.recycler_new_counselor.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.recycler_new_counselor.getLayoutParams());
                int width = Utils.getScreenWidth(context);
                float scalaHeight = (float) 230/690 * width;
                lp.height = (int)scalaHeight;
                holder.recycler_new_counselor.setLayoutParams(lp);
                holder.recycler_new_counselor.setAnimationDurtion(500);
                //设置适配器
                holder.recycler_new_counselor.setAdapter(new TestNormalAdapter( holder.recycler_new_counselor,bannerList,context));
                if (null != bannerList && bannerList.size() > 1) {
                    holder.recycler_new_counselor.setHintView(new ColorPointHintView(BaseApplication.getInstance(), context.getResources().getColor(R.color.color_ffc626), context.getResources().getColor(R.color.gray_bg)));
                } else {
                    holder.recycler_new_counselor.setHintView(null);
                }
                holder.recycler_new_counselor.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        CounselorBean counselorBean = bannerList.get(position);
                        if (counselorBean != null) {
                            if("0".equals(counselorBean.getImg_url_type())){//手语师
                                Intent intent = new Intent(context, CounselorNewDetailActivity.class);
                                intent.putExtra("userName", counselorBean.getUser_name());
                                context.startActivity(intent);
                            }else{
                                //活动界面
                                ActivitiesWebActivity.startActivity(context,counselorBean.getLink_url(),counselorBean.getTitle(),"1");
                            }
                        }
                    }
                });
            } else {
                if (counselorBean != null) {
                    holder.rel_lay.setVisibility(View.VISIBLE);
                    holder.recycler_new_counselor.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(counselorBean.getMultiAverage())) {
                        holder.starBarView.setStarMark(Float.parseFloat(counselorBean.getMultiAverage()));
                    }
                    holder.text_grade.setText(counselorBean.getMultiAverage() + "分");
                    holder.text_name.setText(counselorBean.getName());
                    if (TextUtils.isEmpty(counselorBean.getSuccessCase())) {
                        holder.text_person_info.setText("翻译经历：暂无经历");
                    } else {
                        holder.text_person_info.setText("翻译经历：" + counselorBean.getSuccessCase());
                    }

                    TransFieldBean transFieldBean = counselorBean.getTransField();
                    if (transFieldBean != null) {
                        holder.view_line.setVisibility(View.VISIBLE);
                        String transDirectStr = transFieldBean.getTransDirect();
                        if (!TextUtils.isEmpty(transDirectStr)) {
                            GridViewAdapter gridViewAdapter;
                            String[] transDirectList = transDirectStr.split(",");
                            String[] newList;//只显示一行
                            if (transDirectList.length > 3) {
                                newList = new String[]{transDirectList[0], transDirectList[1], transDirectList[2]};
                                gridViewAdapter = new GridViewAdapter(context, newList);
                            } else {
                                gridViewAdapter = new GridViewAdapter(context, transDirectList);
                            }
                            //recyclerview嵌套GridView
//                      1、在GirdView的所在布局的根布局中设置改属性： android:descendantFocusability="blacksDescendants"
//                      2、动态设置GirdView的如下属性：
//                      gridview.setClickable(false);
//                      gridview.setPressed(false);
//                      gridview.setEnabled(false); 这样设置之后 在recycleview的OnItemClickListener（）中获得点击事件的响应了
                            holder.gridView.setClickable(false);
                            holder.gridView.setPressed(false);
                            holder.gridView.setEnabled(false);
                            holder.gridView.setAdapter(gridViewAdapter);
                        }
                    }

                    SignDialectBean signDialectBean = counselorBean.getSignDialect();
                    StringBuffer sb = new StringBuffer();
                    sb.append("手语方言特长：");
                    if (signDialectBean != null) {
                        String dialectStr = signDialectBean.getDialect();
                        String dialectDesc = signDialectBean.getDialectDesc();
                        if (!TextUtils.isEmpty(dialectStr)) {
                            String[] dialectList = dialectStr.split(",");
                            if (dialectList != null && dialectList.length == 2) {
                                if ("1".equals(dialectList[1])) {
                                    if (!TextUtils.isEmpty(dialectDesc)) {
                                        String des = dialectDesc.replace(",", "  |  ");
                                        sb.append(des + "");
                                    }
                                }
                                if ("0".equals(dialectList[0])) {
//                                    sb.append("通用手语");
                                }
                            } else if (dialectList != null && dialectList.length == 1) {
                                if ("0".equals(dialectList[0])) {
//                                    sb.append("通用手语");
                                } else if ("1".equals(dialectList[0])) {
                                    if (!TextUtils.isEmpty(dialectDesc)) {
                                        String des = dialectDesc.replace(",", "  |  ");
                                        sb.append(des);
                                    }
                                }
                            }
                        }
                        holder.text_sign_language.setText(sb);
                    }

//                String transDirectStr = "";
//                if ("1".equals(counselorBean.getTransDirect())) {
//                    transDirectStr = "日常生活";
//                } else if ("2".equals(counselorBean.getTransDirect())) {
//                    transDirectStr = "医疗问诊";
//                } else if ("3".equals(counselorBean.getTransDirect())) {
//                    transDirectStr = "银行金融";
//                } else if ("4".equals(counselorBean.getTransDirect())) {
//                    transDirectStr = "政府事务";
//                } else if ("5".equals(counselorBean.getTransDirect())) {
//                    transDirectStr = "其他";
//                }
//                holder.text_translation_orientation.setText("翻译方向：" + transDirectStr);

                    if ("online".equals(counselorBean.getOnlineStatus())) {
                        holder.text_call.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                        holder.text_call.setText("立即呼叫");
                        holder.text_call.setTextColor(context.getResources().getColor(R.color.gray3));
                        holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.call_orange_background));
                        if (!TextUtils.isEmpty(counselorBean.getIsHide())) {
                            if ("1".equals(counselorBean.getIsHide())) {//如果客户被该咨询师屏蔽，则显示忙线中
                                holder.text_call.setText("忙线中");
                                holder.text_call.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规
                                holder.text_call.setTextColor(context.getResources().getColor(R.color.color_8f8f8f));
                                holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.call_gray_background));
                            }
                        }
                    } else if ("busy".equals(counselorBean.getOnlineStatus())) {
                        holder.text_call.setText("忙线中");
                        holder.text_call.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规
                        holder.text_call.setTextColor(context.getResources().getColor(R.color.navigation_bar_bg));
                        holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.call_busy_background));
                    } else if ("offline".equals(counselorBean.getOnlineStatus())) {
                        holder.text_call.setText("已离线");
                        holder.text_call.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常规
                        holder.text_call.setTextColor(context.getResources().getColor(R.color.color_8f8f8f));
                        holder.text_call.setBackground(context.getResources().getDrawable(R.drawable.call_gray_background));
                    }

                    String url = counselorBean.getAvatar();
                    if ("1".equals(counselorBean.getGender())) {
                        holder.image_head.setBorderColor(context.getResources().getColor(R.color.color_f0eff5));
                        holder.image_head.setBorderWidth(2);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.image_head_man);
                        Glide.with(context).load(Contants.BASE_IMAGE + url)
                                .apply(options)
                                .into(holder.image_head);
                    } else if ("0".equals(counselorBean.getGender())) {
                        holder.image_head.setBorderColor(context.getResources().getColor(R.color.color_f0eff5));
                        holder.image_head.setBorderWidth(2);
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.drawable.image_head_woman);
                        Glide.with(context).load(Contants.BASE_IMAGE + url)
                                .apply(options)
                                .into(holder.image_head);
                    }
                }
            }
        }


        holder.text_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onCallClick(holder.text_call, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(holder.itemView, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return counselorBeanList.size();
//        return 6;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onCallClick(View view, int position);

        void onFollowClick(View view, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_name;
        private TextView text_translation_orientation;
        private TextView text_call;
        private CircleImageView image_head;
        private CheckBox check_follow;
        private TextView text_line;
        private TextView text_person_info;
        private StarBarView starBarView;
        private TextView text_grade;
        private GridViewInScrollView gridView;
        private TextView text_sign_language;
        private View view_line;
        private RelativeLayout rel_lay;
        private RollPagerView recycler_new_counselor;
        public ViewHolder(View view) {
            super(view);
            image_head = (CircleImageView) view.findViewById(R.id.image_head);
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_translation_orientation = (TextView) view.findViewById(R.id.text_translation_orientation);
            text_call = (TextView) view.findViewById(R.id.text_call);
            text_line = (TextView) view.findViewById(R.id.text_line);
            text_person_info = (TextView) view.findViewById(R.id.text_person_info);
            text_sign_language = (TextView) view.findViewById(R.id.text_sign_language);
            check_follow = (CheckBox) view.findViewById(R.id.check_follow);
            starBarView = (StarBarView) view.findViewById(R.id.starBar);
            text_grade = view.findViewById(R.id.text_grade);
            gridView = view.findViewById(R.id.grid_view);
            view_line = view.findViewById(R.id.view_line);
            rel_lay = view.findViewById(R.id.rel_lay);
            recycler_new_counselor = view.findViewById(R.id.recycler_new_counselor);
        }
    }

}
