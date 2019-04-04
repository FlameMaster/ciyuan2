package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengchen.light.utils.FCUtils;

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
 * = 分 类 说 明：自定义的指示器，多功能
 * ============================================================
 */
public abstract class AllPagerIndicator<D> extends LinearLayout {

    /*列表对应数据*/
    List<D> mTitlesData;
    /*条目点击监听*/
    IndicatorItemClickListener<D> mItemClickListener;

    public AllPagerIndicator() {
        this(FCUtils.getContext(), null);
    }

    public AllPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

/////////////////———————————————————————添加title——————————————————————————————————————/////////////////

    /*添加一条数据*/
    public void addTitleData(List<D> datas) {
        for (D data : datas)
            addTitleData(data);
    }

    /*添加多条数据*/
    public void addTitleData(D d) {
        mTitlesData.add(d);
        addTab(mTitlesData.size() - 1);
    }


    /*添加一个标题*/
    private void addTab(int position) {
        TextView tv = newTitleView(mTitlesData.get(position));
        //点击事件
        tv.setOnClickListener(v -> {
            if (mItemClickListener != null)
                mItemClickListener.onItemClickListener(position, mTitlesData.get(position));
        });
        //添加进布局
        addView(tv);
    }

    /*新建一个tab*/
    abstract TextView newTitleView(D data);

/////////////////—————————————————————————————————————————————————————————————/////////////////


    /*设置点击监听*/
    public void setmItemClickListener(IndicatorItemClickListener<D> mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /*点击监听接口*/
    interface IndicatorItemClickListener<D> {
        void onItemClickListener(int position, D data);
    }
}
