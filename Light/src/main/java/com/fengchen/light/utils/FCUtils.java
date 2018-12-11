package com.fengchen.light.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.fengchen.light.BaseApplication;
import com.fengchen.light.R;
import com.fengchen.light.dagger.component.AppComponent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/5/24 15:22
 * <p>
 * = 分 类 说 明：方便的工具类
 * ================================================
 */
public class FCUtils {

    /**
     * @return 获取全局的Context
     */
    public static Context getContext() {
        return BaseApplication.getContext();
    }

    /**
     * @return 获取主线程的handler
     */
    public static Handler getHandler() {
        return BaseApplication.getHandler();
    }

    /**
     * @return 获取全局的AppComponent
     */
    public static AppComponent getAppComponent() {
        return BaseApplication.getAppComponent();
    }

    /**
     * @return 获取当前时间
     */
    public static long getNowTime() {
        return new Date().getTime();
    }

    /**
     * @return 获取当前应用的版本号
     */
    public static int getVersion() {
        PackageInfo info = null;
        try {
            PackageManager manager = getContext().getPackageManager();
            info = manager.getPackageInfo(getContext().getPackageName(), 0);
//            String version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return info.versionCode;
        }
    }

    /**
     * 结束整个app
     */
    public static void outApp() {
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getContext().startActivity(startMain);
//        System.exit(0);
        BaseApplication.getInstance().exit();
    }


    /**
     * 打印一个toast
     *
     * @param tost
     */
    public static void showToast(String tost) {
        Toast toast = Toast.makeText(getContext(), tost, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);//设置位置
        toast.show();
    }

    /**
     * @return 获得屏幕的宽度高度
     */
    public static int[] getScreenSize() {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        int[] pos = new int[2];
        pos[0] = outMetrics.widthPixels;
        pos[1] = outMetrics.heightPixels;

        return pos;
    }

    /**
     * @return 获取当前app信息
     */
    public static PackageInfo getAppInfo() {
        String versionName = "";
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            versionName = packageInfo.versionCode + "";
            return packageManager.getPackageInfo(getContext().getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return获取当前版本号
     */
    public static String getAppVersionCode() {
        String versionName = null;
        PackageInfo packageInfo = getAppInfo();
        if (packageInfo != null)
            versionName = packageInfo.versionCode + "";
        if (!StringUtil.noNull(versionName)) {
            return "";
        }
        return versionName;
    }

    /**
     * @return获取当前版本
     */
    public static String getAppVersionName() {
        String versionName = null;
        PackageInfo packageInfo = getAppInfo();
        if (packageInfo != null)
            versionName = packageInfo.versionName;
        if (!StringUtil.noNull(versionName)) {
            return "";
        }
        return versionName;
    }

    /**
     * 获取输入管理
     *
     * @return
     */
    public static InputMethodManager getmInputMannager() {
        return BaseApplication.getmInputMannager();
    }

    /**
     * @return 获取设备唯一标示码
     */
    public static String getMachineID() {
        TelephonyManager TelephonyMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return TelephonyMgr.getDeviceId();
    }


    /**
     * 获取渠道名
     *
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(String key) {
        String channelName = null;
        try {
            PackageManager packageManager = getContext().getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，
                // 而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(
                                getContext().getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            return channelName;
        }
    }

    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     * <p>
     * 渠道标志为：
     * 1，andriod（a）
     * <p>
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @return
     */
    public static String getDeviceId() {

        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
//        deviceId.append(getChannelName("UMENG_CHANNEL"));
//        deviceId.append("#");

        try {

            //wifi mac地址
//            WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            String wifiMac = info.getMacAddress();
//            Log.e("err*****","wifi="+wifiMac);
//            if (StringUtil.noNull(wifiMac)) {
//                deviceId.append("wifi");
//                deviceId.append(wifiMac);
//                deviceId.append("#");
//            }
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            Log.e("err*****", "imei=" + imei);
            if (StringUtil.noNull(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                deviceId.append("#");
            }

            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            Log.e("err*****", "sn=" + sn);
            if (StringUtil.noNull(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.e("err*****", "deviceId=" + deviceId);
            return deviceId.toString();
        }
    }

    /*判断是否有网*/
    public static boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) FCUtils.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            //mNetworkInfo.isAvailable();
            return true;//有网
        }
        return false;//没有网
    }

    /**
     * 获取主线程id
     *
     * @return 主线程id
     */
    public static int getMainThreadID() {
        return BaseApplication.getMainThreadID();
    }

    //////////////////////////////////资源文件相关//////////////////////////////////

    /**
     * 获取资源文件管理者
     *
     * @return 本应用的Resources
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取资源目录的字符串
     *
     * @param R_ID 字符串的id
     * @return String字符串
     */
    public static String getString(int R_ID) {
        return getResources().getString(R_ID);
    }

    /**
     * 获取资源目录的字符串组
     *
     * @param R_ID 字符串组的id
     * @return String字符串组
     */
    public static String[] getStringArray(int R_ID) {
        return getResources().getStringArray(R_ID);
    }

    /**
     * 获取资源目录的颜色值
     *
     * @param R_ID 资源目录id
     * @return int类型的色值
     */
    public static int getColor(int R_ID) {
        return getResources().getColor(R_ID);
    }

    /**
     * 获取资源目录的相应主题的颜色值
     *
     * @param currentTheme 使用的主题
     * @param R_ID         资源id
     * @return int类型的色值
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static int getThemeColor(Resources.Theme currentTheme, int R_ID) {
        return getResources().getColor(R_ID, currentTheme);
    }

    /**
     * 获取资源目录的颜色状态选择器
     *
     * @param R_ID 资源id
     * @return 颜色状态选择器
     */
    public static ColorStateList getColorStateList(int R_ID) {
        return getResources().getColorStateList(R_ID);
    }

    /**
     * 获取资源目录的相应主题的颜色选择器
     *
     * @param currentTheme 使用的主题
     * @param R_ID         资源id
     * @return 颜色状态选择器
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static ColorStateList getThemeColorStateList(Resources.Theme currentTheme, int R_ID) {
        return getResources().getColorStateList(R_ID, currentTheme);
    }

    /**
     * 获取资源目录的尺寸
     *
     * @param R_ID 资源id
     * @return int类型的尺寸的像素值
     */
    public static int getDimen(int R_ID) {
        return getResources().getDimensionPixelSize(R_ID);
    }

    /**
     * 获取资源目录的drawable图片资源
     *
     * @param R_ID 资源id
     * @return 资源目录的drawable
     */
    public static Drawable getDrawable(int R_ID) {
        return getResources().getDrawable(R_ID);
    }

    /**
     * 获取资源目录相应主题的drawable图片
     *
     * @param currentTheme 相应主题
     * @param R_ID         资源id
     * @return 资源目录的drawable
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getThemeDrawable(Resources.Theme currentTheme, int R_ID) {
        return getResources().getDrawable(R_ID, currentTheme);
    }

    /**
     * //透明状态栏
     * getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
     * //透明导航栏
     * getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
     * <p>
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     *
     * @return
     */
    public static int getNavigationHeight() {

        int resourceId;
        int rid = getContext().getResources().getIdentifier("config_showNavigationBar",
                "bool", "android");
        if (rid != 0) {
            resourceId = getContext().getResources().getIdentifier("navigation_bar_height",
                    "dimen", "android");
            return getContext().getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }


    //////////////////////////////////转化工具//////////////////////////////////

    /**
     * dp值转像素值
     *
     * @param dp dp值
     * @return 像素值
     */
    public static int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * sp值转像素值
     *
     * @param sp sp值
     * @return 像素值
     */
    public static int sp2px(int sp) {
        return (int) (sp * getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * 像素值转大潘值
     *
     * @param px 像素值
     * @return dp值
     */
    public static float px2dp(int px) {
        return px / getResources().getDisplayMetrics().density;
    }

    //////////////////////////////////其它工具//////////////////////////////////

    /**
     * 加载资源目录的布局转成view
     *
     * @param R_ID 布局id
     * @return view
     */
    public static View inflate(int R_ID) {
        return View.inflate(getContext(), R_ID, null);
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return 判断结果
     */
    public static boolean isRunOnUIThread() {

        // 获取当前线程id
        int myTid = android.os.Process.myTid();
        //如果当前线程id和主线程id相同, 那么当前就是主线程
        if (myTid == getMainThreadID()) {
            return true;
        }

        return false;
    }

    /**
     * 把runnable运行在主线程上
     *
     * @param r 需要运行的runnable
     */
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程, 直接运行
            r.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程
            getHandler().post(r);
        }
    }

    public static void fc() {
        //应用程序最大可用内存
        int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
        //应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory()) / 1024 / 1024;
        //应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        Log.e("**********", "应用程序最大可用内存=" + maxMemory + "M/" +
                "已获得内存=" + totalMemory + "M/" +
                "未使用内存=" + freeMemory + "M" +
                "使用内存=" + (totalMemory - freeMemory) + "M");

        System.gc();
    }

    /**
     * 检测该包名所对应的应用是否存在
     *
     * @param packageName
     * @return
     */
    public static boolean checkPackage(String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            getContext().getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写
     *                   true:key转化成小写，false:不转化
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap,
                                      boolean urlEncode, boolean keyToLower) {

        String buff = null;
        Map<String, String> tmpMap = paraMap;
        try {
//            List<Map.Entry<String, String>> infoIds = new ArrayList<>(tmpMap.entrySet());
            List<Map.Entry<String, String>> infoIds = new ArrayList<>();
            for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
                infoIds.add(entry);
            }
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）***需要TreeMap支持
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtil.noNull(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
            }
            buff = buf.toString();
            Log.e("formatUrlMap", "buff ：" + buff);
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return buff;
        }
    }

    /**
     * 获取一个空的binding
     *
     * @param width
     * @param height
     * @return
     */
    public static ViewDataBinding getNullBinding(int width, int height) {
        ViewDataBinding vb = DataBindingUtil.inflate(
                LayoutInflater.from(FCUtils.getContext()), R.layout.null_layout,
                null, false);
        vb.getRoot().setLayoutParams(new ViewGroup.LayoutParams(width, height));
        return vb;
    }
}
