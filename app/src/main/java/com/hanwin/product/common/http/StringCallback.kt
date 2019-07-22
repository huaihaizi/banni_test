package com.tencent.liteav.demo.httputils

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.lzy.okgo.callback.AbsCallback
import okhttp3.Response
import org.json.JSONObject

abstract class StringCallback<T> : AbsCallback<String>() {
    private var isResponse = false
    //private lateinit var baseRequest: BaseRequest<T>
    override fun convertResponse(response: Response?): String {
        var responseString: String? = ""
        isResponse = true
        if (response != null && response.body() != null) {
            responseString = response.body()?.string()
        }
        response?.close()
        return responseString!!
    }

    /**
     * TODO 请求数据成功
     * @acthor weiang
     * 2019/3/18 4:02 PM
     */
    override fun onSuccess(response: com.lzy.okgo.model.Response<String>?) {
        var responseString = ""
        isResponse = true
        if (response != null && response.body() != null) {
            responseString = response.body().toString()
            if (!TextUtils.isEmpty(responseString)) {
                onResponse(responseString)
            } else {
                //onFaile(ApiConstants.ERROR_CODE_EXCEPTION, ApiConstants.ERROR_MESSAGE)
            }
        } else {
            //onFaile(ApiConstants.ERROR_CODE_EXCEPTION, ApiConstants.ERROR_MESSAGE)
        }
    }


    /**
     * TODO 请求数据失败
     * @acthor weiang
     * 2019/3/18 4:02 PM
     */
    override fun onError(response: com.lzy.okgo.model.Response<String>?) {
        super.onError(response)
        onFaile(0, response.toString())
    }

    /**
     * 返回数据处理
     *
     * @param response
     */
    fun onResponse(response: String) {
        val json = JSONObject(response)
        onStringSuccess(response, response)
//        var data: String? = json.optString(ApiConstants.DATA)
//        val code = json.optInt(ApiConstants.CODE)
//        var message = json.optString(ApiConstants.MESSAGE)
//        baseRequest = Gson().fromJson(response)
//        if (200 == code) {
//            if (baseRequest != null) {
//                val info: T = baseRequest.data
//                Log.d("data", "------------info------>:" + info.toString())
//                onInfoSuccess(info, response)
//            }
//            onStringSuccess(data, response)
//        } else {
//            onFaile(code, message)
//        }
    }

    /**
     * 请求失败回调
     *
     * @param code 失败码
     * @param msg  失败信息
     */
    abstract fun onFaile(code: Int, msg: String)

    /**
     * 请求成功回调
     *
     * @param strData  data数据
     * @param response Callback原始response
     */
    fun onInfoSuccess(info: T, response: String?) {

    }

    /**
     * 请求成功回调
     * @param strData  data数据
     * @param response Callback原始response
     */
    abstract fun onStringSuccess(strData: String?, response: String?)

}