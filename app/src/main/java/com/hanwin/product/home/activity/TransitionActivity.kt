package com.hanwin.product.home.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.hanwin.product.R
import com.hanwin.product.utils.AppUtils

/**
 * TODO:过渡页面
 * @acthor weiang
 * 2019/6/18 2:29 PM
 */
class TransitionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)
        var title = ""
        var path =  ""
        var page = ""
        var type = ActivitiesWebActivity.TYPE_REACTIVE_URL
        val action = intent.action
        if (Intent.ACTION_VIEW == action) {
            try {
                val uri = intent.data
                if (uri != null) {
                    //根据属性值获取数据
                    title = uri.getQueryParameter("title")
                    path = uri.getQueryParameter("path")
                    page = uri.getQueryParameter("page")
                }
            } catch (e: Exception) {
            }
        }
        if (!AppUtils.isLogin()) {
            var intent = Intent()
            intent.setClass(this, ThirdLoginActivity::class.java)
            startActivity(intent)
        } else {
            if(AppUtils.isSignLanguageTeacher()){
                finish()
                return
            }
            ActivitiesWebActivity.startActivity(this,  path+"/"+page, title, type)
        }
        finish()
    }

}
