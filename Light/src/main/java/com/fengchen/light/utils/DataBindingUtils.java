package com.irock.ningxiataxbureau.officeautomation.utils;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.StringUtil;
import com.irock.ningxiataxbureau.officeautomation.R;

import androidx.databinding.BindingAdapter;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：melvinhou@163.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/12/11 21:25
 * <p>
 * = 分 类 说 明：通用的绑定数据工具类
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

            RequestOptions options = new RequestOptions()
                    .override(loadImageWidth > 0 ? FCUtils.dp2px(loadImageWidth) : -1,
                            loadImageHeight > 0 ? FCUtils.dp2px(loadImageHeight) : -1)
                    .placeholder(R.mipmap.icon_hint_empty)
                    .error(R.mipmap.icon_hint_empty)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(FCUtils.getContext()).asBitmap().load(url).apply(options).into(view);
//            Glide.with(FCUtils.getContext()).load(url).apply(options).into(view);
        }
    }



    /*加载textview的图片，设置大小*/
    @BindingAdapter({"drawableId", "type", "textDrawableWidth", "textDrawableHeight"})
    public static void loadTextDrawable(TextView view,
                                        Drawable drawableId, int type,
                                        int textDrawableWidth, int textDrawableHeight) {
        if (drawableId == null || type < 0 || type > 3) return;
//        Drawable drawable = FCUtils.getDrawable(drawableId);
        drawableId.setBounds(0, 0, FCUtils.dp2px(textDrawableWidth), FCUtils.dp2px(textDrawableHeight));
        FCUtils.runOnUIThread(() -> {
            view.setCompoundDrawables(
                    type == 0 ? drawableId : null,
                    type == 1 ? drawableId : null,
                    type == 2 ? drawableId : null,
                    type == 3 ? drawableId : null);
            if (drawableId instanceof Animatable)
                ((Animatable) drawableId).start();
        });
    }

    /*加载textview的图片，设置大小*/
    @BindingAdapter({"textDrawableWidth", "textDrawableHeight"})
    public static void initTextDrawableSize(TextView view, int textDrawableWidth, int textDrawableHeight) {
        for (int i = 0; i < view.getCompoundDrawables().length; i++) {
            Drawable drawable = view.getCompoundDrawables()[i];
//        for (Drawable drawable : view.getCompoundDrawables()) {
            if (drawable != null) {
                int width = FCUtils.dp2px(textDrawableWidth);
                int height = FCUtils.dp2px(textDrawableHeight);
                int x = (drawable.getMinimumWidth() - width) / 2;
                int y = (drawable.getMinimumHeight() - height) / 2;
                if (i <= 1)
                    drawable.setBounds(x, y, x + width, y + height);
                else if (i <= 2)
                    drawable.setBounds(0, y, width, y + height);
                else if (i <= 3)
                    drawable.setBounds(x, y, x + width, y + height);
                else
                    drawable.setBounds(x, 0, x + width, height);
                view.postInvalidate();
            }
        }
    }
}
