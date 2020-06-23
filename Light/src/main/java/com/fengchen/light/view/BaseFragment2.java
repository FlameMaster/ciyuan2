package com.fengchen.light.mvp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fengchen.light.R;
import com.fengchen.light.http.EmptyState;
import com.fengchen.light.manager.DialogCheckBuilder;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.view.BaseActivity2;
import com.fengchen.light.view.BaseFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.ViewDataBinding;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：melvinhou@163.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/12/2 10:43
 * <p>
 * = 分 类 说 明：碎片化页面的鸡肋
 * ============================================================
 */
public abstract class MvpFragment<P extends BasePresenter, DB extends ViewDataBinding>
        extends BaseFragment<DB>
        implements BaseView {

    /*mvp-p*/
    private P mPresenter;
    /*工具栏菜单*/
    private Menu mMenu;
    /*进度条view*/
    private View mLoadingView;
    /*是否在加载中*/
    private boolean isShowLoading = false;
    /*加载弹窗*/
    private ProgressDialog mLoadDialog;


    public P getPresenter() {
        return mPresenter;
    }

    public Menu getMenu() {
        return mMenu;
    }


    @Override
    protected void initFragment() {
        mPresenter = upPresenter();
        //使用新组件，监听activity生命周期
        getLifecycle().addObserver(getPresenter());
        getViewDataBinding().setLifecycleOwner(this);
        //初始化主键
        initActionBar();
        initView();
        initListener();

    }

    /**
     * 初始化工具栏
     * 一般是act不带工具栏，由fgt携带
     */
    protected void initActionBar() {
        Toolbar toolbar = getViewDataBinding().getRoot().findViewById(R.id.bar);
        if (toolbar != null && getActivity() instanceof AppCompatActivity) {
            setHasOptionsMenu(true);
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            //初始bar
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //去除Toolbar自有的Title
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * toolbar的菜单
     *
     * @return 菜单栏资源id
     */
    protected int upBarMenuID() {
        return -1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        int barMenuID = upBarMenuID();
        if (barMenuID > 0) {
            menu.clear();
            inflater.inflate(barMenuID, menu);
            initMenu(menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void initMenu(Menu menu) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back(null);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    protected abstract P upPresenter();

    protected abstract void initView();

    protected abstract void initListener();


/////////////////////////////////////BaseView的实现/////////////////////////////////////////


    @Override
    public void nullClick(View view) {

    }

    @Override
    public void close() {
        getActivity().finish();
    }

    @Override
    public void back(View view) {
        close();
    }

    @Override
    public void refresh(View view) {
        getPresenter().onRefresh();
    }

    @Override
    public void submit(View view) {

    }

    @Override
    public void toActivity(Intent intent) {
        getActivity().startActivity(intent);
    }


/////////////////////////////////////加载相关/////////////////////////////////////////

    @Override
    public ViewGroup getLoadingRootLayout() {
        return (ViewGroup) getViewDataBinding().getRoot();
    }

    /**
     * 给一个头不高度
     *
     * @return
     */
    protected int getLoadingViewTop() {
        int top = 0;
        if (getLoadingRootLayout().findViewById(R.id.bar) != null) {
            TypedArray actionbarSizeTypedArray = FCUtils.getContext()
                    .obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
            top = (int) actionbarSizeTypedArray.getDimension(0, 0)
                    + FCUtils.getStatusHeight();
        }
        return top;
    }

    /**
     *
     * @return 获取加载布局
     */
    protected abstract int getLoadingLayoutID();

    @Override
    public void showLoadingView() {
        showLoadingView(null, false);
    }

    @Override
    public void showLoadingView(String message) {
        showLoadingView(message, false);
    }


    @Override
    public void showLoadingView(String message, boolean isCover) {

        if (!isShowLoading) {
            mLoadingView =
                    View.inflate(FCUtils.getContext(), getLoadingLayoutID(), null);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            lp.topMargin = getLoadingViewTop();
            mLoadingView.setLayoutParams(lp);
            mLoadingView.setTag("loading");
            getLoadingRootLayout().addView(mLoadingView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mLoadingView.setElevation(FCUtils.dp2px(8));
            mLoadingView.setOnClickListener(v -> nullClick(v));
            isShowLoading = true;
        }

        //判断是否覆盖界面
        if (isCover) {
            mLoadingView.setBackgroundColor(Color.WHITE);
            mLoadingView.findViewById(R.id.loading_text).setVisibility(View.VISIBLE);
        } else {
            mLoadingView.setBackgroundColor(Color.TRANSPARENT);
            mLoadingView.findViewById(R.id.loading_text).setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoadingView() {
        if (isShowLoading) {
            getLoadingRootLayout().removeView(mLoadingView);
            mLoadingView = null;
            isShowLoading = false;
        }
    }

    @Override
    public void changeLoadingState(int code) {
        changeLoadingState(code,"网络异常，请稍后重试");
    }


    @Override
    public void changeLoadingState(int code, String message) {
        ((TextView) mLoadingView.findViewById(R.id.loading_text)).setText(message);
        mLoadingView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        mLoadingView.findViewById(R.id.loading_img).setVisibility(View.VISIBLE);
        if (code == EmptyState.NET_ERROR) {
            mLoadingView.findViewById(R.id.loading_button).setVisibility(View.VISIBLE);
            mLoadingView.findViewById(R.id.loading_button).setOnClickListener(v -> refresh(v));
        }
    }

    /*功能 ：显示一个进度条对话框*/
    @Override
    public void showProcess(String message) {
        if (mLoadDialog == null) {
            mLoadDialog = new ProgressDialog(getActivity());
            mLoadDialog.setCanceledOnTouchOutside(false);
        }
        mLoadDialog.setTitle(message);
        mLoadDialog.show();
        mLoadDialog.setCancelable(false);
    }

    /*功能 ：取消一个进度条对话框*/
    @Override
    public void hideProcess() {
        if (mLoadDialog != null) {
            mLoadDialog.hide();
        }
    }

/////////////////////////////////////校验相关/////////////////////////////////////////

    @Override
    public void showCheckView(DialogCheckBuilder builder) {
        if (getActivity() instanceof BaseActivity2) {
            BaseActivity2 acitvity = (BaseActivity2) getActivity();
            acitvity.showCheckView(builder);
        }
    }

    @Override
    public void hideCheckView() {

        if (getActivity() instanceof BaseActivity2) {
            BaseActivity2 acitvity = (BaseActivity2) getActivity();
            acitvity.hideCheckView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadDialog!=null){
            mLoadDialog.dismiss();
            mLoadDialog.cancel();
            mLoadDialog=null;
        }
    }
}
