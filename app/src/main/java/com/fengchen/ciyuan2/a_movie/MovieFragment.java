package com.fengchen.ciyuan2.a_movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fengchen.ciyuan2.databinding.LoadMoreBD;
import com.fengchen.ciyuan2.manager.Banner01Manager;
import com.fengchen.ciyuan2.manager.Banner01SnapHelper;
import com.fengchen.ciyuan2.RxBusClient;
import com.fengchen.ciyuan2.bean.FCBean;
import com.fengchen.ciyuan2.bean.MainMovie;
import com.fengchen.ciyuan2.bean.MovieItem;
import com.fengchen.ciyuan2.helper.ItemFullSnapHelper;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.databinding.FgtMovieBD;
import com.fengchen.ciyuan2.databinding.ItemAnimationBD;
import com.fengchen.ciyuan2.databinding.ItemMovieRecommendBD;
import com.fengchen.ciyuan2.databinding.ItemMovieBannerBD;
import com.fengchen.ciyuan2.databinding.TopMovieBD;
import com.fengchen.ciyuan2.net.CYEntity;
import com.fengchen.ciyuan2.net.CYListEntity;
import com.fengchen.ciyuan2.net.LoadUtils;
import com.fengchen.ciyuan2.utils.CYUtils;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.http.EmptyState;
import com.fengchen.light.model.StateModel;
import com.fengchen.light.utils.IOUtils;
import com.fengchen.light.utils.StringUtil;
import com.fengchen.light.view.BaseFragment;
import com.fengchen.light.utils.FCUtils;
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
 * = 时 间：2018/12/4 11:36
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class MovieFragment extends BaseFragment<FgtMovieBD> implements BaseRecyclerAdapter.OnItemClickListener<MovieItem> {
    FCBean fcBean = new FCBean();

    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
            url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";


    TopMovieBD mTopMovieBD;
    BannerAdapter mBannerAdapter;
    ListAdapter mListAdapter;
    RecommendAdapter mRecommendAdapter;

    RxBusClient mRxBusClient = new RxBusClient() {
        @Override
        protected void onEvent(int type, String message, Object data) {
            if (StringUtil.noNull(message)) {
            }
        }
    };

    @Override
    protected void initFragment() {

        //初始化banner列表
        //给recycle添加整页滑动器
//        new PagerSnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        new Banner01SnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        Banner01Manager bannerManager =new Banner01Manager();
        getViewDataBinding().banner .setLayoutManager(bannerManager);
        mBannerAdapter = new BannerAdapter();
        getViewDataBinding().banner.setAdapter(mBannerAdapter);

        getViewDataBinding().setFc(fcBean);

        //初始化条目列表
        getViewDataBinding().list
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext()));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);

        //添加头
        mTopMovieBD = TopMovieBD.bind(LayoutInflater.from(FCUtils.getContext())
                .inflate(R.layout.top_movie, null));
        mListAdapter.addHeaderBinding(mTopMovieBD);
        //尾布局
        LoadMoreBD loadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(
                FCUtils.getContext()), R.layout.item_loadmore, null, false);
        loadMoreBinding.setState(new StateModel(EmptyState.USER_DEFINED).setUserText(""));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = FCUtils.dp2px(108);
        loadMoreBinding.getRoot().setLayoutParams(lp);
        if (mListAdapter.getTailSize() <= 0) mListAdapter.addTailBinding(loadMoreBinding);

        //初始化推荐列表
        new ItemFullSnapHelper().attachToRecyclerView(mTopMovieBD.recommend);
        mTopMovieBD.recommend.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mRecommendAdapter = new RecommendAdapter();
        mTopMovieBD.recommend.setAdapter(mRecommendAdapter);


        mBannerAdapter.setOnItemClickListener(this);
        mRecommendAdapter.setOnItemClickListener(this);
        mListAdapter.setOnItemClickListener(this);

        loadData();

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_movie;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxBusClient.unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Fc", "\n\n" + FCUtils.px2dp(getViewDataBinding().list.getHeight()) + "\n" + FCUtils.px2dp(getViewDataBinding().list.getWidth()));
    }

////////////////////——————————————————————数据————————————————————————————————////////////////////

    /*加载数据*/
    @SuppressLint("CheckResult")
    private void loadData() {

        Observable.create((ObservableOnSubscribe<MainMovie>) emitter -> {
            MainMovie entity = LoadUtils.getData(
                    MovieItem.SOURCE_ASSETS, "test_main_movie",
                    new TypeToken<CYEntity<MainMovie>>() {
                    });
            emitter.onNext(entity);
            emitter.onComplete();
        })
                .compose(IOUtils.setThread())
                .subscribe(entity -> {
                    Log.e("加载结果", "entity=" + entity.getList().size());
                    if (entity != null) {
                        if (entity.getBanners() != null)
                            mBannerAdapter.addDatas(entity.getBanners());
                        if (entity.getRecommend() != null)
                            mRecommendAdapter.addDatas(entity.getRecommend());
                        if (entity.getList() != null)
                            mListAdapter.addDatas(entity.getList());
                    }
                });
    }

    /*加载数据*/
    @SuppressLint("CheckResult")
    private void loadData(int page) {

        Observable.create((ObservableOnSubscribe<CYListEntity<MovieItem>>) emitter -> {
            CYListEntity<MovieItem> entity = LoadUtils.getData(
                    MovieItem.SOURCE_ASSETS, "movie_list_test",
                    new TypeToken<CYEntity<CYListEntity<MovieItem>>>() {
                    });
            emitter.onNext(entity);
            emitter.onComplete();
        })
                .compose(IOUtils.setThread())
                .subscribe(entity -> {
                    Log.e("加载结果", "entity=" + entity.getList().size());
                    if (entity != null && entity.getList() != null)
                        mListAdapter.addDatas(entity.getList());
                });
    }

    @Override
    public void onItemClick(BaseHolder viewHolder, int position, MovieItem data) {
        Intent intent = new Intent(FCUtils.getContext(), VideoActivity.class);
        intent.putExtra("dataSource", data.getDataSource());
        intent.putExtra("dataPath", data.getDataPath());
        startActivity(intent);
    }

////////////////////——————————————————————适配器————————————————————————————————////////////////////


    /*内置适配器*/
    private class BannerAdapter extends BaseRecyclerAdapter<MovieItem, BaseHolder<ItemMovieBannerBD>> {

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
        public void bindData(BaseHolder<ItemMovieBannerBD> viewHolder, int RealPosition, MovieItem data) {
//            RxImageLoader.with().load(data).into(viewHolder.getBinding().img);
            viewHolder.getBinding().setBanner(data);


        }

        /*根据图片亮度改变背景色*/
        private void updataTitleBackground(View view, ImageView img){
            //view重绘时回调
//            viewHolder.getBinding().img.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
//                @Override
//                public void onDraw() {
//                    if (viewHolder.getBinding().img.getDrawable() != null) {
//                        updataTitleBackground(viewHolder.getBinding().title,viewHolder.getBinding().img);
//                    }
//                }
//            });
            Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
            int bright = CYUtils.getBright(bitmap);
            Log.e("图片亮度查询", "\n\n" + bright);
            view.setBackgroundColor(bright<128?0x55ffffff:0x55000000);
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_movie_banner;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }


    /*内置适配器*/
    private class ListAdapter extends BaseRecyclerAdapter<MovieItem, BaseHolder<ItemAnimationBD>> {
        @Override
        public void bindData(BaseHolder<ItemAnimationBD> viewHolder, int position, MovieItem data) {
            viewHolder.getBinding().setItem(data);
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_movie;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }


    /*推荐适配器*/
    private class RecommendAdapter extends BaseRecyclerAdapter<MovieItem, BaseHolder<ItemMovieRecommendBD>> {
        @Override
        public void bindData(BaseHolder<ItemMovieRecommendBD> viewHolder, int position, MovieItem data) {
            if (position == getDatas().size() - 1) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                if (position == getDatas().size() - 1)
                    lp.rightMargin = lp.leftMargin;
                viewHolder.itemView.setLayoutParams(lp);
            }
            viewHolder.getBinding().setRecommend(data);
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_movie_recommend;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }
}
