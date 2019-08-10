package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fengchen.ciyuan2.R;


/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/4/27 19:38
 * <p>
 * = 分 类 说 明：头部可滑动的布局：简单的滑动嵌套模型
 * ============================================================
 */
public class TopSideLayout extends ViewGroup implements NestedScrollingParent2 {


    /*不使用也可以，只是帮助保存了滑动方向*/
    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    /*头部控件，不变的中间层,主容器*/
    private View mHead, mNormal, mContainer;
    /*头部高度，被隐藏的高度*/
    private int mHeadHeight, mHiddenHeight;
    /*是否启动嵌套滑动*/
    private boolean isSide;


    public TopSideLayout(Context context) {
        this(context, null);
    }

    public TopSideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHeadHeight = 0;
        mHiddenHeight = 0;
        isSide = false;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量所有子控件的宽和高,只有先测量了所有子控件的尺寸，后面才能使用child.getMeasuredWidth()
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //调用系统的onMeasure一般是测量自己(当前ViewGroup)的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //初始化控件
        mHead = findViewById(R.id.side_head);
        mNormal = findViewById(R.id.side_normal);
        mContainer = findViewById(R.id.side_container);

        //判断是否允许滑动
        isSide = mHead != null;
        //初始化高度
        if (isSide) mHeadHeight = mHead.getMeasuredHeight();
        //重新初始化
        int conHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - (mNormal != null ? mNormal.getMeasuredHeight() : 0), MeasureSpec.EXACTLY);
        mContainer.measure(widthMeasureSpec, conHeightMeasureSpec);
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

    /*改变位置*/
    protected void moveLayout() {
        if (mHead != null)
            layoutChild(mHead, 0, - mHiddenHeight);
        if (mNormal != null)
            layoutChild(mNormal, 0, mHeadHeight - mHiddenHeight);
        int containerTop =  mHeadHeight - mHiddenHeight+(mNormal != null ? mNormal.getMeasuredHeight() : 0);
        layoutChild(mContainer,0, containerTop);
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
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        mHiddenHeight += dyUnconsumed;
        if (mHiddenHeight > mHeadHeight) mHiddenHeight = mHeadHeight;
        if (mHiddenHeight < 0) mHiddenHeight = 0;
        moveLayout();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        //不拦截向上滑
        if (dy <= 0) return;
        consumed[1] = dy;
        mHiddenHeight += dy;
        if (mHiddenHeight > mHeadHeight) {
            consumed[1] = 0;
            mHiddenHeight = mHeadHeight;
        }
        if (mHiddenHeight < 0) {
            consumed[1] = 0;
            mHiddenHeight = 0;
        }
        moveLayout();
    }
}
