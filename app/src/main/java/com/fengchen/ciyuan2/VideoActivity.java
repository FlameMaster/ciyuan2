package com.fengchen.ciyuan2;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.SurfaceHolder;

import com.fengchen.ciyuan2.databinding.ActivityVideoBinding;
import com.fengchen.light.view.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/10 14:10
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class VideoActivity extends BaseActivity<ActivityVideoBinding> {

    String url = "https://yys.v.netease.com/2018/0725/ebefc466c32aa2c40aede8207956aae8qt.mp4";
    String url2 = "https://webstatic.bh3.com/video/bh3.com/pv/CG_OP_1800.mp4";
    MediaBinder mMediaBinder;
    List<MediaModel> mVideos;
    MediaServiceConnection  mMediaServiceConnection;

    @Override
    protected void initActivity() {
        mVideos = new ArrayList<>();
        mVideos.add(new MediaModel(url2));
        mMediaBinder = new MediaBinder();
        getViewDataBinding().content.getHolder().addCallback(callback);
        mMediaBinder.setMedias(mVideos);

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_vidoe;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        bindMediaService();
        mMediaBinder.play(0);

    }


    /* 绑定播放器service*/
    protected void bindMediaService() {
        Intent service = new Intent(getIntent());
        service.setClass(this, MediaPlayerService.class);
        mMediaServiceConnection = new MediaServiceConnection();
        bindService(service, mMediaServiceConnection, Service.BIND_AUTO_CREATE);
        startService(service);
    }

    private class MediaServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            mMediaBinder.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };


}
