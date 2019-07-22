package com.hanwin.product.sscr;

import com.hanwin.product.utils.ByteToInputStream;
import com.hanwin.product.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * TODO:ssrc转换工具类
 *
 * @acthor weiang
 * 2019/6/3 11:16 AM
 */
public class SsrcUtils {

    public static byte[] simpleDownSample(byte[] bytes) {
        File SampleChangedFile = new File(FileUtil.getAdtoSdPath() + "www.mp3");
        if (!SampleChangedFile.exists()) {
            SampleChangedFile.mkdirs();
        }
        try {
            InputStream inputStream = new ByteArrayInputStream(bytes);
            FileOutputStream fileOutputStream = new FileOutputStream(SampleChangedFile);
            new SSRC(inputStream, fileOutputStream, 48000, 16000,
                    2,
                    2,
                    1, Integer.MAX_VALUE, 0, 0, true);
            return ByteToInputStream.File2Bytes(SampleChangedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
