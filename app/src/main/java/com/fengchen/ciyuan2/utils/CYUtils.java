package com.fengchen.ciyuan2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.fengchen.light.utils.FCUtils;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/29 11:12
 * <p>
 * = 分 类 说 明：本应用的专用工具类
 * ============================================================
 */
public class CYUtils {

    /*获取系统用户是否开启屏幕旋转：1表示已开启，0表示未开启*/
    public static boolean isOpenScreenRotate() {
        boolean isOpen = false;
        try {
            int screenchange = Settings.System.getInt(FCUtils.getContext().getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);

            isOpen = screenchange != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        } finally {
            return isOpen;
        }
    }

    /*获取窗口大小*/
    public static int[] getWindowsSize() {
        WindowManager wm = (WindowManager) FCUtils.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e("获取屏幕宽高","width："+width+"\t\theight："+height);
        Resources resources = FCUtils.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density1 = dm.density;//密度
        int width2 = dm.widthPixels;
        int height2 = dm.heightPixels;
        Log.e("获取屏幕宽高","width2："+width2+"\t\theight2："+height2);
        final Display display = wm.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        Log.e("获取屏幕宽高","width3："+outPoint.x+"\t\theight3："+outPoint.y);
        return new int[]{outPoint.x, outPoint.y};
    }
    /*获取状态栏高度*/
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = FCUtils.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = FCUtils.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /*获取导航栏高度*/
    public static int getNavigationBarHeight() {
        int resourceId;
        int rid = FCUtils.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = FCUtils.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return FCUtils.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }

    private static final int HOUR = 60 * 60 * 1000;
    private static final int MIN =  60 * 1000;
    private static final int SEC =  1000;
    /** 将时间戳转换为 01:01:01 或 01:01 的格式 */
    public static String formatDuration(int duartion){
        // 计算小时数
        int hour = duartion / HOUR;

        // 计算分钟数
        int min = duartion % HOUR / MIN;

        // 计算秒数
        int sec = duartion % MIN / SEC;

        // 生成格式化字符串
        if (hour == 0){
            // 不足一小时 01：01
            return String.format("%02d:%02d", min, sec);
        } else if (hour<24){
            // 大于一小时 01:01:01
            return String.format("%02d:%02d:%02d", hour, min, sec);
        }else
            return "max";//超出24小时的一般都是直播
    }
}
