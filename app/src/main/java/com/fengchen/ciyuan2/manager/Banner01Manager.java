package com.fengchen.ciyuan2.manager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/7/31 17:49
 * <p>
 * = 分 类 说 明：第4版首页的轮播图
 * ============================================================
 */
public class Banner01Manager extends RecyclerView.LayoutManager {
    //总的滑动距离
    private int mScrolls;
    //当前位置
    private int nowPosition;
    /*滑动速度*/
    private float MILLISECONDS_PER_INCH = 0.7f;
    //滑动方向，0右1左
    private int mScrollDirection;
    public final static int SCROLL_DIRECTION_RIGHT = 0;
    public final static int SCROLL_DIRECTION_LEFT = 1;

    public Banner01Manager() {
        nowPosition = 0;
        mScrolls = 0;
        mScrollDirection = SCROLL_DIRECTION_RIGHT;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        Log.e("onLayoutChildren", "onLayoutChildren() called with: recycler = [" + recycler + "], state = [" + state + "]");
        if (state.getItemCount() == 0 || state.isPreLayout()) return;
        removeAndRecycleAllViews(recycler);
//        measure(recycler);
        onLayout(recycler, state);
    }

    private void onLayout(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();
        if (itemCount < 1) return;
        nowPosition = mScrolls / getWidth() + 1;
        if (nowPosition > getItemCount() - 1) nowPosition = getItemCount() - 1;
        //回收
        recyclerViews(recycler);
        for (int position = nowPosition > 0 ? (nowPosition - 1) : nowPosition; position >= 0 && position <= nowPosition; position++) {
//        int position = 1;
            View child = recycler.getViewForPosition(position);
            creation(child);
            layout(child, position);
        }

    }

    /*测量并赋予初始位置*/
    private void creation(View child) {
        addView(child);
        measureChildWithMargins(child, 0, 0);
        int widthSpace = getWidth() - getDecoratedMeasuredWidth(child);
        int heightSpace = getHeight() - getDecoratedMeasuredHeight(child);
        //我们在布局时，将childView居中处理，这里也可以改为只水平居中
        layoutDecoratedWithMargins(child, widthSpace / 2, heightSpace / 2,
                widthSpace / 2 + getDecoratedMeasuredWidth(child),
                heightSpace / 2 + getDecoratedMeasuredHeight(child));
    }

    /*控制位置*/
    private void layout(View child, int position) {
        if (position == nowPosition) {
            child.setAlpha(1);
            child.setScaleX(1);
            child.setScaleY(1);
            child.setTranslationX(-mScrolls + position * getWidth());
        } else if (position == nowPosition - 1) {
            float ratio = 1 - (float) (mScrolls % getWidth()) / (float) getWidth();
            ratio = 0.8f + ratio * 0.2f;
//            Log.d("layout", "\r\tposition=" + position +"\r\tmScrolls=" + mScrolls + "\r\tratio=" + ratio);
            child.setAlpha(1);
            child.setTranslationX(0);
            child.setScaleX(ratio);
            child.setScaleY(ratio);
        } else {
            child.setAlpha(0);
        }
    }


    /*回收不在屏幕范围的view*/
    private void recyclerViews(RecyclerView.Recycler recycler) {
        detachAndScrapAttachedViews(recycler);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);
            if (position > nowPosition || position < nowPosition - 1)
                removeAndRecycleView(child, recycler);
        }
    }

    ////////////////////////////—————————————————————滑动相关———————————————————————////////////////////////////


    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        Log.d("曹尼玛1", "dx=" + dx + "\r\tmScrolls=" + mScrolls + "\r\twidth=" + getWidth() + "\r\tItemCount=" + getItemCount());
        //数值限制
        if (mScrolls + dx < 0)
            dx = 0 - mScrolls;
        int itemcount = getItemCount();
        if (itemcount < 100 && mScrolls + dx > (itemcount - 1) * getWidth())
            dx = (itemcount - 1) * getWidth() - mScrolls;

        //滑动方向
        if (Math.abs(dx) > 1) {
            if (dx > 0) mScrollDirection = SCROLL_DIRECTION_RIGHT;
            else mScrollDirection = SCROLL_DIRECTION_LEFT;
        }
        mScrolls += dx;
        onLayout(recycler, state);
        //用来限制速度
        if (Math.abs(dx) > 1)
            return dx / 2;
        else return dx;
    }

    //寻找当前第一个位置的控件
    public int findFirstVisibleItemPosition() {
        if (nowPosition > 0) return nowPosition - 1;
        else return nowPosition;
    }


    ////////////////////////////—————————————————————控制自动滑动速度———————————————————————////////////////////////////

    /*获取当前滑动方向*/
    public int getScrollDirection() {
        return mScrollDirection;
    }

    ////////////////////////////—————————————————————定位相关———————————————————————////////////////////////////

    @Override
    public int getDecoratedLeft(@NonNull View child) {
        if (getPosition(child) == nowPosition)
            return -mScrolls % getWidth() + getWidth();
        return -mScrolls % getWidth();
    }

    @Override
    public int getDecoratedRight(@NonNull View child) {
        return getDecoratedLeft(child) + getWidth();
    }

    @Override
    public int getDecoratedMeasuredWidth(@NonNull View child) {
        return super.getDecoratedMeasuredWidth(child);
    }


    ////////////////////////////—————————————————————其它———————————————————————////////////////////////////

    public int getScrolls() {
        return mScrolls;
    }

    public int getNowPosition() {
        return nowPosition;
    }
}
