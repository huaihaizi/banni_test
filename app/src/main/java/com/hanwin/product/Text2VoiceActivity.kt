package com.hanwin.product

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hanwin.product.common.BaseActivity
import com.hanwin.product.home.adapter.TextVoiceAdapter
import com.hanwin.product.home.bean.VoiceListBean
import com.hanwin.product.home.service.AudioRecognizeService
import com.hanwin.product.listener.AudioRecognizeListener
import com.hanwin.product.utils.AppUtils
import com.hanwin.product.utils.PermissionUtil
import com.hanwin.product.utils.SpaceItemDecoration
import com.hanwin.product.utils.ToastUtils.context
import kotlinx.android.synthetic.main.activity_text_voice.*
import kotlinx.android.synthetic.main.popup_text_voice_layout.*

class Text2VoiceActivity : BaseActivity(), AudioRecognizeListener {
    var isAudioStart = false
    lateinit var adapter: TextVoiceAdapter
    lateinit var list: ArrayList<VoiceListBean>
    var handler: Handler? = Handler()
    lateinit var service: AudioRecognizeService
    var textContent: String = ""
    lateinit var languageList: ArrayList<String>
    lateinit var languageAdapter: TextVoicePopupAdapter
    lateinit var edit_layout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionUtil.permissionVideo(this)
        setContentView(R.layout.activity_text_voice)
        edit_layout = findViewById(R.id.edit_layout)
        initRecycleView()
        initLanguageRecycleView()
        service = AudioRecognizeService()
        service.setAudioRecognizeListener(this)
        val intent = Intent(this, service.javaClass)
        startService(intent)
        waveview.setMaxRadius(130f)
        waveview.setDuration(5000)
        waveview.setStyle(Paint.Style.FILL)
        waveview.setColor(Color.parseColor("#FF4500"))
        waveview.setInterpolator(LinearOutSlowInInterpolator())
        image_back.setOnClickListener {
            finish()
        }

        baidu.setOnClickListener {
            adapter.setVoiceType(TextVoiceAdapter.BAIDU_VOICE_TYPE)
        }

        tencent.setOnClickListener {
            adapter.setVoiceType(TextVoiceAdapter.TENCENT_VOICE_TYPE)
        }

        language_image.setOnClickListener {
            if (View.GONE == language_list_layout.visibility) {
                if (isAudioStart) {
                    service.stopRecognize()
                    waveview.stop()
                }
                language_list_layout.visibility = View.VISIBLE
            } else {
                if (isAudioStart) {
                    service.startRecognize(this)
                    waveview.start()
                }
                language_list_layout.visibility = View.GONE
            }
        }

        language_edit.setOnClickListener {
            if (View.GONE == language_list_layout.visibility) {
                if (isAudioStart) {
                    service.stopRecognize()
                    waveview.stop()
                }
                language_list_layout.visibility = View.VISIBLE
            } else {
                if (isAudioStart) {
                    service.startRecognize(this)
                    waveview.start()
                }
                language_list_layout.visibility = View.GONE
            }
        }

        edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(edit_text.text.toString())) {
                    button_confirm.setText("确定")
                } else {
                    button_confirm.setText("返回")
                }
            }
        })

        yuyin_button.setOnClickListener {
            edit_layout.visibility = View.GONE
            language_list_layout.visibility = View.GONE
            select_layout.visibility = View.VISIBLE
            button_confirm.setText("返回")
            try {
                if (!isAudioStart) {
                    var toast = Toast.makeText(this, "开始", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    service.startRecognize(this)
                    waveview.start()
                } else {
                    var toast = Toast.makeText(this, "结束", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    service.stopRecognize()
                    waveview.stop()
                }
                isAudioStart = !isAudioStart
            } catch (e: Exception) {
                print(e.message)
            }
        }

        button_confirm.setOnClickListener {
            if ("确定".equals(button_confirm.text.toString())) {
                activityVoiceId = ""
                textContent = edit_text.text.toString()
                var voiceListBean = VoiceListBean()
                voiceListBean.text = textContent
                voiceListBean.type = 2
                list.add(voiceListBean)
                adapter.setNewData(list)
                activityVoiceId = ""
                var position = 0
                if (adapter != null && adapter.itemCount > 1) {
                    position = adapter.itemCount - 1
                }
                recycle_view.smoothScrollToPosition(position)
                edit_text.text.clear()
            } else {
                edit_layout.visibility = View.GONE
                select_layout.visibility = View.VISIBLE
                AppUtils.closeKeyboard(this, edit_text)
                if (isAudioStart) {
                    service.startRecognize(this)
                    waveview.start()
                }
            }
        }

        image_key.setOnClickListener {
            language_list_layout.visibility = View.GONE
            service.stopRecognize()
            waveview.stop()
            select_layout.visibility = View.GONE
            edit_layout.visibility = View.VISIBLE
            edit_text.setFocusable(true)
            edit_text.setFocusableInTouchMode(true)
            edit_text.requestFocus()
            AppUtils.showKeyboard(this, edit_text)
        }

        image_edit.setOnClickListener {
            language_list_layout.visibility = View.GONE
            service.stopRecognize()
            waveview.stop()
            select_layout.visibility = View.GONE
            edit_layout.visibility = View.VISIBLE
            edit_text.setFocusable(true)
            edit_text.setFocusableInTouchMode(true)
            edit_text.requestFocus()
            AppUtils.showKeyboard(this, edit_text)
        }

    }

    override fun onPause() {
        super.onPause()
        if (service != null) {
            service.stopRecognize()
        }
    }

    /**
     * TODO 初始化recycleview
     * @acthor weiang
     * 2019/3/18 6:04 PM
     */
    fun initRecycleView() {
        list = ArrayList<VoiceListBean>()
        adapter = TextVoiceAdapter(list)
        val layoutManager = LinearLayoutManager(this)
        //设置布局管理器
        recycle_view.setLayoutManager(layoutManager)
        //设置为垂直布局，这也是默认的
        layoutManager.orientation = OrientationHelper.VERTICAL
        recycle_view.addItemDecoration(SpaceItemDecoration(2))
        //设置Adapter
        recycle_view.setAdapter(adapter)
    }

    fun initLanguageRecycleView() {
        //常用语列表初始化
        language_recycle_view?.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        languageList = ArrayList<String>()
        languageList.add("好的")
        languageList.add("明白")
        languageList.add("谢谢您")
        languageList.add("再见")
        languageList.add("我知道了")
        languageAdapter = TextVoicePopupAdapter(languageList)
        language_recycle_view.adapter = languageAdapter
        languageAdapter.setOnItemClickListener { l, view, position ->
            activityVoiceId = ""
            var voiceListBean = VoiceListBean()
            var s: String = languageList.get(position)
            voiceListBean.text = s
            voiceListBean.type = 2
            list.add(voiceListBean!!)
            adapter.setNewData(list)
            recycle_view.smoothScrollToPosition(adapter.itemCount - 1)
            language_list_layout.visibility = View.GONE
        }

    }

    var activityVoiceId = ""
    var textChangeTime: Long = 0
    override fun onSliceSuccess(text: String, voiceId: String) {
        Log.d("onSuccess", "--------------3--------------->: " + text)
        if (!TextUtils.isEmpty(text)) {
            if (!activityVoiceId.equals(voiceId)) {
                activityVoiceId = voiceId
                var voiceListBean = VoiceListBean()
                voiceListBean.text = text
                voiceListBean.type = 1
                list.add(voiceListBean)
            } else {
                list.get(list.size - 1).text = text
            }
            textChangeTime = System.currentTimeMillis()
            adapter.setNewData(list)
            recycle_view.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onSegmentSuccess(text: String, voiceId: String) {
        Log.d("onSuccess", "--------------4--------------->: " + text)
    }


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


}
