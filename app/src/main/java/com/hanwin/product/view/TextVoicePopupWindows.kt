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
        // 好的，明白，谢谢您，再见，我知道了
        list.add("好的")
        list.add("明白")
        list.add("谢谢您")
        list.add("再见")
        list.add("我知道了")
        val inflater: LayoutInflater = LayoutInflater.from(context)
        var recordingView = inflater.inflate(R.layout.popup_text_voice_layout, null)
        this.contentView = recordingView
        recyclerView = contentView.findViewById(R.id.recycle_view)
        rootLayout = contentView.findViewById(R.id.root_layout)
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT// 设置弹出窗口的宽
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT// 设置弹出窗口的高
        //实例化一个ColorDrawable颜色为半透明
        val colorDrawable = ColorDrawable(
                context.getResources().getColor(R.color.translucence))
        this.setBackgroundDrawable(colorDrawable)
        this.isFocusable = true// 设置弹出窗口可
        this.isTouchable = true
        this.isOutsideTouchable = false
        initRecycleView()
    }

    /*
     * TODO 初始化 recycleview
     * @acthor weiang
     * 2019/3/19 3:31 PM
     */
    private fun initRecycleView() {
        adapter = TextVoicePopupAdapter(list)
        recyclerView?.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        if (list != null && list.size > 10) {
            var lp = recyclerView.layoutParams
            lp.height = context.resources.getDimension(R.dimen.dp_40).toInt() * 10
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


    // class

    /**
     * TODO 常用语
     * @acthor weiang
     * 2019/3/19 2:49 PM
     */
    class TextVoicePopupAdapter(data: ArrayList<String>?) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_popup_status_layout, data) {
        override fun convert(helper: BaseViewHolder?, item: String?) {
            var textview_status: TextView = helper?.getView(R.id.textview_status)!!
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