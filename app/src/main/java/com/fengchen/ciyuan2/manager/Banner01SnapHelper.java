package com.fengchen.ciyuan2.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;


/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/4 11:36
 * <p>
 * = 分 类 说 明：推荐列表的帮助类：滚动对齐
 * ============================================================
 */
public class Banner01SnapHelper extends SnapHelper {

    OrientationHelper mHorizontalHelper;

    /*计算的SnapView当前位置与目标位置的距离*/
    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
        out[1] = 0;
        return out;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    //targetView的start坐标与RecyclerView的paddingStart之间的差值
    //就是需要滚动调整的距离
    private int distanceToStart(View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();

    }

    /*找到当前时刻的的SnapView*/
    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.getChildCount() <= 0) return null;
        if (layoutManager instanceof Banner01Manager) {
            int firstChildPosition = ((Banner01Manager) layoutManager).findFirstVisibleItemPosition();
            View firstChildView = layoutManager.findViewByPosition(firstChildPosition);
            int direction = ((Banner01Manager) layoutManager).getScrollDirection();
            //根据滑动方向判断
            if (getHorizontalHelper(layoutManager).getDecoratedEnd(firstChildView) > 0
                    &&((direction == Banner01Manager.SCROLL_DIRECTION_RIGHT
                    && getHorizontalHelper(layoutManager).getDecoratedEnd(firstChildView)
                    >= getHorizontalHelper(layoutManager).getDecoratedMeasurement(firstChildView) * 7 / 8)
                    || (direction == Banner01Manager.SCROLL_DIRECTION_LEFT
                    && getHorizontalHelper(layoutManager).getDecoratedEnd(firstChildView)
                    >= getHorizontalHelper(layoutManager).getDecoratedMeasurement(firstChildView) * 1 / 8))
//                    getHorizontalHelper(layoutManager).getDecoratedEnd(firstChildView)
//                    >= getHorizontalHelper(layoutManager).getDecoratedMeasurement(firstChildView)/2
            ) {

                return firstChildView;
            } else {
                return layoutManager.findViewByPosition(firstChildPosition + 1);
            }
        }
        return null;
    }

    /*在触发fling时找到targetSnapPosition*/
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
//        Log.e("CardSnapHelper", "velocityX:" + velocityX);
        if (Math.abs(velocityX) > 6000)
            velocityX = velocityX / Math.abs(velocityX) * 6000;
        return -velocityX;
    }
}
