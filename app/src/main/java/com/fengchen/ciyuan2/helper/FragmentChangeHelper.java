package com.fengchen.ciyuan2.helper;

import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.transition.Explode;
import android.transition.Slide;

import com.fengchen.ciyuan2.a_home.HomeFragment;
import com.fengchen.ciyuan2.a_inset.InsetFragment;
import com.fengchen.ciyuan2.a_movie.MovieFragment;
import com.fengchen.light.view.BaseFragment;
import com.fengchen.light.utils.StringUtil;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2017/3/2 16:48
 * <p>
 * = 分 类 说 明：fragment切换类
 * ================================================
 */

public class FragmentChangeHelper {

    /*主页*/
    public final static String TAG_MAIN_HOME= "0";
    /*番剧*/
    public final static String TAG_MAIN_MOVIE = "1";
    /*直播*/
    public final static String TAG_MAIN_INSET = "2";


    /*fragment集合*/
    private ArrayMap<String, BaseFragment> mFragmets;
    /*当前的页面*/
    private String currentFragmentCode = null;

    private FragmentManager mFragmentManager;
    private static FragmentChangeHelper helper;

    private FragmentChangeHelper(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        mFragmets = new ArrayMap<>();
    }

    public static FragmentChangeHelper getChangeHelper(FragmentManager fragmentManager) {
        if (helper == null)
            helper = new FragmentChangeHelper(fragmentManager);
        return helper;
    }

    /**
     * 获取唯一的fragment
     *
     * @param tag 标示每一个fragment的tag
     * @return
     */
    private BaseFragment getFragment(String tag) {
        if (!StringUtil.noNull(tag)) return null;
        if (!mFragmets.containsKey(tag)) {
            BaseFragment fragment = null;
            switch (tag) {
                case TAG_MAIN_HOME:
                    fragment = new HomeFragment();
                    break;
                case TAG_MAIN_MOVIE:
                    fragment = new MovieFragment();
                    break;
                case TAG_MAIN_INSET:
                    fragment = new InsetFragment();
                    break;
            }
            //添加到集合
            mFragmets.put(tag, fragment);

        }
        return mFragmets.get(tag);
    }

    /**
     * 页面切换
     *
     * @param tag       标示每一个fragment的tag
     * @param contentId 切换的布局
     */
    public void changeFragment(String tag, int contentId) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right);

        BaseFragment fragment = getFragment(tag);
        BaseFragment currentFragment = getFragment(currentFragmentCode);

        /*5.0转场动画*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Defines enter transition for all fragment views
            Slide slideTransition = new Slide();
            slideTransition.setDuration(500);
            fragment.setEnterTransition(slideTransition);
            fragment.setSharedElementEnterTransition(new Explode());
        }

//        transaction.replace(contentId,fragment).commit();

        if (currentFragment != null) transaction = transaction
                .hide(currentFragment);
        if (!fragment.isAdded()) transaction
                .add(contentId, fragment)
                .commit();
        else transaction
                .show(fragment)
                .commit();
        currentFragmentCode = tag;
    }

    /**
     * 返回当前页面代码
     *
     * @return
     */
    public String getCurrentFragmentCode() {
        return currentFragmentCode;
    }

    /**
     * 返回当前页面
     *
     * @return
     */
    public BaseFragment getCurrentFragment() {
        return getFragment(currentFragmentCode);
    }

    /*清空*/
    public static void cleanHelper() {
        if (helper != null) {
            helper.mFragmentManager = null;
            if (helper.mFragmets != null)
                helper.mFragmets.clear();
            helper.mFragmets = null;
            helper = null;
        }
    }
}
