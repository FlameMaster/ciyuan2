package com.fengchen.ciyuan2.a_movie;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.fengchen.ciyuan2.bean.FCBean;
import com.fengchen.ciyuan2.helper.ItemFullSnapHelper;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.databinding.FgtMovieBD;
import com.fengchen.ciyuan2.databinding.ItemAnimationBD;
import com.fengchen.ciyuan2.databinding.ItemMovieRecommendBD;
import com.fengchen.ciyuan2.databinding.ItemMovieBannerBD;
import com.fengchen.ciyuan2.databinding.TopMovieBD;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.rxjava.fileloader.image.RxImageLoader;
import com.fengchen.light.view.BaseFragment;
import com.fengchen.light.utils.FCUtils;

import java.util.ArrayList;
import java.util.List;

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
public class MovieFragment extends BaseFragment<FgtMovieBD> {
    FCBean fcBean = new FCBean();

    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
            url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";


    TopMovieBD mTopMovieBD;
    BannerAdapter mBannerAdapter;
    ListAdapter mListAdapter;
    RecommendAdapter mRecommendAdapter;

    @Override
    protected void initFragment() {

        ImageParameter imageParameter = new ImageParameter(
                url,
                1280, 720);

        //初始化banner列表
        //给recycle添加整页滑动器
        new PagerSnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        getViewDataBinding().banner
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        mBannerAdapter = new BannerAdapter();
        getViewDataBinding().banner.setAdapter(mBannerAdapter);
        List<ImageParameter> list = new ArrayList<>();
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        mBannerAdapter.addDatas(list);

        getViewDataBinding().setFc(fcBean);
        mBannerAdapter.setOnItemClickListener((viewHolder, position, data) -> {
            FCUtils.showToast(position + "");
            fcBean.getLiveData().postValue("" + position);
            fcBean.getObData().set("" + position);
        });

        //初始化条目列表
        getViewDataBinding().list
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext()));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);

        //添加头
        mTopMovieBD = TopMovieBD.bind(LayoutInflater.from(FCUtils.getContext())
                .inflate(R.layout.top_movie, null));
        mListAdapter.addHeaderBinding(mTopMovieBD);

        //初始化推荐列表
        new ItemFullSnapHelper().attachToRecyclerView(mTopMovieBD.recommend);
        mTopMovieBD.recommend.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mRecommendAdapter = new RecommendAdapter();
        mTopMovieBD.recommend.setAdapter(mRecommendAdapter);
        mRecommendAdapter.addDatas(list);


        List<ImageParameter> list1 = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            list1.add(imageParameter);
        mListAdapter.addDatas(list1);

        mListAdapter.setOnItemClickListener((viewHolder, position, data) -> {
            Intent intent = new Intent(FCUtils.getContext(), VideoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_movie;
    }


    /*内置适配器*/
    private class BannerAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemMovieBannerBD>> {

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
        public void bindData(BaseHolder<ItemMovieBannerBD> viewHolder, int RealPosition, ImageParameter data) {
            RxImageLoader.with().load(data).into(viewHolder.getBinding().img);

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
    private class ListAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemAnimationBD>> {
        @Override
        public void bindData(BaseHolder<ItemAnimationBD> viewHolder, int position, ImageParameter data) {
//            if (position == 0 || position == getDatas().size() - 1) {
//                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
//                if (position == 0)
//                    lp.topMargin = 0;
//                if (position == getDatas().size() - 1)
//                    lp.bottomMargin = FCUtils.dp2px(120);
//                viewHolder.itemView.setLayoutParams(lp);
//            }
            RxImageLoader.with().load(data).into(viewHolder.getBinding().imgCover);

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
    private class RecommendAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemMovieRecommendBD>> {
        @Override
        public void bindData(BaseHolder<ItemMovieRecommendBD> viewHolder, int position, ImageParameter data) {
            if (position == getDatas().size() - 1) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                if (position == getDatas().size() - 1)
                    lp.rightMargin = lp.leftMargin;
                viewHolder.itemView.setLayoutParams(lp);
            }
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
