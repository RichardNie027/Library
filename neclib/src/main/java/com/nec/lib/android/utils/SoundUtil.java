package com.nec.lib.android.utils;

import android.media.AudioManager;
import android.media.SoundPool;

import com.nec.lib.android.application.MyApplication;

import java.util.HashMap;

public class SoundUtil {

    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;

    public static SoundUtil newInstance(int... resIds) {
        SoundUtil soundUtil = new SoundUtil();
        soundUtil.mSoundPool = new SoundPool(1, // maxStreams参数，该参数为设置同时能够播放多少音效
                AudioManager.STREAM_MUSIC, // streamType参数，该参数设置音频类型，在游戏中通常设置为：STREAM_MUSIC
                0 // srcQuality参数，该参数设置音频文件的质量，目前还没有效果，设置为0为默认值。
        );
        soundUtil.mSoundPoolMap = new HashMap<Integer, Integer>();
        int soundIdx = 1;
        for (int resId: resIds) {
            soundUtil.mSoundPoolMap.put(soundIdx++, soundUtil.mSoundPool.load(MyApplication.getCurrentActivity(), resId, 1));
        }
        return soundUtil;
    }

    public void playBeepSound(int soundIdx) {
        mSoundPool.play(mSoundPoolMap.get(soundIdx), 1, 1, 1, 0, 1);
    }

}
