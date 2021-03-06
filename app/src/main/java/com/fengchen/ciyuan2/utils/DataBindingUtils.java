package com.fengchen.ciyuan2.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.StringUtil;


/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/1/26 14:09
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class DataBindingUtils {

    /*加载网络图片*/
    @BindingAdapter({"loadImageUrl", "loadImageWidth", "loadImageHeight"})
    public static void loadImageUrl(ImageView view, String url, int loadImageWidth, int loadImageHeight) {
        if (view != null && StringUtil.isImageUrl(url)) {
//            ImageParameter imageParameter = new ImageParameter(url)
//                    .setWidth(FCUtils.dp2px(loadImageWidth))
//                    .setHeight(FCUtils.dp2px(loadImageHeight));
//            RxImageLoader.with().load(imageParameter).into(view);

            //宽高为0时保持原大小
            RequestOptions options = new RequestOptions()
                    .override(loadImageWidth > 0 ? FCUtils.dp2px(loadImageWidth) : Target.SIZE_ORIGINAL,
                            loadImageHeight > 0 ? FCUtils.dp2px(loadImageHeight) : Target.SIZE_ORIGINAL);

//                    .placeholder(R.drawable.placeholder_square_z150_z150)
//                    .error(R.drawable.placeholder_square_z150_z150)
//            Glide.with(FCUtils.getContext()).asBitmap().load(url).apply(options).into(view);
            Glide.with(FCUtils.getContext()).load(url).apply(options).into(view);

            //图片地址后面加上这个可以裁剪图片
//            @1320w_824h.webp
        }
    }

    /*加载网络图片*/
    @BindingAdapter({"loadBackgroundeUrl", "loadBackgroundWidth", "loadBackgroundHeight"})
    public static void loadBackgroundeUrl(View view, String url, int loadImageWidth, int loadImageHeight) {
        if (view != null && StringUtil.isImageUrl(url)) {
//            ImageParameter imageParameter = new ImageParameter(url)
//                    .setWidth(FCUtils.dp2px(loadImageWidth))
//                    .setHeight(FCUtils.dp2px(loadImageHeight));
//            RxImageLoader.with().load(imageParameter).into(view);

            //宽高为0时保持原大小
            RequestOptions options = new RequestOptions()
                    .override(loadImageWidth > 0 ? FCUtils.dp2px(loadImageWidth) : Target.SIZE_ORIGINAL,
                            loadImageHeight > 0 ? FCUtils.dp2px(loadImageHeight) : Target.SIZE_ORIGINAL);

//                    .placeholder(R.drawable.placeholder_square_z150_z150)
//                    .error(R.drawable.placeholder_square_z150_z150)
//            Glide.with(FCUtils.getContext()).asBitmap().load(url).apply(options).into(view);
//            Glide.with(FCUtils.getContext()).load(url).apply(options).into(view);


            //图片地址后面加上这个可以裁剪图片
//            @1320w_824h.webp
        }
    }

    /*加载textview的图片，设置大小*/
    @BindingAdapter({"textDrawableWidth", "textDrawableHeight"})
    public static void initTextDrawableSize(TextView view, int textDrawableWidth, int textDrawableHeight) {
        for (Drawable drawable : view.getCompoundDrawables()) {
            if (drawable != null) {
                int width = FCUtils.dp2px(textDrawableWidth);
                int height = FCUtils.dp2px(textDrawableHeight);
                int x = (drawable.getMinimumWidth() - width) / 2;
                int y = (drawable.getMinimumWidth() - height) / 2;
                drawable.setBounds(x, y, x + width, y + height);
                view.postInvalidate();
            }
        }
    }
}
