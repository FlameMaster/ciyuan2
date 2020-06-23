package com.fengchen.ciyuan2.a_home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.fengchen.ciyuan2.DomeActivity;
import com.fengchen.ciyuan2.bean.MainMovie;
import com.fengchen.ciyuan2.bean.MediaModel;
import com.fengchen.ciyuan2.bean.Movie;
import com.fengchen.ciyuan2.bean.MovieItem;
import com.fengchen.ciyuan2.databinding.LoadMoreBD;
import com.fengchen.ciyuan2.net.CYEntity;
import com.fengchen.ciyuan2.net.CYListEntity;
import com.fengchen.ciyuan2.net.LoadUtils;
import com.fengchen.ciyuan2.utils.DataBindingUtils;
import com.fengchen.ciyuan2.helper.ItemFullSnapHelper;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.a_movie.VideoActivity;
import com.fengchen.ciyuan2.databinding.FgtHomeBD;
import com.fengchen.ciyuan2.databinding.ItemHomeBD;
import com.fengchen.ciyuan2.databinding.ItemHomeBannerBD;
import com.fengchen.ciyuan2.wiget.ParallaxLayout;
import com.fengchen.ciyuan2.wiget.RoundGifImageView;
import com.fengchen.ciyuan2.wiget.RoundLayout;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.http.EmptyState;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.model.StateModel;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.utils.IOUtils;
import com.fengchen.light.utils.StringUtil;
import com.fengchen.light.view.BaseFragment;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class HomeFragment extends BaseFragment<FgtHomeBD> {

    final String url = ".jpg",
            url2 = "https://i0.hdslb.com/bfs/article/dd7596b659a8b448e823e3155a7f35dd7baf0b43.png";


    RecommendAdapter mRecommendAdapter;
    ListAdapter mListAdapter;

    @Override
    protected void initFragment() {

        //初始化推荐列表
        new ItemFullSnapHelper().attachToRecyclerView(getViewDataBinding().recommend);
        getViewDataBinding().recommend.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mRecommendAdapter = new RecommendAdapter();
        getViewDataBinding().recommend.setAdapter(mRecommendAdapter);
        //初始化条目列表
        getViewDataBinding().list
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext()));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);


        List<ImageParameter> list1 = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            list1.add(new ImageParameter(url2, FCUtils.dp2px(360), FCUtils.dp2px(160)));
        mListAdapter.addDatas(list1);

        mRecommendAdapter.setOnItemClickListener(
                (BaseRecyclerAdapter.OnItemClickListener<MovieItem>) (viewHolder, position, data) -> {
                    Intent intent = new Intent(FCUtils.getContext(), VideoActivity.class);
                    intent.putExtra("dataSource", data.getDataSource());
                    intent.putExtra("dataPath", data.getDataPath());
                    startActivity(intent);
                });
        mListAdapter.setOnItemClickListener((viewHolder, position, data) -> {
        });
        initData();

        getViewDataBinding().banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testC(v);
            }
        });
        getViewDataBinding().bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),DomeActivity.class));
            }
        });
        //尾布局
        LoadMoreBD loadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(
                FCUtils.getContext()), R.layout.item_loadmore, null, false);
        loadMoreBinding.setState(new StateModel(EmptyState.USER_DEFINED).setUserText(""));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = FCUtils.dp2px(108);
        loadMoreBinding.getRoot().setLayoutParams(lp);
        if (mListAdapter.getTailSize() <= 0) mListAdapter.addTailBinding(loadMoreBinding);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_home;
    }

    public void testC(View view) {
        getViewDataBinding().recommend.getChildAt(0).setDrawingCacheEnabled(true);
        Bitmap drawingCache = Bitmap.createBitmap(getViewDataBinding().recommend.getChildAt(0).getDrawingCache());
        ((ImageView)view).setImageBitmap(drawingCache);
    }


    @SuppressLint("CheckResult")
    private void initData() {
        getViewDataBinding().setBannerUrl("https://b-ssl.duitang.com/uploads/item/201801/08/20180108181057_VSyJF.gif");
//        getViewDataBinding().setBannerUrl("http://img2.100bt.com/upload/ttq/20140606/1402032172085.gif");

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
                        if (entity.getRecommend() != null)
                            mRecommendAdapter.addDatas(entity.getRecommend());
                    }
                });
    }

    /*推荐适配器*/
    private class RecommendAdapter extends BaseRecyclerAdapter<MovieItem, BaseHolder<ItemHomeBannerBD>> {
        @Override
        public void bindData(BaseHolder<ItemHomeBannerBD> viewHolder, int position, MovieItem data) {
            if (position == getDatas().size() - 1) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                lp.rightMargin = lp.leftMargin;
                viewHolder.itemView.setLayoutParams(lp);
            }
            viewHolder.getBinding().setRecommend(data);
            viewHolder.getBinding().setTop(position + 1);
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_home_recommend;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }


    /*内置适配器*/
    private class ListAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemHomeBD>> {
        @Override
        public void bindData(BaseHolder<ItemHomeBD> viewHolder, int position, ImageParameter data) {
//            if (position == getDatas().size() - 1) {
//                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
//                    lp.bottomMargin = FCUtils.dp2px(120);
//                viewHolder.itemView.setLayoutParams(lp);
//            }

//            if (position == 1) {
//                data.setUrl("https://b-ssl.duitang.com/uploads/blog/201407/19/20140719173322_arNuS.jpeg");
//                data.setHeight(FCUtils.dp2px(320));
//                getViewDataBinding().container.setOnScrollChangeListener(
//                        (NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) ->
//                                viewHolder.getBinding().root.moveChild(getViewDataBinding().getRoot().getHeight()));
//                ParallaxLayout.LayoutParams lp = (ParallaxLayout.LayoutParams)
//                        viewHolder.getBinding().img.getLayoutParams();
//                lp.height = FCUtils.dp2px(320);
//                viewHolder.getBinding().img.setLayoutParams(lp);
//            }

            DataBindingUtils.loadImageUrl(viewHolder.getBinding().img, data.getUrl(),
                    data.getWidth(), data.getHeight());
        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_home;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }
}
