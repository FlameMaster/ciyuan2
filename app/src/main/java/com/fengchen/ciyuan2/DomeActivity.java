package com.fengchen.ciyuan2;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.fengchen.ciyuan2.databinding.ActDomeBinding;
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
 * = 时 间：2019/4/27 20:18
 * <p>
 * = 分 类 说 明：用来演示各种dome控件
 * ============================================================
 */
public class DomeActivity extends BaseActivity<ActDomeBinding> {
    @Override
    protected void initActivity() {
        ImageView svgg = findViewById(R.id.svgg);
        if (svgg != null)
            svgg.setOnClickListener(v -> {
                Drawable drawable = svgg.getDrawable();
                if (drawable instanceof Animatable) {
                    FCUtils.showToast("开始动画");
                    ((Animatable) drawable).start();
                }
            });

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_dome;
    }

    @Override
    public void changeWindowBar(int statusColor, int navigationColor) {
//        super.changeWindowBar(statusColor, navigationColor);
    }
}
