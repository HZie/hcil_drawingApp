package com.example.drawingapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import java.util.HashMap;

public class SoundManager {
    private SoundPool mSoundPool;
    private HashMap<Integer,Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    private Context mContext;

    public SoundManager(Context mContext,SoundPool mSoundPool){
        this.mContext = mContext;
        this.mSoundPool = mSoundPool;
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void addSound(int index,int soundId){
        //효과음 추가
        mSoundPoolMap.put(index,mSoundPool.load(mContext,soundId,1));
    }

    public int playSound(int streamId){ //효과음 재생
         int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
         return mSoundPool.play(mSoundPoolMap.get(streamId),streamVolume,streamVolume,1,1000000,1.0f);
    }

    public void stopSound(int streamId){
        // mSoundPool.stop(streamId);
        mSoundPool.autoPause();
    }
    public void pauseSound(int streamId){
        mSoundPool.pause(streamId);
    }
    public void resumeSound(int streamId){
        mSoundPool.resume(streamId);
    }
}

