package com.fengchen.ciyuan2.a_inset;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.databinding.PictureActBD;
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
 * = 时 间：2019/2/26 17:42
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class PictureActivity extends BaseActivity<PictureActBD> {


    @Override
    public void changeWindowBar(int statusColor, int navigationColor) {
        super.changeWindowBar(statusColor, navigationColor);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        //设置状态栏文字颜色及图标为深色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //设置状态栏文字颜色及图标为浅色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //初始bar
        setSupportActionBar(getViewDataBinding().bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //去除Toolbar自有的Title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    protected void initActivity() {
        getViewDataBinding().setUrl(getIntent().getStringExtra("url"));
        RequestOptions options = new RequestOptions()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        Glide.with(FCUtils.getContext()).asBitmap().load(getIntent().getStringExtra("url")).apply(options).into(getViewDataBinding().photo);
//        Glide.with(FCUtils.getContext()).load(getIntent().getStringExtra("url")).apply(options).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                getViewDataBinding().photo.setImageDrawable(resource);
//            }
//        });

    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_picture;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.collect:
                FCUtils.showToast("收藏是不可能收藏的");
                break;
            case R.id.download:
                FCUtils.showToast("扫码支付宝下载");
                break;
            case R.id.album:
                FCUtils.showToast("友情提示\n1.退出应用\n2.打开系统相册");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
