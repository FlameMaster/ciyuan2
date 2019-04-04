package com.fengchen.ciyuan2.net;

import java.util.List;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/2/15 16:41
 * <p>
 * = 分 类 说 明：列表的实体类,用于实现CYEntity的D
 * ============================================================
 */
public class CYListEntity<D>{

    private List<D> list;
    private int page;
    private int size;


    public List<D> getList() {
        return list;
    }

    public void setList(List<D> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
