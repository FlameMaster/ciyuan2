package com.fengchen.light.view;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.fengchen.light.BaseApplication;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.PermissionUtil;

import org.jetbrains.annotations.NotNull;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/5/23 19:09
 * <p>
 * = 分 类 说 明：最基础的activity
 * ================================================
 */
public abstract class BaseActivity<DB extends ViewDataBinding> extends AppCompatActivity {

    /*视图模型*/
    private DB mBinding;
    /*请求回调*/
    private PermissionUtil.PermissionGrant  permissionGrant;

    /*初始化*/
    protected abstract void initActivity();

    /*获取绑定器*/
    public DB getViewDataBinding(){
        return mBinding;
    }

    /*获取布局id*/
    protected abstract int getLayoutID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getInstance().putActivity(this);
        //初始化布局模型
        int layoutId = getLayoutID();
        mBinding = DataBindingUtil.setContentView(this, layoutId);
        //设置导航栏状态
        changeWindowBar(Color.TRANSPARENT,Color.TRANSPARENT);
        //初始化
        initActivity();
    }

    @Override
    protected void onDestroy() {
        if (mBinding != null) {
            mBinding.unbind();
            mBinding = null;
        }
        super.onDestroy();
        BaseApplication.getInstance().removeActivity(this);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }



    /**
     * 处理权限请求结果
     *
     * @param requestCode
     *          请求权限时传入的请求码，用于区别是哪一次请求的
     *
     * @param permissions
     *          所请求的所有权限的数组
     *
     * @param grantResults
     *          权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
     *          授予: PackageManager.PERMISSION_GRANTED
     *          拒绝: PackageManager.PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.e(getClass().getName(),"权限授予成功");
            if (permissionGrant!=null)
                permissionGrant.onPermissionGranted(requestCode);
        }else {
            FCUtils.showToast("未能授予权限");
        }
    }
    /*权限申请成功的回调*/
    public void setPermissionGrant(PermissionUtil.PermissionGrant permissionGrant) {
        this.permissionGrant = permissionGrant;
    }

    /**
     * 切换状态栏颜色和导航栏颜色
     *
     * @param statusColor 状态栏颜色
     * @param navigationColor 导航栏颜色
     */
    public void changeWindowBar(@ColorInt int statusColor,@ColorInt int navigationColor){

        //透明工具条
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// Android 5.0 以上 全透明
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            /**
             *控制状态栏和导航栏的显示：
             *SYSTEM_UI_FLAG_VISIBLE：默认状态栏，呼出虚拟导航栏会自动resize
             *INVISIBLE：隐藏状态栏，同时Activity会伸展全屏显示
             *SYSTEM_UI_FLAG_FULLSCREEN：隐藏状态栏，API >= 16不可单独使用，否则部分手机顶部有白条
             *SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：半透明状态栏，API >= 16
             *SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟导航栏，API >= 16 自动resize,触摸屏幕会自动显示虚拟导航栏
             *SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：半透明虚拟导航栏，会自动设置半透明状态栏，API >= 16
             *SYSTEM_UI_FLAG_IMMERSIVE：自动隐藏状态栏和虚拟导航栏，并且在bar出现的位置滑动可以呼出bar，API >= 19
             *SYSTEM_UI_FLAG_IMMERSIVE_STIKY：和上面不同的是，呼出的bar会自动再隐藏掉，API >= 19
             * SYSTEM_UI_FLAG_LAYOUT_STABLE：保持整个View稳定，使View不会因为System UI的变化而重新layout，API >= 16没发现作用
             */
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
            getWindow().setStatusBarColor(statusColor);
            // 虚拟导航键
            getWindow().setNavigationBarColor(navigationColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// Android 4.4 以上 半透明
            // 状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟导航键
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //4.4版本颜色无法设置，只能通过view显示
        }

//            tintManager = new SystemBarTintManager(this);
//            //设置图片状态栏
//            tintManager.setStatusBarTintResource(R.drawable.gradient_statusbar_bg);
//            tintManager.setStatusBarTintEnabled(true);
    }

}
