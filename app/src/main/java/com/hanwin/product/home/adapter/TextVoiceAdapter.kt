package com.hanwin.product.home.adapter

import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hanwin.product.R
import com.hanwin.product.Text2VoiceActivity
import com.hanwin.product.home.bean.VoiceListBean
import com.hanwin.product.utils.AppUtils
import com.tencent.qcloudtts.callback.QCloudPlayerCallback

/**
 * TODO:列表适配器
 * @acthor weiang
 * 2019/7/3 2:31 PM
 */
class TextVoiceAdapter : BaseQuickAdapter<VoiceListBean, BaseViewHolder> {

    private var voiceType = 0

    constructor(list: ArrayList<VoiceListBean>) : super(R.layout.item_voice_layout) {

    }

    companion object {
        //百度语音
        val BAIDU_VOICE_TYPE = 0
        //腾讯语音
        val TENCENT_VOICE_TYPE = 1
    }

    fun setVoiceType(voiceType: Int) {
        this@TextVoiceAdapter.voiceType = voiceType
    }

    override fun convert(helper: BaseViewHolder?, item: VoiceListBean?) {
        var content_textview = helper?.getView<TextView>(R.id.content_text)
        var edit_textview = helper?.getView<TextView>(R.id.eidt_text)
        var imageView = helper?.getView<ImageView>(R.id.image_view)
        var content_layout = helper?.getView<LinearLayout>(R.id.content_layout)
        var edit_layout = helper?.getView<LinearLayout>(R.id.edit_layout)
        if (!TextUtils.isEmpty(item?.text)) {
            content_textview?.text = item?.text
            edit_textview?.text = item?.text
        }
        if (1 == item?.type) {
            edit_layout?.visibility = View.GONE
            content_layout?.visibility = View.VISIBLE
        } else {
            content_layout?.visibility = View.GONE
            edit_layout?.visibility = View.VISIBLE
        }

        helper?.addOnClickListener(R.id.image_view)
        if (item!!.isPlay) {
            AppUtils.tencentSpeek(item?.text, object : QCloudPlayerCallback {
                override fun onTTSPlayStart() {
                    Text2VoiceActivity.currPlayPosition = helper!!.adapterPosition
                    (imageView?.background as AnimationDrawable).start()
                }

                override fun onTTSPlayWait() {
                }

                override fun onTTSPlayNext() {
                }

                override fun onTTSPlayStop() {
                }

                override fun onTTSPlayEnd() {
                    (imageView?.background as AnimationDrawable).stop()
                    (imageView?.background as AnimationDrawable).selectDrawable(0)
                    item.isPlay = false
                }

                override fun onTTSPlayResume() {
                }
            })
        }
    }
}
