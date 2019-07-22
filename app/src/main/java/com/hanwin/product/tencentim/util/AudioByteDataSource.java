package com.hanwin.product.tencentim.util;

import com.tencent.aai.audio.data.PcmAudioDataSource;
import com.tencent.aai.audio.exception.AudioRecognizerException;

/**
 * Created by admin on 2019/5/10.
 */

public class AudioByteDataSource implements PcmAudioDataSource{
    private int bufferSize;
    private short[] dataSource;

    public AudioByteDataSource(){
        this.bufferSize = 960;
    }

    @Override
    public int read(short[] audioPcmData, int length) {
        try {
            if(this.dataSource == null) {
                return -1;
            }
            System.arraycopy(this.dataSource, 0, audioPcmData, 0, this.dataSource.length);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
        return this.dataSource.length;
    }

    @Override
    public void start() throws AudioRecognizerException {

    }

    @Override
    public void stop() {
        if(dataSource != null){
            dataSource = null;
        }
    }

    @Override
    public int maxLengthOnceRead() {
        return this.bufferSize;
    }

    public void addByteData(short[] dataSource){
        this.dataSource = dataSource;
    }
}
