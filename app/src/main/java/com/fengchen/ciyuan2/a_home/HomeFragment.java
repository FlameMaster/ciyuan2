package com.fengchen.ciyuan2.a_home;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fengchen.ciyuan2.utils.DataBindingUtils;
import com.fengchen.ciyuan2.helper.ItemFullSnapHelper;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.a_movie.VideoActivity;
import com.fengchen.ciyuan2.databinding.FgtHomeBD;
import com.fengchen.ciyuan2.databinding.ItemHomeBD;
import com.fengchen.ciyuan2.databinding.ItemHomeBannerBD;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.utils.FCUtils;
import com.fengchen.light.view.BaseFragment;

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
 * = 时 间：2019/1/26 14:50
 * <p>
 * = 分 类 说 明：
 * ============================================================
 */
public class HomeFragment extends BaseFragment<FgtHomeBD> {

    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
            url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";


    RecommendAdapter mRecommendAdapter;
    ListAdapter mListAdapter;

    @Override
    protected void initFragment() {

        ImageParameter imageParameter = new ImageParameter(
                url,
                1280, 720);

        //初始化推荐列表
        new ItemFullSnapHelper().attachToRecyclerView(getViewDataBinding().recommend);
        getViewDataBinding().recommend.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mRecommendAdapter = new RecommendAdapter();
        getViewDataBinding().recommend.setAdapter(mRecommendAdapter);
        List<ImageParameter> list = new ArrayList<>();
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        mRecommendAdapter.addDatas(list);


        //初始化条目列表
        getViewDataBinding().list
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext()));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);


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
        return R.layout.fragment_home;
    }


    /*推荐适配器*/
    private class RecommendAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemHomeBannerBD>> {
        @Override
        public void bindData(BaseHolder<ItemHomeBannerBD> viewHolder, int position, ImageParameter data) {
            if (position == getDatas().size() - 1) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                    lp.rightMargin = lp.leftMargin;
                viewHolder.itemView.setLayoutParams(lp);
            }

            DataBindingUtils.loadImageUrl(viewHolder.getBinding().img,data.getUrl(),
                    viewHolder.getBinding().img.getWidth(),viewHolder.getBinding().img.getHeight());
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

            DataBindingUtils.loadImageUrl(viewHolder.getBinding().img,url2,
                    viewHolder.getBinding().img.getWidth(),viewHolder.getBinding().img.getHeight());
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
