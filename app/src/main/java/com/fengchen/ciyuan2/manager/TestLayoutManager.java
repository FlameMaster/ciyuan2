package com.fengchen.ciyuan2.manager;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
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
 * = 时 间：2019/1/28 15:12
 * <p>
 * = 分 类 说 明：瀑布流
 * ============================================================
 */
public class TestLayoutManager extends RecyclerView.LayoutManager {

    /*滑动方向*/
    public final static int SCROLL_NORMALCY = 0;
    public final static int SCROLL_UP = 1;
    public final static int SCROLL_DOWN = -1;

    /*item的列数*/
    private int mCount;
    /*维护列的高度：用于判断每个item在哪行展示*/
    private int[] offsets;
    /*竖直滑动距离*/
    private int scrolls;
    /*最高一列的高度*/
    private int maxHeight;
    /*单个条目的宽度*/
    private int mEachWidth;

    /*方向帮助类*/
    private OrientationHelper mHorizontalHelper, mVerticalHelper;
    private SparseArray<View> mAttchedViews = new SparseArray<>();
    /*存储child大小的池*/
    private Pool<Rect> mChildRects = new Pool<>(new Pool.Factory<Rect>() {
        @Override
        public Rect get() {
            return new Rect();
        }
    });


    public TestLayoutManager(int count) {
        //计算大概位置
        scrolls = scrolls * mCount / count;
        mCount = count;
        offsets = new int[count];
        mChildRects.getArray().clear();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(mEachWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

////////////////////////////—————————————————————布局相关———————————————————————////////////////////////////

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0)
            detachAndScrapAttachedViews(recycler);
        if (getChildCount() == 0 && state.isPreLayout()) return;

        if (mHorizontalHelper == null)
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(this);
        if (mVerticalHelper == null)
            mVerticalHelper = OrientationHelper.createVerticalHelper(this);

        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler);
        mAttchedViews.clear();
        //每个item的宽度
        mEachWidth = mHorizontalHelper.getTotalSpace() / mCount;
        //布局
        layout(recycler, state, 0);
    }

    /*开始布局：dy 1 上滑 -1 下滑 0初始*/
    private void layout(RecyclerView.Recycler recycler, RecyclerView.State state, int scrollDirection) {
        Rect layoutRange = new Rect(
                getPaddingLeft(),
                getPaddingTop() + scrolls,
                mHorizontalHelper.getTotalSpace() + getPaddingRight(),
                mVerticalHelper.getTotalSpace() + getPaddingTop() + scrolls);
        //child数量
        int itemCount = state.getItemCount();
        if (scrollDirection == SCROLL_NORMALCY || scrollDirection == SCROLL_UP) {//初始化和往上滑
            dolayoutAndRecycler(recycler, layoutRange, itemCount);
        } else {//手指往下滑，前面的view出现
            dolayoutAndRecyclerDown(recycler, layoutRange, itemCount);
        }
    }

    private void dolayoutAndRecyclerDown(RecyclerView.Recycler recycler, Rect layoutRange,
                                         int itemCount) {
        //屏幕可见数量
        int childCount = getChildCount();
        //获取屏幕中child最大的位置：向下滑动最大值时刻在减小
        int maxPosition = getMaxPosition(childCount);
        //回收不在屏幕范围的child
        recyclerViews(recycler, layoutRange);
        int xx = 0;
        for (int i = maxPosition; i >= 0; i--) {
            //获取当前child的矩阵大小
            Rect layout = mChildRects.get(i);
            //判断当前矩阵是否在显示区
            if (Rect.intersects(layout, layoutRange)
                    && mAttchedViews.get(i) == null) {
                View viewForPosition = recycler.getViewForPosition(i);
                addView(viewForPosition);
                //添加到集合中
                mAttchedViews.put(i, viewForPosition);
                //测量child
                measureChildWithMargins(viewForPosition, mEachWidth * (mCount - 1), 0);
                //将ViewLayout出来，显示在屏幕上，内部会自动追加上该View的ItemDecoration和Margin。此时我们的View已经可见了
                layoutDecoratedWithMargins(viewForPosition, layout.left, layout.top - scrolls, layout.right, layout.bottom - scrolls);
            }
            //判断child的位置
            if (layout.bottom <= layoutRange.top) {
                xx++;
                if (xx >= mCount) {
                    break;
                }
            }
        }
    }

    /*布局child*/
    private void dolayoutAndRecycler(RecyclerView.Recycler recycler, Rect layoutRange,
                                     int itemCount) {
        //屏幕可见数量
        int childCount = getChildCount();
        //获取屏幕中最小的位置：由于向上滑最小位置时刻在增大
        int minPosition = getMinPosition(childCount);

        //回收不在屏幕范围的child
        recyclerViews(recycler, layoutRange);
        int xx = 0;
        A:
        for (int i = minPosition; i < itemCount; i++) {
            if (mChildRects.get(i).isEmpty()) {
                mChildRects.getArray().remove(i);
                caculate(recycler, 0);
            }
            //获取当前child的矩阵大小
            final Rect layout = getRect(recycler, i);
            //判断当前矩阵是否在显示区
            if (Rect.intersects(layout, layoutRange) && mAttchedViews.get(i) == null) {
                View viewForPosition = recycler.getViewForPosition(i);
                addView(viewForPosition);
                //添加到集合
                mAttchedViews.put(i, viewForPosition);
                //测量
                measureChildWithMargins(viewForPosition, mEachWidth * (mCount - 1), 0);
                //将ViewLayout出来，显示在屏幕上，内部会自动追加上该View的ItemDecoration和Margin。此时我们的View已经可见了
                layoutDecoratedWithMargins(viewForPosition, layout.left, layout.top - scrolls, layout.right, layout.bottom - scrolls);
            }
            if (layout.top >= layoutRange.bottom) {
                xx++;
                if (xx >= mCount) {
                    break A;//直接跳出A标记的for循环
                }
            }
        }
    }

    /*预加载：用于计算*/
    private void caculate(final RecyclerView.Recycler recycler, int dy) {
        S:
        for (int i = mChildRects.size(); i < getItemCount(); i++) {
            //之测量不同type的大小 计算位置
            View scrap = recycler.getViewForPosition(i);
            addView(scrap);
            measureChildWithMargins(scrap, mEachWidth * (mCount - 1), 0);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(scrap);
            removeAndRecycleView(scrap, recycler);
            int rowNumber = getMinIndex();
            Rect rect = mChildRects.get(i);
            rect.set(rowNumber * mEachWidth, offsets[rowNumber], (rowNumber + 1) * mEachWidth, offsets[rowNumber] + decoratedMeasuredHeight);
            offsets[rowNumber] = offsets[rowNumber] + rect.height();
            /*只多加载一屏幕的*/
            if (offsets[getMinIndex()] > dy + scrolls + getPaddingTop() + mVerticalHelper.getTotalSpace()) {
                break S;
            }

        }
        maxHeight = getMaxHeight();
    }

    /*回收不在屏幕范围的view*/
    private void recyclerViews(RecyclerView.Recycler recycler, Rect layoutrect) {
        //屏幕可见数量
        int childCount = getChildCount();
        //循环回收
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            //获得child的范围
            Rect rect = mChildRects.get(position);
            //判断两个矩形位置是否相交：即当前子类是否还在显示范围内
            if (!Rect.intersects(rect, layoutrect)) {
                //不在显示范围的
                mAttchedViews.remove(position);
                removeAndRecycleView(childAt, recycler);
                childCount--;
            }
        }
    }


////////////////////////////—————————————————————滑动相关———————————————————————////////////////////////////

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mChildRects.size() < getItemCount()
                && maxHeight <= dy + scrolls + getPaddingTop() + mVerticalHelper.getTotalSpace()) {
            caculate(recycler, dy);
        }

        if (maxHeight < mVerticalHelper.getTotalSpace()) {
            return 0;
        }
        if (scrolls + dy > maxHeight - mVerticalHelper.getTotalSpace()) {
            dy = maxHeight - mVerticalHelper.getTotalSpace() - scrolls;
        }

        if (scrolls + dy < 0) {
            dy = -scrolls;
        }
        offsetChildrenVertical(-dy);
        scrolls += dy;
        if (dy > 0) {
            layout(recycler, state, SCROLL_UP);
        } else if (dy < 0) {
            layout(recycler, state, SCROLL_DOWN);
        }
        return dy;
    }


//    @Override
//    public Parcelable onSaveInstanceState() {
//        Parcelable parcelable = super.onSaveInstanceState();
//        Mystate mystate = new Mystate(parcelable).setPosition(scrolls);
//        return mystate;
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//
//        Mystate state1 = (Mystate) state;
//        super.onRestoreInstanceState(state1.getSuperState());
//        scrolls = state1.getPosition();
//
//    }

////////////////////////////—————————————————————工具方法———————————————————————////////////////////////////

    /*获取屏幕中在adapter中位置最小的值：由于是瀑布流布局，所以需要全部遍历比较*/
    private int getMinPosition(int childCount) {
        int min = 0;
        if (childCount != 0) {
            //获取当前可见第一个child在adapter中的位置
            min = getPosition(getChildAt(0));
            //遍历所有child
            for (int i = 1; i < childCount; i++) {
                int position = getPosition(getChildAt(i));
                if (position < min) {
                    min = position;
                }
            }
        }
        return min;
    }

    /*获取屏幕中在adapter中位置最大的值：由于是瀑布流布局，所以需要全部遍历比较*/
    private int getMaxPosition(int childCount) {
        int max = 0;
        if (childCount != 0) {
            //获取当前可见第一个child在adapter中的位置
            max = getPosition(getChildAt(0));
            //遍历所有child
            for (int i = 1; i < childCount; i++) {
                int position = getPosition(getChildAt(i));
                if (position > max) {
                    max = position;
                }
            }
        }
        return max;
    }

    /*获取最短的列：用于item续写位置*/
    private int getMinIndex() {
        int min = 0;
        int minnum = offsets[0];
        //循环获取最短的列
        for (int i = 1; i < offsets.length; i++) {
            if (minnum > offsets[i]) {
                minnum = offsets[i];
                min = i;
            }
        }
        //返回最短的列的序号
        return min;
    }

    /*获取最高的列的高度*/
    private int getMaxHeight() {
        int max = offsets[0];
        for (int i = 1; i < offsets.length; i++) {
            if (offsets[i] > max) {
                max = offsets[i];
            }
        }
        return max;
    }

    /*获取child的矩阵范围*/
    public Rect getRect(RecyclerView.Recycler recycler, int position) {
        Rect rectx = mChildRects.get(position);
        return rectx;
    }


////////////////////////////—————————————————————私有类———————————————————————////////////////////////////

    private static class Mystate implements Parcelable {
        int position;
        Parcelable superState;

        public Mystate setPosition(int position) {
            this.position = position;
            return this;
        }

        public Parcelable getSuperState() {
            return superState;
        }

        public void setSuperState(Parcelable superState) {
            this.superState = superState;
        }

        public int getPosition() {
            return position;
        }

        public Mystate(Parcelable superState) {
            this.superState = superState;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.position);
            dest.writeParcelable(this.superState, flags);
        }

        protected Mystate(Parcel in) {
            this.position = in.readInt();
            this.superState = in.readParcelable(Parcelable.class.getClassLoader());
        }

        public static final Parcelable.Creator<Mystate> CREATOR = new Parcelable.Creator<Mystate>() {
            @Override
            public Mystate createFromParcel(Parcel source) {
                return new Mystate(source);
            }

            @Override
            public Mystate[] newArray(int size) {
                return new Mystate[size];
            }
        };
    }

    private static class Pool<T> {
        SparseArray<T> array;
        Factory<T> tnew;

        public Pool(Factory<T> tnew) {
            array = new SparseArray<>();
            this.tnew = tnew;
        }

        public int size() {
            return array.size();
        }

        public SparseArray<T> getArray() {
            return array;
        }

        public T get(int key) {
            T t = array.get(key);
            if (t == null) {
                t = tnew.get();
                array.put(key, t);
            }
            return t;
        }

        public interface Factory<T> {
            T get();
        }
    }
}
