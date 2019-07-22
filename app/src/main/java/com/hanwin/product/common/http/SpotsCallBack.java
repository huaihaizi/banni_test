package com.hanwin.product.common.http;

import android.content.Context;

import com.hanwin.product.utils.ToastUtils;
import com.hanwin.product.viewutils.DialogUtil;

import dmax.dialog.SpotsDialog;
import okhttp3.Request;
import okhttp3.Response;


public abstract class SpotsCallBack<T> extends SimpleCallback<T> {
    private static SpotsDialog mDialog;
    private boolean isShowDialog = true;
    private Context context;
    private DialogUtil dialogUtil;

    public SpotsCallBack(Context context, String... first) {
        super(context);
        this.context = context;
        if (first != null && first.length > 0) {
            //暂无逻辑
            isShowDialog = false;
        } else {
            initSpotsDialog();
            isShowDialog = true;
        }
    }

    private void initSpotsDialog() {
//        mDialog = new SpotsDialog(context, R.style.mySpotsDialogDefault);
        dialogUtil = new DialogUtil();
        dialogUtil.showLoadingDialog(context, "");
    }

    public void showDialog() {
        if (dialogUtil != null) {
            dialogUtil.dialogLoading.show();
        }
    }

    public void dismissDialog() {
        if (dialogUtil != null) {
            dialogUtil.dismiss();
        }
    }


    public void setLoadMessage(int resId) {
        mDialog.setMessage(context.getString(resId));
    }

    @Override
    public void onBeforeRequest(Request request) {
        if (isShowDialog) {
            showDialog();
        }
    }

    @Override
    public void onResponse(Response response) {
        if (isShowDialog) {
            dismissDialog();
        }
    }

    @Override
    public void onFailure(Request request, Exception e) {
        super.onFailure(request, e);
        if (isShowDialog) {
            dismissDialog();
        }
        ToastUtils.show(context, "服务器忙，请稍后重试");
    }

    @Override
    public void onError(Response response, int code, Exception e) {
        super.onError(response, code, e);
        if (isShowDialog) {
            dismissDialog();
        }
        ToastUtils.show(context, "服务器忙，请稍后重试");
    }

    @Override
    public void onServerError(Response response, int code, String errmsg) {
        if (isShowDialog) {
            dismissDialog();
        }
        ToastUtils.show(context, "服务器忙，请稍后重试");
    }
}
