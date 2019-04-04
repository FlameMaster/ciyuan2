package com.fengchen.ciyuan2.bean;

import java.util.List;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/2/16 14:03
 * <p>
 * = 分 类 说 明：主页的插画数据
 * ============================================================
 */
public class MainInset {
    private List<InsetItem> list;
    private List<InsetItem> banners;

    public List<InsetItem> getList() {
        return list;
    }

    public void setList(List<InsetItem> list) {
        this.list = list;
    }

    public List<InsetItem> getBanners() {
        return banners;
    }

    public void setBanners(List<InsetItem> banners) {
        this.banners = banners;
    }
}
