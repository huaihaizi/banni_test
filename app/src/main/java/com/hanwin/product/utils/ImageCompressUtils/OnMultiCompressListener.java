package com.hanwin.product.utils.ImageCompressUtils;

import java.io.File;
import java.util.List;

/**
 * Created by zhaopf on 2017/11/30.
 */

public interface OnMultiCompressListener {
    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(List<File> fileList);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(Throwable e);
}
