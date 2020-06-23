package com.irock.ningxiataxbureau.officeautomation.alpha.mvp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengchen.light.http.EmptyState;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.StringUtil;
import com.fengchen.light.view.BaseActivity;
import com.irock.ningxiataxbureau.officeautomation.DialogCheckBuilder;
import com.irock.ningxiataxbureau.officeautomation.R;
import com.irock.ningxiataxbureau.officeautomation.wiget.LoadDialog;

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
 * = 时 间：2019/11/27 17:25
 * <p>
 * = 分 类 说 明：项目的顶层活动页面
 * ============================================================
 */
public abstract class NxoaActivity2< DB extends ViewDataBinding>
        extends BaseActivity<DB>
        implements BaseView {

    /*工具栏*/
    private Toolbar mToolbar;
    /*工具栏菜单*/
    private Menu mMenu;

    /*检查弹窗*/
    private Dialog mCheckDialog;
    /*进度条view*/
    private View mLoadingView;
    /*是否在加载中*/
    private boolean isShowLoading = false;
    /*加载弹窗*/
    private LoadDialog mLoadDialog;


    public Toolbar getToolbar() {
        return mToolbar;
    }

    public Menu getMenu() {
        return mMenu;
    }


    @Override
    public void changeWindowBar(int statusColor, int navigationColor) {
//        super.changeWindowBar(statusColor, navigationColor);
        //设置状态栏文字颜色及图标为深色
//        getWindow().getDecorView().setSystemUiVisibility(
//        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //设置状态栏文字颜色及图标为浅色
//        getWindow().getDecorView().setSystemUiVisibility(
//        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//        View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //改变状态栏和导航栏样式
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            // 虚拟导航键
            getWindow().setNavigationBarColor(Color.WHITE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 状态栏半透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟导航键半透明
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //4.4版本颜色无法设置，只能通过view显示
        }
    }

    @Override
    protected void initActivity() {
        //工具栏
        initToolBar();
        getViewDataBinding().setLifecycleOwner(this);
        //初始化view
        initView();
        //初始化监听
        initListener();
    }

    /**
     * 初始化工具栏
     */
    protected void initToolBar() {
        //工具条
        mToolbar = findViewById(R.id.bar);
        if (mToolbar != null) {
            //初始bar
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //去除Toolbar自有的Title
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        int barMenuID = upBarMenuID();
        if (barMenuID > 0)
            getMenuInflater().inflate(barMenuID, menu);
        initMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 初始化菜单栏
     *
     * @param menu
     */
    protected void initMenu(Menu menu) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            back(null);
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        onLoading();
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected  void onLoading(){
        showLoadingView("加载中...",true);
    }


/////////////////////////////////////BaseView的实现/////////////////////////////////////////


    @Override
    public void nullClick(View view) {

    }


    @Override
    public void close() {
        hideCheckView();
        hideLoadingView();
        if (mCheckDialog != null) {
            mCheckDialog.dismiss();
            mCheckDialog.cancel();
            mCheckDialog = null;
        }
        finish();
    }

    @Override
    public void back(View view) {
        close();
    }

    @Override
    public void submit(View view) {
    }

    @Override
    public void refresh(View view) {
        onLoading();
    }

    @Override
    public void toActivity(Intent intent) {
        startActivity(intent);
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
//        ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("bxbxbx");
//        dialog.show();

        if (!isShowLoading) {
            mLoadingView =
                    View.inflate(FCUtils.getContext(), R.layout.view_loading, null);
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
            mLoadingView.findViewById(R.id.text_state).setVisibility(View.VISIBLE);
        } else {
            mLoadingView.setBackgroundColor(Color.TRANSPARENT);
            mLoadingView.findViewById(R.id.text_state).setVisibility(View.GONE);
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
        changeLoadingState(code, "网络异常，请稍后重试");
    }

    @Override
    public void changeLoadingState(int code, String message) {
        ((TextView) mLoadingView.findViewById(R.id.text_state)).setText(message);
        mLoadingView.findViewById(R.id.progress).setVisibility(View.GONE);
        mLoadingView.findViewById(R.id.img_err).setVisibility(View.VISIBLE);
        if (code == EmptyState.NET_ERROR) {
            mLoadingView.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
            mLoadingView.findViewById(R.id.refresh).setOnClickListener(v -> onLoading());
        }
    }

    /*功能 ：显示一个进度条对话框*/
    @Override
    public void showProcess(String message) {
        if (mLoadDialog == null) {
            mLoadDialog = new LoadDialog(this,R.style.NxDialog3);
            mLoadDialog.setCanceledOnTouchOutside(false);
        }
        mLoadDialog.show(message);
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
        if (builder == null) return;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setMessage(Html.fromHtml("<font color='#000000'>" + builder.getExplainText() + "</font>"));

        //标题
        if (StringUtil.noNull(builder.getTitleText()))
            dialogBuilder.setTitle(builder.getTitleText());

        //积极按钮
        String confirmText = "确定";
        if (StringUtil.noNull(builder.getConfirmText())) confirmText = builder.getConfirmText();
        dialogBuilder.setPositiveButton(confirmText, (dialog, which) -> {
            builder.confirm();
        });

        //消极按钮
        if (StringUtil.noNull(builder.getCancelText()))
            dialogBuilder.setNegativeButton(builder.getCancelText(), (dialog, which) -> {
                builder.cancel();
            });

        //显示
        mCheckDialog = dialogBuilder.show();
    }

    @Override
    public void hideCheckView() {
        if (mCheckDialog != null) mCheckDialog.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDialog!=null){
            mLoadDialog.dismiss();
            mLoadDialog.cancel();
            mLoadDialog=null;
        }
    }
}
