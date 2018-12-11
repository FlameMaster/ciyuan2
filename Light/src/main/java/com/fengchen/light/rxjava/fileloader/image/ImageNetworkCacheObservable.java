package com.fengchen.light.rxjava.fileloader.image;

import android.graphics.Bitmap;

import com.fengchen.light.rxjava.fileloader.NetworkCacheObservable;
import com.fengchen.light.model.BitmapCacheModel;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.utils.ImageUtil;
import com.fengchen.light.utils.StringUtil;

import java.io.InputStream;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/9/10 16:42
 * <p>
 * = 分 类 说 明：网络图片的请求类
 * ================================================
 */

public class ImageNetworkCacheObservable extends NetworkCacheObservable<BitmapCacheModel,Bitmap,ImageParameter> {

    @Override
    public BitmapCacheModel getDataFromCache(ImageParameter imageParameter) {
        String key=StringUtil.toCacheKey(imageParameter);
        BitmapCacheModel cacheModel = new BitmapCacheModel(key);
        cacheModel.setCacheValue(getCache(imageParameter.getUrl()));
        return cacheModel;
    }

    @Override
    public Bitmap input2Cache(InputStream inputStream) {
        return ImageUtil.decodeBitmapFromStream(inputStream,-1,-1);
    }
}
