package com.fengchen.light.mvp;

import android.view.View;

import com.fengchen.light.view.BaseActivity2;

import androidx.databinding.ViewDataBinding;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：melvinhou@163.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/11/27 17:25
 * <p>
 * = 分 类 说 明：项目的顶层活动页面
 * ============================================================
 */
public  abstract class MvpActivity2<P extends BasePresenter, DB extends ViewDataBinding>
        extends BaseActivity2<DB>

{


    public P getPresenter() {
        return mPresenter;
    }

    /*mvp-p*/
    private P mPresenter;

    @Override
    protected void initActivity() {
        mPresenter = upPresenter();
        //使用新组件，监听activity生命周期
        getLifecycle().addObserver(getPresenter());
        super.initActivity();
    }


    protected abstract P upPresenter();



    @Override
    public void refresh(View view) {
        getPresenter().onRefresh();
    }


    @Override
    protected  void onLoading(){
    }
}
