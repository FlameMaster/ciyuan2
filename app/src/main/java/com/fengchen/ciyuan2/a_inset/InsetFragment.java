package com.fengchen.ciyuan2.a_inset;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.fengchen.ciyuan2.utils.DataBindingUtils;
import com.fengchen.ciyuan2.R;
import com.fengchen.ciyuan2.a_movie.VideoActivity;
import com.fengchen.ciyuan2.databinding.FgtInsetBD;
import com.fengchen.ciyuan2.databinding.ItemInsetBD;
import com.fengchen.ciyuan2.databinding.ItemInsetBannerBD;
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
public class InsetFragment extends BaseFragment<FgtInsetBD> {

    final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
            url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";


    BannerAdapter mBannerAdapter;
    ListAdapter mListAdapter;

    @Override
    protected void initFragment() {

        ImageParameter imageParameter = new ImageParameter(
                url,
                1280, 720);

        //初始化推荐列表
        new PagerSnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        getViewDataBinding().banner.setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
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
        getViewDataBinding().banner.scrollToPosition(10000);
        getViewDataBinding().banner.smoothScrollToPosition(10001);
        getViewDataBinding().banner.addOnScrollListener(new RecyclerView.OnScrollListener() {

            float MIN_SCALE = 0.65f;//当前图片缩小比例

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
                    float fraction = gap * 1.0f / recyclerView.getWidth() / 2;
                    scale(child, fraction);
                }
            }

            private void scale(View child, float fraction) {
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(fraction));
                child.setScaleX(scaleFactor);
                child.setScaleY(scaleFactor);
            }
        });


        //初始化条目列表
//        getViewDataBinding().list
//                .setLayoutManager(new TestLayoutManager(2));
        getViewDataBinding().list
                .setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mListAdapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(mListAdapter);


        List<ImageParameter> list1 = new ArrayList<>();
        for (int i = 0; i < 40; i++)
            list1.add(imageParameter);
        mListAdapter.addDatas(list1);

        mListAdapter.setOnItemClickListener((viewHolder, position, data) -> {
            Intent intent = new Intent(FCUtils.getContext(), VideoActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_inset;
    }


    /*推荐适配器*/
    private class BannerAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemInsetBannerBD>> {

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
        public void bindData(BaseHolder<ItemInsetBannerBD> viewHolder, int position, ImageParameter data) {

            DataBindingUtils.loadImageUrl(viewHolder.getBinding().img, data.getUrl(),
                    viewHolder.getBinding().img.getWidth(), viewHolder.getBinding().img.getHeight());
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
    private class ListAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemInsetBD>> {
        @Override
        public void bindData(BaseHolder<ItemInsetBD> viewHolder, int position, ImageParameter data) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            if (position%5 == 0) lp.height = FCUtils.dp2px(240);
            if (position%5 == 1) lp.height = FCUtils.dp2px(170);
            if (position%5 == 2) lp.height = FCUtils.dp2px(140);
            if (position%5 == 3) lp.height = FCUtils.dp2px(330);
            if (position%5 == 4) lp.height = FCUtils.dp2px(210);

            viewHolder.itemView.setLayoutParams(lp);

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
