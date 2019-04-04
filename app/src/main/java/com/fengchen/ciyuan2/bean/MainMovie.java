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
 * = 分 类 说 明：主页的番剧数据
 * ============================================================
 */
public class MainMovie {
    private List<MovieItem> list;
    private List<MovieItem> banners;
    private List<MovieItem> recommend;

    public List<MovieItem> getList() {
        return list;
    }

    public void setList(List<MovieItem> list) {
        this.list = list;
    }

    public List<MovieItem> getBanners() {
        return banners;
    }

    public void setBanners(List<MovieItem> banners) {
        this.banners = banners;
    }

    public List<MovieItem> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<MovieItem> recommend) {
        this.recommend = recommend;
    }
}
