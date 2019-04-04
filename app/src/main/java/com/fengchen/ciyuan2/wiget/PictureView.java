package com.fengchen.ciyuan2.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.IOUtils;
import com.fengchen.light.utils.ImageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/2/26 17:39
 * <p>
 * = 分 类 说 明：效率太低了
 * ============================================================
 */
public class PictureView extends SurfaceView implements GestureDetector.OnGestureListener, SurfaceHolder.Callback {

    private BitmapRegionDecoder mDecoder;
    //绘制的区域
    private volatile Rect mRect;
    //触发移动事件的最小距离
    private int mScaledTouchSlop;
    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    //图片的宽度和高度
    private int mImageWidth, mImageHeight;
    //手势控制器
    private GestureDetector mGestureDetector;
    private BitmapFactory.Options mOptions;

    /*帮助类*/
    private SurfaceHolder surfaceHolder;
    private Canvas mCanvas;

    public PictureView(Context context) {
        this(context, null);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /*设置图片*/
    public void setImageUrl(String url) {
        //测量原始宽高
        measureOriginalSize(url);
    }

    /*初始化*/
    private void init() {
        //设置显示图片的参数，如果对图片质量有要求，就选择ARGB_8888模式
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        //初始化手势控制器
        mGestureDetector = new GestureDetector(FCUtils.getContext(), this);
        mScaledTouchSlop = ViewConfiguration.get(FCUtils.getContext())
                .getScaledTouchSlop();
        mRect = new Rect();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @SuppressLint("CheckResult")
    public void measureOriginalSize(String path) {
        //测量图片宽度
        Observable.create((ObservableOnSubscribe<BitmapFactory.Options>) emitter -> {
            InputStream inputStream = null;
            BitmapFactory.Options options = null;
            try {
                //对资源链接
                URL url = new URL(path);
                //打开输入流
//                inputStream = url.openStream();
                inputStream = FCUtils.getResources().getAssets().open("sc.png");
                //  加载参数
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                Log.e("超大图加载", new StringBuffer("方法:measureOriginalSize\n")
                        .append("width:[").append(options.outWidth).append("]\n")
                        .append("height:[").append(options.outHeight).append("]\n")
                        .toString());
                //顺便初始化一下，用来显示大图
                mDecoder = BitmapRegionDecoder
                        .newInstance(inputStream, false);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //关闭流
                IOUtils.close(inputStream);
                emitter.onNext(options);
                emitter.onComplete();
            }
        })
                .compose(IOUtils.setThread())
                .subscribe(options -> {
                    if (options != null) {
                        //赋值
                        mImageWidth = options.outWidth;
                        mImageHeight = options.outHeight;
                        //初始化图形
                        int centerW = mImageWidth / 2;
                        int centerH = mImageHeight / 2;
                        mRect.left = centerW - getMeasuredWidth() / 2;
                        mRect.right = centerW + getMeasuredWidth() / 2;
                        mRect.top = centerH - getMeasuredHeight() / 2;
                        mRect.bottom = centerH + getMeasuredHeight() / 2;
//                        postInvalidate();
                        drawImage();
                    }
                });

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //显示图片
//        if (mDecoder != null) {
//            Bitmap bm = mDecoder.decodeRegion(mRect, mOptions);
//            if (bm != null) {
//                Log.d("超大图加载", new StringBuffer("方法:onDraw\n")
//                        .append("width:[").append(bm.getWidth()).append("]\n")
//                        .append("height:[").append(bm.getHeight()).append("]\n")
//                        .append("left:[").append(mRect.left).append("]\n")
//                        .append("top:[").append(mRect.top).append("]\n")
//                        .toString());
//                canvas.drawBitmap(bm, 0, 0, null);
//            }
//        }
        super.onDraw(canvas);
    }

/////////////////————————————————————————————滑动处理———————————————————————————————/////////////////


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件交给手势控制器处理
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /*获取按下的位置*/
    @Override
    public boolean onDown(MotionEvent e) {
        mLastX = (int) e.getRawX();
        mLastY = (int) e.getRawY();
        return true;
    }

    /*滑动*/
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int x = (int) e2.getRawX();
        int y = (int) e2.getRawY();
        move(x, y);
        return true;
    }

    /*惯性滑动*/
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int x = (int) e2.getRawX();
        int y = (int) e2.getRawY();
        move(x, y);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    /*移动的时候更新图片显示的区域*/
    private void move(int x, int y) {

        int deltaX = x - mLastX;
        int deltaY = y - mLastY;

        Log.d("超大图加载", new StringBuffer("方法:move\n")
                .append("deltaX:[").append(deltaX).append("]\n")
                .append("deltaY:[").append(deltaY).append("]\n")
                .toString());
        //如果图片宽度大于屏幕宽度
        if (mImageWidth > getWidth()) {
            //移动rect区域
            mRect.offset(-deltaX, 0);
            //检查是否到达图片最右端
            if (mRect.right > mImageWidth) {
                mRect.right = mImageWidth;
                mRect.left = mImageWidth - getWidth();
            }

            //检查左端
            if (mRect.left < 0) {
                mRect.left = 0;
                mRect.right = getWidth();
            }

        }
        //如果图片高度大于屏幕高度
        if (mImageHeight > getHeight()) {
            mRect.offset(0, -deltaY);

            //是否到达最底部
            if (mRect.bottom > mImageHeight) {
                mRect.bottom = mImageHeight;
                mRect.top = mImageHeight - getHeight();
            }

            if (mRect.top < 0) {
                mRect.top = 0;
                mRect.bottom = getHeight();
            }
        }
        //重绘
//        postInvalidate();
        drawImage();

        mLastX = x;
        mLastY = y;
    }

    @SuppressLint("CheckResult")
    private void drawImage() {
        Bitmap bm = null;
        if (mDecoder != null)
            bm = mDecoder.decodeRegion(mRect, mOptions);//效率太低了

        // 通过lockCanvas加锁并得到該SurfaceView的画布
        mCanvas = surfaceHolder.lockCanvas();
        if (mCanvas != null && bm != null) {
            Log.d("超大图加载", new StringBuffer("方法:onDraw\n")
                    .append("width:[").append(bm.getWidth()).append("]\n")
                    .append("height:[").append(bm.getHeight()).append("]\n")
                    .append("left:[").append(mRect.left).append("]\n")
                    .append("top:[").append(mRect.top).append("]\n")
                    .toString());
            mCanvas.drawBitmap(bm, 0, 0, null);
            // 释放锁并提交画布进行重绘
            surfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    /*长按*/
    @Override
    public void onLongPress(MotionEvent e) {
        mLastX = (int) e.getRawX();
        mLastY = (int) e.getRawY();
    }

    /*SurfaceView已经创建*/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setImageUrl("https://i0.hdslb.com/bfs/article/b4e33d91dd31f16be7c36cefc1a77fe7b628c5d8.jpg");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /*SurfaceView已经销毁*/
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}