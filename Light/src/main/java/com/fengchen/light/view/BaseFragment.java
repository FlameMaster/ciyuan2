package com.fengchen.light.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/5/24 14:17
 * <p>
 * = 分 类 说 明：最基础的fragment
 * ================================================
 */
public abstract class BaseFragment<DB extends ViewDataBinding> extends Fragment {

    /*视图模型*/
    private DB mBinding;

    /*初始化*/
    protected abstract void initFragment();

    /*获取绑定器*/
    public DB getViewDataBinding() {
        return mBinding;
    }

    /*获取布局id*/
    protected abstract int getLayoutID();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //初始化布局模型
        int layoutId = getLayoutID();
//      DataBindingUtil.setContentView(getActivity(), layoutId);//这样会引发异常，只适合在activity中调用
        mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false);


        //初始化
        initFragment();
        return getViewDataBinding().getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mBinding != null)
            mBinding.unbind();
        super.onDestroy();
    }

    public void toActivity(View view, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> p = new Pair<>(view, view.getTransitionName());
            ActivityOptions activityOptions =
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(), p);
            startActivityForResult(intent,0, activityOptions.toBundle());
        } else
            startActivityForResult(intent, 0);
    }
}
