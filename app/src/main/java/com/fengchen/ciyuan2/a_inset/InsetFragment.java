package com.fengchen.ciyuan2.a_inset;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.bean.InsetItem;
import com.fengchen.ciyuan2.bean.MainInset;
import com.fengchen.ciyuan2.bean.MainMovie;
import com.fengchen.ciyuan2.bean.MovieItem;
import com.fengchen.ciyuan2.databinding.FgtInsetBD;
import com.fengchen.ciyuan2.databinding.ItemInsetBD;
import com.fengchen.ciyuan2.databinding.ItemInsetBannerBD;
import com.fengchen.ciyuan2.databinding.LoadMoreBD;
import com.fengchen.ciyuan2.databinding.TopInsetBD;
import com.fengchen.ciyuan2.net.CYEntity;
import com.fengchen.ciyuan2.net.LoadUtils;
import com.fengchen.ciyuan2.utils.CYUtils;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.http.EmptyState;
import com.fengchen.light.model.StateModel;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.IOUtils;
import com.fengchen.light.view.BaseFragment;
import com.google.gson.reflect.TypeToken;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2019/1/26 14:50
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class InsetFragment extends BaseFragment<FgtInsetBD> {

    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
            url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";


    BannerAdapter mBannerAdapter;
    ListAdapter mListAdapter;

    @Override
    protected void initFragment() {

        //初始化推荐列表
        new PagerSnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        getViewDataBinding().banner.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mBannerAdapter = new BannerAdapter();
        getViewDataBinding().banner.setAdapter(mBannerAdapter);
        getViewDataBinding().banner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            float MIN_SCALE = 0.8f;//当前图片缩小比例

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                RecyclerView.SCROLL_STATE_IDLE//静止没有滚动
//                RecyclerView.SCROLL_STATE_DRAGGING //外部拖拽
//                RecyclerView.SCROLL_STATE_SETTLING //自动滚动
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //获取中心点
                int middle = (int) (recyclerView.getX() + recyclerView.getWidth() / 2);
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    int childMiddle = (int) (child.getX() + child.getWidth() / 2);
                    //获取相对位置
                    int gap = Math.abs(middle - childMiddle);
                    float fraction = (float) gap / (float) recyclerView.getWidth();
                    scale((ViewGroup) child, fraction);
                }
            }

            private void scale(ViewGroup layout, float fraction) {
                View mainContainer = layout.getChildAt(0);
                View invertedView = layout.getChildAt(1);
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - fraction);
                layout.setAlpha(1 - fraction);
                mainContainer.setScaleX(scaleFactor);
                mainContainer.setScaleY(scaleFactor);
                invertedView.setScaleX(scaleFactor);
                invertedView.setScaleY(scaleFactor);
                invertedView.setTranslationY(FCUtils.dp2px(24) * fraction);
            }
        });


        //初始化条目列表
//        getViewDataBinding().list
//                .setLayoutManager(new TestLayoutManager(2));
        getViewDataBinding().list
                .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);
        mListAdapter.setOnItemClickListener((BaseRecyclerAdapter.OnItemClickListener<InsetItem>) (viewHolder, position, data) -> {
            Intent intent = new Intent(FCUtils.getContext(), PictureActivity.class);
            intent.putExtra("url", data.getUrl());
            toActivity(((ItemInsetBD) viewHolder.getBinding()).img, intent);
        });
        //尾布局
        LoadMoreBD loadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(
                FCUtils.getContext()), R.layout.item_loadmore, null, false);
        loadMoreBinding.setState(new StateModel(EmptyState.USER_DEFINED).setUserText(""));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = FCUtils.dp2px(108);
        loadMoreBinding.getRoot().setLayoutParams(lp);
        if (mListAdapter.getTailSize() <= 0) mListAdapter.addTailBinding(loadMoreBinding);

        TopInsetBD topInsetBD = TopInsetBD.bind(View.inflate(FCUtils.getContext(), R.layout.top_inset, null));
        mListAdapter.addHeaderBinding(topInsetBD);
        loadData();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_inset;
    }


    /*加载数据*/
    @SuppressLint("CheckResult")
    private void loadData() {

        Observable.create((ObservableOnSubscribe<MainInset>) emitter -> {
            MainInset entity = LoadUtils.getData(
                    MovieItem.SOURCE_ASSETS, "test_main_inset",
                    new TypeToken<CYEntity<MainInset>>() {
                    });
            emitter.onNext(entity);
            emitter.onComplete();
        })
                .compose(IOUtils.setThread())
                .subscribe(entity -> {
                    Log.e("加载结果", "entity=" + entity.getList().size());
                    if (entity != null) {
                        if (entity.getBanners() != null) {
                            mBannerAdapter.addDatas(entity.getBanners());
                            getViewDataBinding().banner.scrollToPosition(10000);
                            getViewDataBinding().banner.smoothScrollToPosition(10001);
                        }
                        if (entity.getList() != null)
                            mListAdapter.addDatas(entity.getList());
                    }
                });
    }


    /*推荐适配器*/
    private class BannerAdapter extends BaseRecyclerAdapter<InsetItem, BaseHolder<ItemInsetBannerBD>> {

        @Override
        public int getRealPosition(BaseHolder holder) {
            //重写父方法中的参数
            return holder.getLayoutPosition() % getDatas().size();
        }

        @Override
        public void onBindViewHolder(final BaseHolder viewHolder, int position) {
            //重写父方法中的参数
            int RealPosition = position % getDatas().size();
            super.onBindViewHolder(viewHolder, RealPosition);
        }

        @Override
        public int getItemCount() {
            return getDatas().size() < 2 ? getDatas().size() : Integer.MAX_VALUE;
        }

        @Override
        public void bindData(BaseHolder<ItemInsetBannerBD> viewHolder, int position, InsetItem data) {
//            viewHolder.getBinding().setBanner(data);
            RequestOptions options = new RequestOptions()
                    .override(FCUtils.dp2px(280), FCUtils.dp2px(210));
            Glide.with(FCUtils.getContext()).asBitmap().load(data.getUrl()).apply(options).into(new ImageViewTarget<Bitmap>(viewHolder.getBinding().img) {
                @Override
                protected void setResource(@Nullable Bitmap resource) {
                    getView().setImageBitmap(resource);

                    if (resource != null) {
                        viewHolder.getBinding().main.setDrawingCacheEnabled(true);
                        if (viewHolder.getBinding().main.getDrawingCache() != null) {
                            Bitmap drawingCache = Bitmap.createBitmap(viewHolder.getBinding().main.getDrawingCache());
                            Matrix matrix = new Matrix();
                            matrix.preScale(1, -1); // 实现图片的反转
                            // 创建反转后的图片Bitmap对象
                            Bitmap reflectionImage = Bitmap.createBitmap(drawingCache, 0,
                                    drawingCache.getHeight() - FCUtils.dp2px(60), drawingCache.getWidth(), FCUtils.dp2px(60), matrix, false);
                            //添加遮罩
                            Bitmap bitmapWithReflection = Bitmap.createBitmap(drawingCache.getWidth(),
                                    FCUtils.dp2px(60), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmapWithReflection);
                            canvas.drawBitmap(reflectionImage, 0, 0, null); // 创建画布对象，将原图画于画布，起点是原点位置
                            Paint paint = new Paint();
                            LinearGradient shader = new LinearGradient(0, 0, 0, bitmapWithReflection.getHeight(),
                                    0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);// 创建线性渐变LinearGradient对象
                            paint.setShader(shader); // 绘制
                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//倒影遮罩效果
                            // 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果
                            canvas.drawRect(0, 0, bitmapWithReflection.getWidth(), bitmapWithReflection.getHeight(), paint);
                            viewHolder.getBinding().imgG.setImageBitmap(bitmapWithReflection);
                        }
                    }
                }

            });
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_inset_banner;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }


    /*内置适配器*/
    private class ListAdapter extends BaseRecyclerAdapter<InsetItem, BaseHolder<ItemInsetBD>> {
        int width;

        ListAdapter() {
            width = (CYUtils.getWindowsSize()[0] - FCUtils.dp2px(8) * 3) / 2;
        }

        @Override
        public void bindData(BaseHolder<ItemInsetBD> viewHolder, int position, InsetItem data) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            int height = data.getWidth() > 0 ? width * data.getHeight() / data.getWidth() : 0;
            if (lp.height != height) {
                lp.height = height;
                viewHolder.itemView.setLayoutParams(lp);
            }
            viewHolder.getBinding().setHeight(height);
            viewHolder.getBinding().setItem(data);

            Log.d("****", "url=" + data.getUrl() + "\n" + "width=" + data.getWidth() + "\n" + "height=" + data.getHeight());
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_inset;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }
}
