package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fengchen.light.utils.FCUtils;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/2/26 14:57
 * <p>
 * = 分 类 说 明：内部可移动的的布局
 * ============================================================
 */
public class ParallaxLayout extends RoundLayout{

    int[] layoutParams= new int[4];

    public ParallaxLayout(Context context) {
        super(context);
    }

    public ParallaxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        layoutParams[0]=l;
        layoutParams[1]=getChildAt(0).getTop()-getChildAt(0).getHeight()/4;
        layoutParams[2]=r;
        layoutParams[3]=getChildAt(0).getBottom()-getChildAt(0).getHeight()/4;
    }

    public void moveChild(int height){
        int[] location = new int[2] ;
//        getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        getChildAt(0).getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
//        -height/2+getHeight()/2表示控件在屏幕中心的位置
        int dy = location[1]-height/2+getHeight()/2;
        dy=dy/3;
        if (dy>getChildAt(0).getHeight()/4)
            dy=getChildAt(0).getHeight()/4;
        if (-dy>getChildAt(0).getHeight()/4)
            dy=-getChildAt(0).getHeight()/4;
        Log.e("滑动ing...","屏幕高度:"+height+"\t\t坐标:"+location[1]+"\t\t位移:"+dy);
//        getChildAt(0).layout(
//                getChildAt(0).getLeft(),
//                layoutParams[1]+dy,
//                getChildAt(0).getRight(),
//                layoutParams[3]+dy);
//        postInvalidate();
        getChildAt(0).setTranslationY(dy);
    }
}
