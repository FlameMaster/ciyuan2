package com.fengchen.light.model;

import com.google.gson.annotations.SerializedName;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/9/3 15:02
 * <p>
 * = 分 类 说 明：网络实体类
 * ================================================
 */

public class BaseEntity<E> {


    /*访问成功*/
    public static final int CODE_NORMAL = 0;

    @SerializedName("status")
    private int code;
    @SerializedName("errmsg")
    private String message;
    @SerializedName("data")
    private E data;
    @SerializedName("date")
    private String date;

    public boolean isSuccess() {
        return code == CODE_NORMAL;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
