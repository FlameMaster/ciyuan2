package com.fengchen.ciyuan2;

import android.media.MediaPlayer;
import android.media.MediaTimestamp;
import android.os.Binder;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.Serializable;
import java.util.List;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/10 14:54
 * <p>
 * = 分 类 说 明：媒体播放辅助类
 * ============================================================
 */
public class MediaBinder extends Binder implements Serializable,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {

    /*媒体列表*/
    private List<MediaModel> mMedias;
    /*播放器*/
    private MediaPlayer mMediaPlayer;
    /*当前位置*/
    private int nowMediaPosition;

    public MediaBinder(){
        if (mMediaPlayer != null) mMediaPlayer.reset();
        mMediaPlayer = new MediaPlayer();
        nowMediaPosition =0;
    }


    /*释放资源*/
    public void release(){
        nowMediaPosition =0;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mMedias!=null){
            mMedias.clear();
            mMedias=null;
        }
    }

    /*设置播放列表*/
    public void setMedias(List<MediaModel> datas) {
        mMedias = datas;
    }

    /*设置视频播放的holder*/
    public void setDisplay(SurfaceHolder holder){
        mMediaPlayer.setDisplay(holder);
    }

    /*播放完成后*/
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 自动播放下一首
        next();
    }

    /*媒体准备完成*/
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("onPrepared","媒体准备完成");
        start();//开始播放
        if (nowMediaPosition > 0) {    //如果不是从头播放
            mMediaPlayer.seekTo(nowMediaPosition);
        }
    }

    /**
     * 如果正在播放则返回true
     */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /*播放*/
    public void play(int position) {

        //列表循环
        if (position < 0) position = mMedias.size() - 1;
        else if (position >= mMedias.size()) position = 0;

        //通知ui变更
        nowMediaPosition = position;

        try {
            mMediaPlayer.reset();//把各项参数恢复到初始状态
            mMediaPlayer.setDataSource(mMedias.get(position).getUrl());
            mMediaPlayer.prepareAsync();
//            mMediaPlayer.prepare();    //进行缓冲
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*暂停*/
    public void pause() {
        if (mMediaPlayer != null && isPlaying()) {
            //通知ui变更
            mMediaPlayer.pause();
        }
    }

    /*开始播放*/
    public void start() {
        if (mMediaPlayer != null && !isPlaying()) {
            //通知ui变更
            mMediaPlayer.start();
        }
    }

    /*停止*/
    public void stop() {
        if (mMediaPlayer != null) {
            //通知ui变更
            mMediaPlayer.stop();
            try {
                mMediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*跳转播放进度*/
    public void seekTo(int mSec) {
        mMediaPlayer.seekTo(mSec);
    }

    /*下一首*/
    public void next() {
        play(nowMediaPosition+1);
    }

    /*上一首*/
    public void previous() {
        play(nowMediaPosition-1);
    }

    /* 返回当前的总时长*/
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    /*返回当前已播放时间*/
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /*获取当前位置*/
    public int getNowMediaPosition() {
        return nowMediaPosition;
    }

    /*获取对应位置的数据*/
    public MediaModel getMediaData(int position){
        return mMedias.get(position);
    }

}
