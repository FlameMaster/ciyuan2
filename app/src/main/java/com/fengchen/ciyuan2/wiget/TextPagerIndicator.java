package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/2/28 14:16
 * <p>
 * = 分 类 说 明：文字适配器
 * ============================================================
 */
public class TextPagerIndicator extends AllPagerIndicator<String> {
    //tab宽高
    int mTabWidth,mTabHeight;
    //tab字体大小
    float mTabTextSize;
    //tab字体颜色
    Color mTabTextColor;

    /*新建一个标题*/
    protected TextView newTitleView(String title) {
        TextView tv = new TextView(getContext());
        //初始化大小
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setPadding(0, 0, 0, 0);
        //初始化样式
        tv.setText(title);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return tv;
    }


}
