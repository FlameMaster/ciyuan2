package com.fengchen.light;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.inputmethod.InputMethodManager;

import com.fengchen.light.dagger.component.AppComponent;
import com.fengchen.light.dagger.component.DaggerAppComponent;
import com.fengchen.light.dagger.module.AppModule;

import java.util.ArrayList;
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
 * = 时 间：2017/5/24 14:16
 * <p>
 * = 分 类 说 明：初始化整个app
 * ================================================
 */
public abstract class BaseApplication extends Application {

    /*打开的activity**/
    private Map<String,Activity> activities;
    /*应用实例**/
    private static BaseApplication instance;

    /*Dagger2的全局连接器*/
    private static AppComponent mAppComponent;
    /*本应用的全局上下文*/
    private static Context mContext;
    /*主线程已经初始化之后的handler*/
    private static Handler mHandler;
    /*主线程id*/
    private static int mainThreadID;
    /*输入帮助*/
    private static InputMethodManager mInputMannager;

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadID() {
        return mainThreadID;
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static InputMethodManager getmInputMannager() {
        return mInputMannager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化全局参数
        initGldbelParameter();
        //初始化连接器
         mAppComponent = DaggerAppComponent
                 .builder()
                 .appModule(new AppModule(getContext()))
                 .build();
        //初始化日志
        initLogger();
        //关联一个全局异常捕捉
        Thread.setDefaultUncaughtExceptionHandler(getException());
    }

    /**
     * 初始化全局参数
     */
    private void initGldbelParameter() {
        activities = new ArrayMap<>();
        instance = this;
        mContext= getApplicationContext();
        mHandler = new Handler();
        mainThreadID = android.os.Process.myTid();
        mInputMannager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 初始化本地日志
     */
    private void initLogger() {

    }

    /*获取一个异常处理类*/
    protected abstract BaseException getException();

    /*获得实例*/
    public static BaseApplication getInstance(){
        return instance;
    }
    /*新建了一个activity*/
    public void putActivity(Activity activity){
        String tag = activity.getClass().getName();
        activities.put(tag,activity);
    }
    /*结束指定的Activity*/
    public void removeActivity(Activity activity){
        if (activity!=null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /*应用退出，结束所有的activity*/
    public void exit(){
        for (Activity activity : activities.values()) {
            if (activity!=null) {
                activity.finish();
            }
        }
        activities.clear();
        System.exit(0);
    }

    /*关闭Activity列表中的所有Activity*/
    public void finishActivitys(){
        for (Activity activity : activities.values()) {
            if (null != activity) {
                activity.finish();
            }
        }
        activities.clear();
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
