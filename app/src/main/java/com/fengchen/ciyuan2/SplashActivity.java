package com.fengchen.ciyuan2;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.fengchen.ciyuan2.databinding.ActSplashBD;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.view.BaseActivity;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/8/10 15:30
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class SplashActivity extends BaseActivity<ActSplashBD> {
    @Override
    protected void initActivity() {
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_splash;
    }

    public void test(View view){
        Drawable avDrawable = getViewDataBinding().logo.getBackground();
        if (avDrawable instanceof Animatable){
            ((Animatable) avDrawable).start();
        }
    }
}
