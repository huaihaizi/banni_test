package com.hanwin.product.utils.recorder;

/**
 * Created by admin on 2019/5/8.
 */

public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}
