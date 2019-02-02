package com.fengchen.ciyuan2.a_movie;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.SeekBar;

import com.fengchen.ciyuan2.utils.CYUtils;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.RxBusClient;
import com.fengchen.ciyuan2.databinding.ActVideoBD;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.IOUtils;
import com.fengchen.light.utils.StringUtil;
import com.fengchen.light.view.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

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
 * = 分 类 说 明：播放视频页面，闲的蛋疼写的
 * ============================================================
 */
public class VideoActivity extends BaseActivity<ActVideoBD> {

    /*默认属性*/
    public static final int SCREEN_DIRECTION_UNDEFINED = 404;
    /*竖屏的两种风格*/
    private static final int[] SYSTEM_UI_FLAG_PORTRAIT = {
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    };
    /*横屏的两种风格*/
//    private static final int[] SYSTEM_UI_FLAG_LANDSCAPE = {
//            View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//    };
    private static final int[] SYSTEM_UI_FLAG_LANDSCAPE = {
            View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
            View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    };


    String url = "https://yys.v.netease.com/2018/0725/ebefc466c32aa2c40aede8207956aae8qt.mp4";
    String url2 = "https://webstatic.bh3.com/video/bh3.com/pv/CG_OP_1800.mp4";
    String url3 = "http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8";


    Intent mMediaPlayerIntent;
    MediaBinder mMediaBinder;
    List<MediaModel> mVideos;
    MediaServiceConnection mMediaServiceConnection;

    /*屏幕方向*/
    int mScreenDirection;
    /*是否横屏显示*/
    boolean isLandscape = false;
    //屏幕旋转监听的参数
    SensorManager mSensorManager;
    Sensor mSensor;
    SensorEventListener mSensorEventListener;
    /*屏幕方向开关变化的监听*/
    ContentObserver mScreenRotateObserver;
    /*横竖屏状态*/
    int mWindowFlag = 0;
    /*状态栏是否显示*/
    boolean isShowStableBar = false;
    /*显示工具条计时，开始进度条计时*/
    Disposable mChangeOrientationDisposable, mProgressDisposable;

    @Override
    public void changeWindowBar(int statusColor, int navigationColor) {
        if (isLandscape) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LANDSCAPE[mWindowFlag]);
        } else
            getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_PORTRAIT[mWindowFlag]);
        // 状态栏
        getWindow().setStatusBarColor(0x33000000);
        // 虚拟导航键
        getWindow().setNavigationBarColor(navigationColor);
    }

    @Override
    protected void initActivity() {
        //屏幕方向
        mScreenDirection = FCUtils.getResources()
                .getConfiguration().orientation;
        isLandscape = FCUtils.getResources()
                .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        //SurfaceHolder是SurfaceView的监听，callback是回调
        getViewDataBinding().tv.tvShow.getHolder().addCallback(callback);

        mVideos = new ArrayList<>();
        mVideos.add(new MediaModel(url2));

        //绑定播放的服务
        bindMediaService();
        //初始化屏幕旋转的参数
        initGravitySener();

        //状态栏变化的监听
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            Log.e("状态栏变化", "visibility=" + visibility);
            if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {//状态栏显示
                if (mChangeOrientationDisposable != null) mChangeOrientationDisposable.dispose();
                getViewDataBinding().tv.surfaceClick.setVisibility(View.VISIBLE);
                mChangeOrientationDisposable = Observable.timer(3, TimeUnit.SECONDS)
                        .compose(IOUtils.setThread())
                        .subscribe(aLong -> {
                            mWindowFlag=1;
                            showSurfaceClick(null);
                        });
            } else {//状态栏隐藏
                if (mChangeOrientationDisposable != null) mChangeOrientationDisposable.dispose();
                getViewDataBinding().tv.surfaceClick.setVisibility(View.GONE);
            }
        });

        //注册拖动监听
        getViewDataBinding().tv.tvProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 不是用户发起的变更，则不处理
                if (!fromUser) return;
                // 跳转播放进度
                mMediaBinder.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //注册rxbus
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_vidoe;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //这里重写返回键
            back(null);
            return true;
        }
        return false;
    }

///////////////////////——————————————————————点击事件—————————————————————————///////////////////////

    /*返回*/
    public void back(View view) {
        if (isLandscape) changeTV(null);
        else finish();
    }

    /*切换播放*/
    public void changePlay(View view) {
        if (mMediaBinder.isPlaying()) mMediaBinder.pause();
        else mMediaBinder.start();
    }

    /*切换屏幕*/
    public void changeTV(View view) {
        if (mWindowFlag == 0) mWindowFlag = 1;
        else mWindowFlag = 0;
        StringBuffer buffer = new StringBuffer("H,");
        int marginRight;
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            buffer.append(16).append(":").append(9);
            marginRight = 0;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            int[] size = CYUtils.getWindowsSize();
            buffer.append(size[0]).append(":").append(size[1]);
            marginRight = CYUtils.getNavigationBarHeight();
        }
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) getViewDataBinding().tv.tvShow.getLayoutParams();
        ConstraintLayout.LayoutParams lp2 = (ConstraintLayout.LayoutParams) getViewDataBinding().tv.tvChange.getLayoutParams();
        lp.dimensionRatio = buffer.toString();
        lp2.rightMargin = marginRight;
        getViewDataBinding().tv.tvShow.setLayoutParams(lp);
        getViewDataBinding().tv.tvChange.setLayoutParams(lp2);
    }

    /*显示/隐藏操作栏*/
    public void showSurfaceClick(View view) {
        Log.e("被点击了", "JOJO我不做人了");
        if (mWindowFlag == 0) {
            mWindowFlag = 1;
        } else mWindowFlag = 0;
        changeWindowBar(Color.TRANSPARENT, Color.TRANSPARENT);
    }

///////////////////////——————————————————————播放回调—————————————————————————///////////////////////

    RxBusClient mRxBusClient = new RxBusClient() {
        @Override
        protected void onEvent(int type, String message, Object data) {
            if (StringUtil.noNull(message)) {
                if (message.contains("tv_play"))
                    onMediaPlay((MediaModel) data);
                else if (message.contains("tv_start"))
                    onMediaStart();
                else if (message.contains("tv_pause"))
                    onMediaPause();
                else if (message.contains("tv_stop"))
                    onMediaStop();
            }
        }
    };

    /*开始播放*/
    private void onMediaPlay(MediaModel model) {
        //设置标题
        //初始化各种参数
        getViewDataBinding().tv.tvProgressMaxText.setText(CYUtils.formatDuration(mMediaBinder.getDuration()));
        getViewDataBinding().tv.tvProgress.setMax(mMediaBinder.getDuration());

        //开始更新进度条
        if (mProgressDisposable != null) mProgressDisposable.dispose();
        mProgressDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(aLong -> {
//                    Log.e("apply", "aLong=" + aLong);
//                    return FCUtils.getNowTime();
                    return aLong;
                })
                .compose(IOUtils.setThread())
                .subscribe(residueTime -> {
                    //更新进度条
                    updataProgress();
                });
    }

    /*播放*/
    private void onMediaStart() {
        getViewDataBinding().tv.tvProgressMaxText.setText(CYUtils.formatDuration(mMediaBinder.getDuration()));
        getViewDataBinding().tv.tvProgress.setMax(mMediaBinder.getDuration());
        getViewDataBinding().tv.tvPlay.setImageResource(R.drawable.ic_tv_pause);
    }

    /*暂停*/
    private void onMediaPause() {
        getViewDataBinding().tv.tvPlay.setImageResource(R.drawable.ic_tv_play);
    }

    /*结束*/
    private void onMediaStop() {
        getViewDataBinding().tv.tvPlay.setImageResource(R.drawable.ic_tv_play);
    }

    /*更新进度条*/
    private void updataProgress() {
        int position = mMediaBinder.getCurrentPosition();
        getViewDataBinding().tv.tvProgress.setProgress(position);
        getViewDataBinding().tv.tvProgressText.setText(CYUtils.formatDuration(position));
    }

///////////////////////——————————————————————屏幕切换—————————————————————————///////////////////////

    /*横竖屏切换时调用*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
//        横竖屏切换前调用，保存用户想要保存的数据
//        onSaveInstanceState(Bundle outState)
//        屏幕切换完毕后调用用户存储的数据
//        onRestoreInstanceState(Bundle savedInstanceState)
        super.onConfigurationChanged(newConfig);
        changeWindowBar(Color.TRANSPARENT, Color.TRANSPARENT);

        Log.e("切换横竖屏", "onConfigurationChanged" + newConfig.orientation);
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置横屏
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMediaBinder != null) {
            Log.e("开始播放", "onStart");
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        //设置视频播放的控件
                        mMediaBinder.setDisplay(getViewDataBinding().tv.tvShow.getHolder());
                        mMediaBinder.start();
                    });
        }
    }

    @Override
    public void onResume() {
        changeWindowBar(Color.TRANSPARENT, Color.TRANSPARENT);
        super.onResume();
        changeSensor();
        //注册屏幕变化开关的监听
        FCUtils.getContext().getContentResolver()
                .registerContentObserver(Settings.System
                                .getUriFor(Settings.System.ACCELEROMETER_ROTATION),
                        false, mScreenRotateObserver);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaBinder != null) {
            Log.e("暂停播放", "onStop");
            mMediaBinder.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除屏幕变化开关的监听
        FCUtils.getContext().getContentResolver()
                .unregisterContentObserver(mScreenRotateObserver);
        unbindService(mMediaServiceConnection);
        stopService(mMediaPlayerIntent);
        //注销rxbus
        mRxBusClient.unregister();
    }

    /*屏幕方向切换*/
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (mChangeOrientationDisposable != null) mChangeOrientationDisposable.dispose();
        if (requestedOrientation == SCREEN_DIRECTION_UNDEFINED | requestedOrientation == mScreenDirection)
            return;
        super.setRequestedOrientation(requestedOrientation);
        mScreenDirection = requestedOrientation;
    }

    /*构造初始化重力重力感应*/
    private void initGravitySener() {
        // 获取传感器管理器
        mSensorManager = (SensorManager) FCUtils.getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        // 获取传感器类型
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mSensorEventListener = new SensorEventListener() {
            /*感应检测到Sensor的值有变化*/
            @Override
            public void onSensorChanged(SensorEvent event) {

                //只需要重力传感器
                if (Sensor.TYPE_GRAVITY != event.sensor.getType()) return;

                //获取xy方向的偏移量
                float[] values = event.values;
                float x = values[0];
                float y = values[1];

                //根据偏移量判断方向
                int newOrientation = SCREEN_DIRECTION_UNDEFINED;
                if (x < 4.5 && x >= -4.5 && y >= 4.5) {//竖屏
                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (x >= 4.5 && y < 4.5 && y >= -4.5) {//横屏
                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else if (x <= -4.5 && y < 4.5 && y >= -4.5) {//翻转横屏
                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                } else if (x < 4.5 && x >= -4.5 && y < -4.5) {//翻转竖屏
                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                }
//                if (x < 3.5 && x >= -3.5) {//竖屏，具体方向由系统判断
//                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
//                } else if (y < 9 && y >= -9) {//横屏，具体方向由系统判断
//                    newOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
//                }

//                Log.e("监听内容的变化", "newOrientation=" + newOrientation + "____x=" + x + "____y=" + y);
                Log.e("监听内容的变化", "newOrientation=" + newOrientation + "mScreenDirection=" + mScreenDirection);
                setRequestedOrientation(newOrientation);

            }

            /*感应检测到Sensor的精密度有变化*/
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        mScreenRotateObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                changeSensor();
            }
        };
    }

    /*根据开关状态判断是否开启重力感应*/
    protected void changeSensor() {
        //注册监听，第三个属性是延迟紧密度
        if (CYUtils.isOpenScreenRotate())
            mSensorManager.registerListener(mSensorEventListener, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        else
            mSensorManager.unregisterListener(mSensorEventListener);
    }

    /* 绑定播放器service*/
    protected void bindMediaService() {
        mMediaPlayerIntent = new Intent(FCUtils.getContext(), MediaPlayerService.class);
        startService(mMediaPlayerIntent);


        mMediaServiceConnection = new MediaServiceConnection();
        bindService(mMediaPlayerIntent, mMediaServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private class MediaServiceConnection implements ServiceConnection {
        /*当与service的连接建立后被调用*/
        @SuppressLint("CheckResult")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("绑定成功", "当前时间" + new SimpleDateFormat("mm:ss:")
                    .format(new Date()));
            mMediaBinder = (MediaBinder) service;
            mMediaBinder.setMedias(mVideos);


            //开始播放
            mMediaBinder.play(0);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        //设置视频播放的控件
                        mMediaBinder.setDisplay(getViewDataBinding().tv.tvShow.getHolder());
                    });

        }

        /*当与service的连接意外断开时被调用*/
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("初始化callback", "surfaceCreated：holder=" + holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e("切换callback", "surfaceChanged：holder=" + holder);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e("释放callback", "surfaceChanged：holder=" + holder);
        }
    };


}
