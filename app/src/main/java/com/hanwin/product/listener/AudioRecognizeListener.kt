package com.hanwin.product.listener

/**
 * TODO:实时语音转换监听
 * @acthor weiang
 * 2019/7/8 11:38 AM
 */
interface AudioRecognizeListener {

    /**
     * TODO:实时片段翻译
     * @acthor weiang
     * 2019/7/8 11:41 AM
     */
    fun onSliceSuccess(text:String,voiceId:String)

    /**
     * TODO:实时一句话翻译
     * @acthor weiang
     * 2019/7/8 11:41 AM
     */
    fun onSegmentSuccess(text:String,voiceId:String)

}