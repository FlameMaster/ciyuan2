package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fengchen.ciyuan2.R;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/3/30 16:17
 * <p>
 * = 分 类 说 明：弥散阴影布局
 * ================================================
 */

public class DiffusionView extends View {

    private Paint mPaint;
    /*圆角大小*/
    private float mCornerRadius;
    /*阴影大小*/
    private float mShadowRadius;
    /*横向偏移量*/
    private float mDeviationX;
    /*纵向偏移量*/
    private float mDeviationY;
    /*阴影颜色*/
    private int mShadowColor;
    /*阴影中间颜色*/
    private int mShadowCenterColor;


    public void setCornerRadius(float mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
    }

    public void setShadowRadius(float shadowRadius){
        mShadowRadius = shadowRadius;
    }

    public void setShadowColor(int color){
        mShadowColor = color;
    }


    public DiffusionView(Context context) {
        this(context, null);
    }

    public DiffusionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiffusionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomAttributes(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(mShadowCenterColor);
        this.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiffusionView);
        //圆角大小
        mCornerRadius = a.getDimensionPixelSize(
                R.styleable.DiffusionView_diffusion_corner_radius, 0);
        //阴影大小
        mShadowRadius = a.getDimensionPixelSize(
                R.styleable.DiffusionView_shadow_radius, 0);
        //x偏移
        mDeviationX = a.getDimensionPixelSize(
                R.styleable.DiffusionView_deviationX, 0);
        //y偏移
        mDeviationY = a.getDimensionPixelSize(
                R.styleable.DiffusionView_deviationY, 0);
        //阴影颜色
        mShadowColor = a.getColor(
                R.styleable.DiffusionView_shadow_color, 0x00ffffff);
        //阴影内部颜色
        mShadowCenterColor = a.getColor(
                R.styleable.DiffusionView_shadow_centre_color, 0x00ffffff);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制阴影，param1：模糊半径；param2：x轴偏移量：param3：y轴偏移量；param4：阴影颜色
        mPaint.setShadowLayer(mShadowRadius, mDeviationX, mDeviationY, mShadowColor);
        //预留空间
        RectF rect = new RectF(mShadowRadius , mShadowRadius,
                getMeasuredWidth() - mShadowRadius ,
                getMeasuredHeight() - mShadowRadius );
        //画角
        canvas.drawRoundRect(rect, mCornerRadius, mCornerRadius, mPaint);

        super.onDraw(canvas);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resid) {
        super.setBackgroundResource(resid);
    }


    /**
     * 设置中间色
     * @param color
     */
    public void setShadowCenterColor(int color){
        mShadowCenterColor = color;
        mPaint.setColor(mShadowCenterColor);
        mPaint.setShadowLayer(mShadowRadius, mDeviationX, mDeviationY, mShadowColor);
        postInvalidate();
    }

}
