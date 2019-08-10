package com.fengchen.light.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

import com.fengchen.light.R;
import com.fengchen.light.http.EmptyException;
import com.fengchen.light.http.EmptyState;
import com.fengchen.light.utils.FCUtils;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/9/11 20:06
 * <p>
 * = 分 类 说 明：网络加载状态
 * ================================================
 */

public class StateModel extends BaseObservable {

    @EmptyState
    private int emptyState = EmptyState.NORMAL;

    private boolean empty;

    private String userText =null;

    public StateModel() {
    }

    public StateModel(@EmptyState int emptyState) {
        this.emptyState = emptyState;
    }

    public int getEmptyState() {
        return emptyState;
    }

    /**
     * 设置状态
     *
     * @param emptyState
     */
    public void setEmptyState(@EmptyState int emptyState) {
        this.emptyState = emptyState;
        notifyChange();
    }

    /**
     * 显示进度条
     *
     * @return
     */
    public boolean isProgress() {
        return this.emptyState == EmptyState.PROGRESS;
    }

    /**
     * 根据异常显示状态
     *
     * @param e
     */
    public void bindThrowable(Throwable e) {
        if (e instanceof EmptyException) {
            @EmptyState
            int code = ((EmptyException) e).getCode();

            setEmptyState(code);
        }
    }

    public boolean isEmpty() {
        return this.emptyState != EmptyState.NORMAL;
    }

    /**
     * 空状态信息
     *
     * @return
     */
    @Bindable
    public String getCurrentStateLabel() {

        switch (emptyState) {
            case EmptyState.EMPTY:
                return FCUtils.getString(R.string.no_data);
            case EmptyState.NOT_MORE_DATA:
                return FCUtils.getString(R.string.no_more_data);
            case EmptyState.NET_ERROR:
                return FCUtils.getString(R.string.net_err);
            case EmptyState.NOT_AVAILABLE:
                return FCUtils.getString(R.string.server_not_avaliabe);
            case EmptyState.USER_DEFINED:
                return getUserText();
            default:
                return FCUtils.getString(R.string.please_check_net_state);
        }
    }

    /**
     * 空状态图片
     *
     * @return
     */
    @Bindable
    public Drawable getEmptyIconRes() {
        switch (emptyState) {
            case EmptyState.EMPTY:
                return FCUtils.getDrawable(0);
            case EmptyState.NET_ERROR:
                return FCUtils.getDrawable(0);
            case EmptyState.NOT_AVAILABLE:
                return FCUtils.getDrawable(0);
            default:
                return FCUtils.getDrawable(0);
        }
    }

    public String getUserText() {
        return userText;
    }

    public StateModel setUserText(String userText) {
        this.userText = userText;
        return this;
    }
}
