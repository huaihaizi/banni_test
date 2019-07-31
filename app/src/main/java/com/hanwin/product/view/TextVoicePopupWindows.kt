package com.hanwin.product.view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hanwin.product.R
import com.hanwin.product.home.bean.VoiceListBean
import com.hanwin.product.utils.Contants.list
import org.jetbrains.anko.longToast
import java.util.*

/**
 * TODO:常用语选择栏
 * @acthor weiang
 * 2019/7/17 1:31 PM
 */
class TextVoicePopupWindows : PopupWindow {
    var context: Context
    lateinit var recyclerView: RecyclerView
    lateinit var rootLayout: LinearLayout
    lateinit var button_cancle: TextView
    lateinit var list: ArrayList<String>
    lateinit var adapter: TextVoicePopupAdapter
    lateinit var listener: PopupWindowsItemListener

    constructor(context: Context) : super(context) {
        this.context = context
        init()
    }

    /**
     * TODO:初始化
     * @acthor weiang
     * 2019/7/17 1:34 PM
     */
    private fun init() {
        list = ArrayList()
        list.add("你好,我听力不好，请对着手机说话，手机会将 您的声音转化成文字，谢谢！")
        list.add("请您稍微讲慢一点，这样软件可以翻译的更加准确")
        list.add("我正在打字跟您沟通，请您多等一会")
        val inflater: LayoutInflater = LayoutInflater.from(context)
        var recordingView = inflater.inflate(R.layout.popup_text_voice_layout, null)
        this.contentView = recordingView
        recyclerView = contentView.findViewById(R.id.language_recycle_view)
        rootLayout = contentView.findViewById(R.id.root_layout)
        button_cancle = contentView.findViewById(R.id.button_cancle)
        // 设置弹出窗口的宽
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置弹出窗口的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        //实例化一个ColorDrawable颜色为半透明
        val colorDrawable = ColorDrawable(
                context.getResources().getColor(R.color.color_50_000000))
        this.setBackgroundDrawable(colorDrawable)
        this.isFocusable = true// 设置弹出窗口可
        this.isTouchable = true
        this.isOutsideTouchable = true
        initRecycleView()
        button_cancle.setOnClickListener {
            dismiss()
        }
    }

    /*
     * TODO 初始化 recycleview
     * @acthor weiang
     * 2019/3/19 3:31 PM
     */
    private fun initRecycleView() {
        adapter = TextVoicePopupAdapter(list)
        recyclerView?.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        if (list != null && list.size > 3) {
            var lp = recyclerView.layoutParams
            lp.height = context.resources.getDimension(R.dimen.dp_40).toInt() * 3
            recyclerView.setLayoutParams(lp)
        }
        recyclerView?.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            var bean = VoiceListBean()
            bean.text = adapter.data.get(position).toString()
            bean.type = 2
            if (listener != null) {
                listener.onPopupItemClick(bean)
            }
            dismiss()
        }
    }

    fun setOnPopupItemClickListener(listener: PopupWindowsItemListener) {
        this@TextVoicePopupWindows.listener = listener
    }

    /**
     * TODO 常用语
     * @acthor weiang
     * 2019/3/19 2:49 PM
     */
    class TextVoicePopupAdapter(data: ArrayList<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_popup_status_layout, data) {
        override fun convert(helper: BaseViewHolder?, item: String?) {
            var textview_status: TextView = helper?.getView(R.id.textview_status)!!
            var lint: View = helper?.getView(R.id.line_view)!!
            if (helper.adapterPosition == data.size - 1) {
                lint.visibility = View.GONE
            } else {
                lint.visibility = View.VISIBLE
            }
            textview_status.isSelected = true
            textview_status.text = item
        }
    }

    override fun showAsDropDown(anchor: View) {
        if (Build.VERSION.SDK_INT >= 24) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor)
    }

    //点击监听
    interface PopupWindowsItemListener {
        fun onPopupItemClick(item: VoiceListBean?)
    }

}