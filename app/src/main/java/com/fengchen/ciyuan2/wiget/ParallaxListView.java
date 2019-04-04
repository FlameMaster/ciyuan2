package com.fengchen.ciyuan2.wiget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;

import com.fengchen.light.utils.FCUtils;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/3/1 17:45
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class ParallaxListView extends RecyclerView {

    /*最大高度*/
    private int maxHeight;
    /*控件*/
    private View mParallaxView;
    /*最初的高度*/
    private int orignalHeight;
    /*方向*/
    private int mOrientation;

    public ParallaxListView(@NonNull Context context) {
        super(context);
    }

    public ParallaxListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    //    LinearLayoutManager.HORIZONTAL
    public void setParallaxView(final View view, int orientation, int maxLength) {
        this.mParallaxView = view;
        //设定最大高度
        mParallaxView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mParallaxView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                orignalHeight = mParallaxView.getHeight();
                maxHeight = maxLength;
                mOrientation = orientation;
                Log.e("tag", "orignalHeight: " + orignalHeight);
            }
        });

    }

    /**
     * 在listview滑动到头的时候执行，可以获取到继续滑动的距离和方向
     * deltaX：继续滑动x方向的距离
     * deltaY：继续滑动y方向的距离     负：表示顶部到头   正：表示底部到头
     * maxOverScrollX:x方向最大可以滚动的距离
     * maxOverScrollY：y方向最大可以滚动的距离
     * isTouchEvent: true: 是手指拖动滑动     false:表示fling靠惯性滑动;
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        if (deltaY < 0 && isTouchEvent) {
            //表示顶部到头，并且是手动拖动到头的情况
            //我们需要不断的增加View的高度
            if (mParallaxView != null) {

                //力度减弱
                int newHeight = mParallaxView.getHeight() - deltaY / 2;
                if (newHeight > maxHeight) newHeight = maxHeight;

                mParallaxView.getLayoutParams().height = newHeight;
                //使mParallaxView的布局参数生效
                mParallaxView.requestLayout();
            }
        }
Log.e("fff","333");
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            //需要将mParallaxView的高度缓慢恢复到最初高度
            ValueAnimator animator = ValueAnimator.ofInt(mParallaxView.getHeight(), orignalHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {

                    //获取动画的值，设置给mParallaxView
                    int animatedValue = (Integer) animator.getAnimatedValue();
                    mParallaxView.getLayoutParams().height = animatedValue;

                    //使mParallaxView的布局参数生效
                    mParallaxView.requestLayout();
                }
            });

            //弹性的插值器
            animator.setInterpolator(new OvershootInterpolator(3));
            animator.setDuration(300);
            animator.start();
        }
        return super.onTouchEvent(ev);
    }

}
