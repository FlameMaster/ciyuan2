package com.fengchen.ciyuan2;

import android.provider.Settings;

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
}
