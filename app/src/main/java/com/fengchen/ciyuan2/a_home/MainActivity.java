package com.fengchen.ciyuan2.a_home;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.bean.Ghg;
import com.fengchen.ciyuan2.databinding.ActivityMainBinding;
import com.fengchen.ciyuan2.helper.FragmentChangeHelper;
import com.fengchen.ciyuan2.wiget.RoundLayout;
import com.fengchen.light.view.BaseActivity;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.StringUtil;

import java.lang.reflect.Method;


/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/1 19:39
 * <p>
 * = 分 类 说 明：
 * ================================================
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {


    /*切换fragment的tag*/
    String fragmentTag;

    /*fragment切换帮助类*/
    private FragmentChangeHelper mFragmentChangeHelper;

    @Override
    protected void initActivity() {
        //判断小米是否开启全面屏手势，不等于0为开启全面屏
        int fullscreen;
        try {
            fullscreen = Settings.Global.getInt(getContentResolver(), "force_fsg_nav_bar");
        } catch (Settings.SettingNotFoundException e) {
            fullscreen = 0;
        }
        //改变底部边距
        if ( navigationBarExist2()&&fullscreen == 0) {
            RoundLayout.LayoutParams lp = (RoundLayout.LayoutParams) getViewDataBinding().tabhost.getLayoutParams();
            lp.bottomMargin = FCUtils.getNavigationHeight();
            getViewDataBinding().tabhost.setLayoutParams(lp);
        }

        getViewDataBinding().setGhG(new Ghg());

        mFragmentChangeHelper = FragmentChangeHelper
                .getChangeHelper(getSupportFragmentManager());
        fragmentTag = FragmentChangeHelper.TAG_MAIN_HOME;
        //切换页面
        if (StringUtil.noNull(fragmentTag))
            mFragmentChangeHelper.changeFragment(
                    fragmentTag, getViewDataBinding().root.getId());


        /*页面切换监听*/
        RadioGroup.OnCheckedChangeListener mChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.tab_home:
                        fragmentTag = FragmentChangeHelper.TAG_MAIN_HOME;
                        break;
                    case R.id.tab_movie:
                        fragmentTag = FragmentChangeHelper.TAG_MAIN_MOVIE;
                        break;
                    case R.id.tab_inset:
                        fragmentTag = FragmentChangeHelper.TAG_MAIN_INSET;
                        break;
                }

                //切换页面
                if (StringUtil.noNull(fragmentTag))
                    mFragmentChangeHelper.changeFragment(
                            fragmentTag, getViewDataBinding().root.getId());
            }
        };
        getViewDataBinding().tabhost.setOnCheckedChangeListener(mChangeListener);


    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }


    /**
     * 判断导航栏是否显示
     *
     * @return
     */
    public boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = FCUtils.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    /**
     * 此方法在模拟器还是在真机都是完全正确
     *
     * @return
     */
    public boolean navigationBarExist2() {
        WindowManager windowManager = getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    @Override
    protected void onDestroy() {
        FragmentChangeHelper.cleanHelper();
        super.onDestroy();
    }
}
