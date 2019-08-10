package com.fengchen.ciyuan2.wiget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.fengchen.ciyuan2.R;
import com.fengchen.light.utils.FCUtils;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/5/24 15:04
 * <p>
 * = 分 类 说 明：实现觅上我的页面滑动
 * ============================================================
 */
public class MsMySideLayout extends ViewGroup implements NestedScrollingParent2 {

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    //所需控件
    private View mUserPhoto, mCard, mHead, mIndicator, mContainer, mRoot;
    //滑动差值,mSideContainer(mHead,mIndicator,mContainer)
    private int mSideContainerInitialTop, mSideContainerMinTop, mSideContainerTop;
    private boolean isSide;

    //某布局高度，下滑最大值(会回弹)
    int headheight, maxDownSideHeight;
    //手指是否抬起,是否有惯性滑动
    boolean isFingerUp, isSideFling;
    ValueAnimator animator;

    public MsMySideLayout(Context context) {
        this(context, null);
    }

    public MsMySideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mSideContainerInitialTop = 0;
        mSideContainerTop = 0;
        isSide = false;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);


        //初始化一下值
        isFingerUp = true;
        isSideFling = false;
        headheight = dp2px(300);
        maxDownSideHeight = dp2px(100);
        mSideContainerInitialTop = dp2px(110 + 80);
        mSideContainerTop = dp2px(110 + 80);
        mSideContainerMinTop = dp2px(46) - headheight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量所有子控件的宽和高,只有先测量了所有子控件的尺寸，后面才能使用child.getMeasuredWidth()
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //调用系统的onMeasure一般是测量自己(当前ViewGroup)的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //初始化控件
//        mRoot = findViewById(R.id.container);
        mUserPhoto = findViewById(R.id.side_photo);
        mCard = findViewById(R.id.side_card);
        mHead = findViewById(R.id.side_head);
        mIndicator = findViewById(R.id.side_normal);
        mContainer = findViewById(R.id.side_container);

        //判断是否允许滑动
        isSide = mHead != null;
//        //初始化高度
//        if (isSide) mSideContainerInitialTop = mHead.getMeasuredHeight();
//        //重新初始化
//        int conHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
//                getMeasuredHeight() - (mNormal != null ? mNormal.getMeasuredHeight() : 0), MeasureSpec.EXACTLY);
//        mContainer.measure(widthMeasureSpec, conHeightMeasureSpec);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        moveLayout();

    }

    private void layoutChild(final View child, int left, int top) {
        final int width = child.getMeasuredWidth();
        final int height = child.getMeasuredHeight();
        child.layout(left, top, left + width, top + height);
    }

    private void layoutChild(final View child, int left, int top, int right, int bottom) {
        child.layout(left, top, right, bottom);
    }

    /*改变位置*/
    protected void moveLayout() {
        //头像
        int photoSize = 50;
        int photoMargin = 28;
        if (mSideContainerTop < mSideContainerInitialTop) {
            photoMargin -= (mSideContainerInitialTop - mSideContainerTop) / 16;
            photoSize -= ((float) mSideContainerInitialTop - (float) mSideContainerTop) / 16f;
            if (photoMargin < 8) photoMargin = 8;
            if (photoSize < 30) photoSize = 30;
        }
        layoutChild(mUserPhoto, dp2px(photoMargin), dp2px(photoMargin), dp2px(photoMargin + photoSize), dp2px(photoMargin + photoSize));
        float cardF = ((float) mSideContainerTop - (float) mSideContainerInitialTop) / (float) maxDownSideHeight;
        //背景中的卡片
        int cardT = 110;
        int cardB = 110 + 240;
        if (cardF > 0) {
            cardT -= cardF * 30;
            cardB -= cardF * 30;
        }
        layoutChild(mCard, dp2px(20), dp2px(cardT), getRight() - dp2px(20), dp2px(cardB));
        //底部组合
//        layoutChild(mRoot,dp2px(0), mSideContainerTop);
        layoutChild(mHead, dp2px(0), mSideContainerTop);
        layoutChild(mIndicator, dp2px(0), mSideContainerTop + headheight);
        layoutChild(mContainer, dp2px(0), mSideContainerTop + headheight + dp2px(60));
        postInvalidate();
    }


//////////////////////————————————————————嵌套滑动——————————————————————//////////////////////

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
//        type 1是滚动  0是滑动
        return isSide;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);

        Log.e("停止滚动", "type=" + type + "\tisFingerUp=" + isFingerUp + "\tisSideFling=" + isSideFling + "\tmSideContainerTop=" + mSideContainerTop);
        //当滑动超过初始位置的时候，滑回去
        if (isFingerUp) {
            if (mSideContainerTop > mSideContainerInitialTop) {
                if (type == 0)
//                    ((NestedScrollView) mContainer).fling(3000);
//                    FCUtils.showToast("归位");
                    fc();
            }
        } else {
            //手指按下时初始化惯性滑动类型
            isSideFling = false;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.d("后于子类之前滑动", "dyConsumed=" + dyConsumed + "\tdyUnconsumed=" + dyUnconsumed + "\ttype=" + type);
        mSideContainerTop -= dyUnconsumed;
        if (mSideContainerTop > mSideContainerInitialTop + maxDownSideHeight) {
            mSideContainerTop = mSideContainerInitialTop + maxDownSideHeight;
            if (type == 1 && mSideContainerTop == mSideContainerInitialTop + maxDownSideHeight) {
                ((NestedScrollView) mContainer).stopNestedScroll(1);
                fc();
            }
        }
        if (mSideContainerTop < mSideContainerMinTop) mSideContainerTop = mSideContainerMinTop;
        moveLayout();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.d("先于子类之前滑动", "dy=" + dy + "\ttype=" + type);
        //不拦截向上滑
        if (dy <= 0) return;
        consumed[1] = dy;
        mSideContainerTop -= dy;

        if (mSideContainerTop > mSideContainerInitialTop + maxDownSideHeight) {
            consumed[1] = 0;
            mSideContainerTop = mSideContainerInitialTop + maxDownSideHeight;
        }
        if (mSideContainerTop < mSideContainerMinTop) {
            consumed[1] = 0;
            mSideContainerTop = mSideContainerMinTop;
        }
        moveLayout();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (mSideContainerTop > mSideContainerInitialTop) return true;
        //惯性滑动之前
        Log.e("惯性滚动.停止滚动", "velocityY=" + velocityY);
        isSideFling = true;
        float sy = Math.abs(velocityY) > 6000 ? (velocityY / Math.abs(velocityY) * 6000) : velocityY;
        ((NestedScrollView) mContainer).fling((int) sy);
        return true;
//        return super.onNestedPreFling(target, velocityX, sy);
    }

    //////////////////////————————————————————其它——————————————————————//////////////////////

    private int dp2px(int dp) {
        return FCUtils.dp2px(dp);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.e("手指抬起.停止滚动", "mSideContainerTop=" + mSideContainerTop);
                isFingerUp = true;
                break;
            case MotionEvent.ACTION_DOWN:
                Log.e("手指按下.停止滚动", "mSideContainerTop=" + mSideContainerTop);
                isFingerUp = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void fc() {
        final float size = mSideContainerTop - mSideContainerInitialTop;
        float time = size / (float) maxDownSideHeight * 3000f;
        Log.e("回弹.停止滚动", "size=" + size + "\ttime=" + time);
        ValueAnimator animator = ValueAnimator.ofInt(0, (int) size);
        animator.addUpdateListener(animator1 -> {
            mSideContainerTop -= (Integer) animator1.getAnimatedValue();
            if (mSideContainerTop > mSideContainerInitialTop + maxDownSideHeight) {
                mSideContainerTop = mSideContainerInitialTop + maxDownSideHeight;
            }
            if (mSideContainerTop < mSideContainerInitialTop) {
                mSideContainerTop = mSideContainerInitialTop;
            }
            moveLayout();
            if (mSideContainerTop == mSideContainerInitialTop)
                animator1.cancel();
        });
        //弹性的插值器
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(Float.valueOf(time).longValue());
        animator.start();
    }
}
