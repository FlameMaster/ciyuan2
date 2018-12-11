package com.fengchen.light.http;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.utils.FCUtils;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/11/30  18:02
 * <p>
 * = 分 类 说 明：图片加载类
 * ============================================================
 */
public class ImageLoader {


    /*单例对象*/
    private static ImageLoader singleton;

    private ImageLoader() {
    }

    /*获取单例对象*/
    public static ImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (ImageLoader.class) {
                if (singleton == null) {
                    singleton = new ImageLoader();
                }
            }
        }
        return singleton;
    }

    /**
     * 加载图片
     * @param parameter
     */
    public void load(ImageParameter parameter){

    }

    public void loadimage(){
        ImageView view =new ImageView(FCUtils.getContext());
        Glide
                .with(FCUtils.getContext())
                .load("")
                .into(view);
    }



}
