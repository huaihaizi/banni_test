package com.hanwin.product.home.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hanwin.product.R;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.http.OkHttpHelper;
import com.hanwin.product.common.http.SpotsCallBack;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.ChatRecordBean;
import com.hanwin.product.home.bean.ChatRecordTimeBean;
import com.hanwin.product.utils.Contants;
import com.hanwin.product.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by zhaopf on 2018/10/17 0017.
 */

public class ChatRecordAdapter extends RecyclerView.Adapter<ChatRecordAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    public List<ChatRecordBean> chatRecordBeanList = new ArrayList<>();
    private Context context;
    private OnItemClickLitener mOnItemClickLitener;
    private OkHttpHelper okHttpHelper = new OkHttpHelper();

    public ChatRecordAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ChatRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_chat_record_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (chatRecordBeanList != null && chatRecordBeanList.size() > 0) {
            List<ChatRecordTimeBean> list = new ArrayList<>();
            final ChatRecordBean chatRecordBean = chatRecordBeanList.get(position);
            List<ChatRecordTimeBean> chatRecordTimeBeanList = chatRecordBean.getCreateLenTime();
            if (chatRecordTimeBeanList != null && chatRecordTimeBeanList.size() > 0) {
                //当是查看更多时，将展示整个list，当为false时，只取前三条
                if (chatRecordBean.isSeeMore()) {
                    list.clear();
                    list.addAll(chatRecordTimeBeanList);
                } else {
                    if(chatRecordTimeBeanList != null && chatRecordTimeBeanList.size() > 3){
                        list.addAll(chatRecordTimeBeanList.subList(0, 3));
                    }else{
                        list.addAll(chatRecordTimeBeanList);
                    }
                }
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getInstance()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            holder.recycler_record.setLayoutManager(linearLayoutManager);
            TimeRecordListAdapter timeRecordListAdapter = new TimeRecordListAdapter(context, list);
            holder.recycler_record.setAdapter(timeRecordListAdapter);

            if (chatRecordBean != null) {
                if(!TextUtils.isEmpty(chatRecordBean.getName())){
                    holder.text_name.setText(chatRecordBean.getName());
                }else {
                    if(!TextUtils.isEmpty(chatRecordBean.getNickName())) {
                        holder.text_name.setText(chatRecordBean.getNickName());
                    }
                }

                if ("1".equals(chatRecordBean.getGender())) {
                    holder.text_gender.setText("性别：男");
                } else if ("0".equals(chatRecordBean.getGender())) {
                    holder.text_gender.setText("性别：女");
                }
                String url = chatRecordBean.getAvatar();
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.image_head_man);
                Glide.with(context).load(Contants.BASE_IMAGE + url)
                        .apply(options)
                        .into(holder.image_head);

                if (chatRecordBean.isSeeMore()) {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.up_normal);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.text_more.setCompoundDrawables(null, null, drawable, null);
                } else {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.down_normal);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.text_more.setCompoundDrawables(null, null, drawable, null);
                }
                //根据屏蔽状态展示不同的样式  0:未被屏蔽  1：已经屏蔽
                if ("1".equals(chatRecordBean.getIsHide())) {
                    holder.text_shield.setText("取消屏蔽");
                    holder.text_shield.setTextColor(context.getResources().getColor(R.color.gray));
                    holder.text_shield.setBackground(context.getResources().getDrawable(R.drawable.shield_gray_corner_bg));
                } else if ("0".equals(chatRecordBean.getIsHide())) {
                    holder.text_shield.setText("屏蔽他");
                    holder.text_shield.setTextColor(context.getResources().getColor(R.color.color_CF5D45));
                    holder.text_shield.setBackground(context.getResources().getDrawable(R.drawable.shield_corner_bg));
                }
            }

            holder.text_shield.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideUser(chatRecordBean, holder, position);
                }
            });

            holder.text_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatRecordBean.isSeeMore()) {
                        chatRecordBeanList.get(position).setSeeMore(false);
                        notifyItemChanged(position);
                    } else {
                        chatRecordBeanList.get(position).setSeeMore(true);
                        notifyItemChanged(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatRecordBeanList.size();
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
        private TextView text_gender;
        private ImageView image_head;
        private TextView text_shield;
        private TextView text_more;
        private RecyclerView recycler_record;

        public ViewHolder(View view) {
            super(view);
            image_head = (ImageView) view.findViewById(R.id.image_head);
            text_name = (TextView) view.findViewById(R.id.text_name);
            text_gender = (TextView) view.findViewById(R.id.text_gender);
            text_shield = (TextView) view.findViewById(R.id.text_shield);
            text_more = (TextView) view.findViewById(R.id.text_more);
            recycler_record = (RecyclerView) view.findViewById(R.id.recycler_record);
        }
    }

    /**
     * 咨询师屏蔽或取消屏蔽客户
     *
     * @param chatRecordBean
     */
    public void hideUser(ChatRecordBean chatRecordBean, ViewHolder holder, int position) {
        Map<String, Object> params = new HashMap<>();
        params.put("customerUserName", chatRecordBean.getUserName());
        params.put("signUserName", BaseApplication.getInstance().getUser().getUserName());
        if ("0".equals(chatRecordBean.getIsHide())) {
            params.put("hideOrCancel", "hide");//屏蔽
        } else {
            params.put("hideOrCancel", "cancel");//取消屏蔽
        }
        sendHide(params, holder, position);
    }

    private void sendHide(final Map<String, Object> params, final ViewHolder holder, final int position) {
        okHttpHelper.post(Contants.BASE_URL + "sign_language/signHideUser", params, new SpotsCallBack<BaseRespMsg>(context) {
            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg != null) {
                    if (baseRespMsg.getCode() >= 0) {
                        if (params.get("hideOrCancel").equals("hide")) {
                            chatRecordBeanList.get(position).setIsHide("1");
                            holder.text_shield.setText("取消屏蔽");
                            holder.text_shield.setTextColor(context.getResources().getColor(R.color.gray));
                            holder.text_shield.setBackground(context.getResources().getDrawable(R.drawable.shield_gray_corner_bg));
                            ToastUtils.show(context, "屏蔽成功");
                        } else if ("cancel".equals(params.get("hideOrCancel"))) {
                            chatRecordBeanList.get(position).setIsHide("0");
                            holder.text_shield.setText("屏蔽他");
                            holder.text_shield.setTextColor(context.getResources().getColor(R.color.color_CF5D45));
                            holder.text_shield.setBackground(context.getResources().getDrawable(R.drawable.shield_corner_bg));
                            ToastUtils.show(context, "取消屏蔽");
                        }
                    } else {
                        ToastUtils.show(context, baseRespMsg.getMsg());
                    }
                }
            }
        });
    }
}
