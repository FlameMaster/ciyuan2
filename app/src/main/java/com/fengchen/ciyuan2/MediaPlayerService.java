package com.fengchen.ciyuan2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

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
 * = 时 间：2018/12/10 15:59
 * <p>
 * = 分 类 说 明：媒体播放的服务
 * ============================================================
 */
public class MediaPlayerService extends Service {

    private MediaBinder mMediaBinder;

    @Override
    public IBinder onBind(Intent intent) {
        return mMediaBinder;
    }


    @Override
    public void onCreate() {
        mMediaBinder = new MediaBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mMediaBinder.stop();
    }




    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    public final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int positon;

        public PreparedListener(int positon) {
            this.positon = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
        }
    }
}
