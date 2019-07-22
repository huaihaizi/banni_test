/**
 * @ create_date 2014-12-1
 * @ author lu_qwen
 * @ version 1.0
 */
package com.hanwin.product.viewutils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
 * @title MyListViewformesure
 * @description 自定义listview 解决计算listview高度不准确问题
 * @author peng_yqun
 * @version 
 * @create_date 2014-12-25
 * @copyright (c) CVIC SE
 *
 */
public class MyListViewformesure extends ListView {
	public MyListViewformesure(Context context) {
		super(context);
	}
	public MyListViewformesure(Context context, AttributeSet as) {
		  super(context,as);
		 }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
