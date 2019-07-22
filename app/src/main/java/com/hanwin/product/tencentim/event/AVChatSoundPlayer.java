package com.hanwin.product.tencentim.event;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.hanwin.product.R;
import com.hanwin.product.common.BaseApplication;

/**
 */
public class AVChatSoundPlayer {

    private static final String TAG = "AVChatSoundPlayer";

    public enum RingerTypeEnum {
        CONNECTING,
        NO_RESPONSE,
        PEER_BUSY,
        PEER_REJECT,
        RING,;
    }

    private Context context;
    private RingerTypeEnum ringerTypeEnum;
    MediaPlayer mediaPlayer;

    public AVChatSoundPlayer() {
        this.context = BaseApplication.getInstance();
    }

    public synchronized void play(RingerTypeEnum type) {
        Log.e(TAG, "play type->" + type.name());
        this.ringerTypeEnum = type;
        int ringId = 0;
        switch (type) {
            case NO_RESPONSE:
                ringId = R.raw.avchat_no_response;
                break;
            case PEER_BUSY:
                ringId = R.raw.avchat_peer_busy;
                break;
            case PEER_REJECT:
                ringId = R.raw.avchat_peer_reject;
                break;
            case CONNECTING:
                ringId = R.raw.avchat_connecting;
                break;
            case RING:
                ringId = R.raw.avchat_ring;
                break;
        }
        if (ringId != 0) {
            play(ringId);
        }
    }

    private void play(int ringId) {
        mediaPlayer = MediaPlayer.create(context, ringId);
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(context, ringId);
            }
            mediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//设置重复播放
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void stop() {
        Log.e(TAG, "stop");
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
