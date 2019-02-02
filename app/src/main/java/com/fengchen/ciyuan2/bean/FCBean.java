package com.fengchen.ciyuan2.bean;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/13 16:24
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class FCBean {


    MutableLiveData<String> liveData = new MutableLiveData<>();

    ObservableField<String > obData = new ObservableField<>();

    public MutableLiveData<String> getLiveData() {
        return liveData;
    }

    public ObservableField<String> getObData() {
        return obData;
    }
}
