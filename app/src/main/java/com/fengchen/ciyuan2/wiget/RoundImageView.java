package com.fengchen.ciyuan2.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.fengchen.ciyuan2.R;
import com.fengchen.light.utils.ImageUtil;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2016/3/24 18:21
 * <p>
 * = 分 类 说 明：带圆角和两层边框的imageview
 * ================================================
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    private int mBorderThickness;
    private Context mContext;
    private int defaultColor = 0x00FFFFFF;
    /*圆角大小*/
    private int mCornerRadius;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor;//图片的外边界
    private int mBorderInsideColor;//图片的内边界
    //边框数量，0-2
    int mBorderNumber = 0;
    //大小是否固定
    private boolean mFixedSize = true;


    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        //圆角大小
        mCornerRadius = a.getDimensionPixelSize(
                R.styleable.RoundImageView_corner_radius, 0);
        //边界的宽度
        mBorderThickness = a.getDimensionPixelSize(
                R.styleable.RoundImageView_border_thickness, 0);
        //外边界的颜色
        mBorderOutsideColor = a.getColor(
                R.styleable.RoundImageView_border_outside_color, defaultColor);
        //内边界的颜色
        mBorderInsideColor = a.getColor(
                R.styleable.RoundImageView_border_inside_color, defaultColor);
        //大小是否固定，默认固定的
        mFixedSize = a.getBoolean(
                R.styleable.RoundImageView_fixed_size, true);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.RGB_565, true);

        // 保证重新读取图片后不会因为图片大小而改变控件宽、高的大小（针对宽、高为wrap_content布局的imageview，但会导致margin无效）
        // if (defaultWidth != 0 && defaultHeight != 0) {
        // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        // defaultWidth, defaultHeight);
        // setLayoutParams(params);
        // }


        if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            mBorderNumber = 2;
            // 画外圆
            drawCircleBorder(canvas, mCornerRadius, 0, mBorderOutsideColor);
            // 画内圆
            drawCircleBorder(canvas, mCornerRadius, mBorderThickness, mBorderInsideColor);
        } else if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            mBorderNumber = 1;
            drawCircleBorder(canvas, mCornerRadius, 0, mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor
                && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            mBorderNumber = 1;
            drawCircleBorder(canvas, mCornerRadius, 0, mBorderOutsideColor);
        }

        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, mCornerRadius);
        canvas.drawBitmap(roundBitmap, mBorderThickness * mBorderNumber, mBorderThickness * mBorderNumber, null);

    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius 半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {

        Bitmap scaledSrcBmp;

        //判断图片大小和控件大小是否一致
        if (bmp.getWidth() != getBitmapWidth()
                || bmp.getHeight() != getBitmapHeight()) {
            if (mFixedSize)//大小固定时直接裁剪中间图片
                bmp = ImageUtil.ImageCrop(bmp,
                        getBitmapHeight(),//长边
                        getBitmapWidth(), //短边
                        true);
            scaledSrcBmp = Bitmap.createScaledBitmap(bmp, getBitmapWidth(),
                    getBitmapHeight(), true);
            bmp.recycle();
            bmp = null;
        } else {
            scaledSrcBmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0,
                scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());//设置位置
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);// 抗锯齿
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
//                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
//                paint);//画圆
        canvas.drawRoundRect(rectF, radius, radius, paint);//画角
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
         scaledSrcBmp.recycle();
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int thicknessSize, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        RectF rect = new RectF(thicknessSize, thicknessSize,
                getWidth() - thicknessSize, getHeight() - thicknessSize);
        //画角
        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    /**
     * @return 获取图片应该拥有的宽度
     */
    private int getBitmapWidth() {
        return getWidth() - mBorderThickness * 2 * mBorderNumber;
    }

    /**
     * @return 获取图片应该拥有的高度
     */
    private int getBitmapHeight() {
        return getHeight() - mBorderThickness * 2 * mBorderNumber;
    }

}
