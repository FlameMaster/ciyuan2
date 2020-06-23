package com.irock.ningxiataxbureau.officeautomation.alpha.mvp;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.fengchen.light.http.EmptyState;
import com.irock.ningxiataxbureau.officeautomation.DialogCheckBuilder;

import androidx.lifecycle.ViewModelStoreOwner;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：melvinhou@163.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/11/27 17:28
 * <p>
 * = 分 类 说 明：mvp-v
 * ============================================================
 */
public interface BaseView extends ViewModelStoreOwner {

    /**
     * 关闭页面
     */
    void close();


    /**
     * 跳转页面
     *
     * @param intent
     */
    void toActivity(Intent intent);


////////////////////////////////网络加载相关//////////////////////////////////////////////

    /**
     * 返回加载控制的父布局
     *
     * @return
     */
    ViewGroup getLoadingRootLayout();

    /**
     * 显示加载条
     *
     * @param isCover 是否覆盖页面
     * @param message 加载信息
     */
    void showLoadingView(String message, boolean isCover);
    void showLoadingView();
    void showLoadingView(String message);

    /**
     * 隐藏加载条
     */
    void hideLoadingView();

    /**
     * 切换加载状态
     *
     * @param code 状态码
     * @param mesage 信息
     */
    void changeLoadingState(@EmptyState int code, String mesage);
    void changeLoadingState(@EmptyState int code);


    /**
     * 显示一个检查弹窗
     *
     * @param builder 显示的参数
     */
    void showCheckView(DialogCheckBuilder builder);

    /**
     * 隐藏检查弹窗
     */
    void hideCheckView();

    /**
     * 进度条弹窗
     * @param message
     */
    void showProcess(String message);

    /**
     * 进度条弹窗
     */
    void hideProcess();

////////////////////////////////通用点击事件//////////////////////////////////////////////

    void nullClick(View view);

    /**
     * 返回
     *
     * @param view
     */
    void back(View view);

    /**
     * 刷新
     *
     * @param view
     */
    void refresh(View view);

    /**
     * 提交
     *
     * @param view
     */
    void submit(View view);

}
