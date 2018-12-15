package com.fengchen.ciyuan2;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.util.Log;

import com.fengchen.ciyuan2.databinding.FragmentHomeBinding;
import com.fengchen.ciyuan2.databinding.ItemHomeBannerBinding;
import com.fengchen.ciyuan2.databinding.ItemAnimationBinding;
import com.fengchen.light.adapter.BaseHolder;
import com.fengchen.light.adapter.BaseRecyclerAdapter;
import com.fengchen.light.model.ImageParameter;
import com.fengchen.light.rxjava.fileloader.image.RxImageLoader;
import com.fengchen.light.view.BaseFragment;
import com.fengchen.light.utils.FCUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    FCBean fcBean = new FCBean();

    @Override
    protected void initFragment() {

        final String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1544282968&di=b71095ebeb311984002154ed4d483844&imgtype=jpg&er=1&src=http%3A%2F%2Fi1.hdslb.com%2Fbfs%2Farchive%2Fd926af39dbdbf60f2f149377c4b76371e4447532.jpg",
                url2 = "https://imgweb.8868.com/jiaoyi_manage_imgs/36f14d32f68e449bb2d32f766c558a9c.gif";

        ImageParameter imageParameter = new ImageParameter(
                url,
                1280, 720);

        //给recycle添加整页滑动器
        new PagerSnapHelper().attachToRecyclerView(getViewDataBinding().banner);
        getViewDataBinding().banner
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        BannerAdapter banner_adapter = new BannerAdapter();
        getViewDataBinding().banner.setAdapter(banner_adapter);
        List<ImageParameter> list = new ArrayList<>();
        list.add(imageParameter);
        list.add(imageParameter);
        list.add(imageParameter);
        banner_adapter.addDatas(list);

        getViewDataBinding().setFc(fcBean);
        banner_adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseHolder viewHolder, int position, Object data) {
                FCUtils.showToast(position + "");
                fcBean.liveData.postValue("" + position);
                fcBean.obData.set("" + position);
            }
        });

        //
        getViewDataBinding().list
                .setLayoutManager(new LinearLayoutManager(FCUtils.getContext()));
        ListAdapter list_adapter = new ListAdapter();
        getViewDataBinding().list.setAdapter(list_adapter);

        List<ImageParameter> list1 = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            list1.add(imageParameter);
        list_adapter.addDatas(list1);

        Observable
                .create(new ObservableOnSubscribe<String>() {

                    @Override
                    public void subscribe(ObservableEmitter<String> emitter){
                        emitter.onNext("tt");
                        emitter.onComplete();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e("fff",""+s);
                    }
                });
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_home;
    }


    /*内置适配器*/
    private class BannerAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemHomeBannerBinding>> {

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
        public void bindData(BaseHolder<ItemHomeBannerBinding> viewHolder, int RealPosition, ImageParameter data) {
            RxImageLoader.with().load(data).into(viewHolder.getBinding().img);

        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_home_banner;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }


    /*内置适配器*/
    private class ListAdapter extends BaseRecyclerAdapter<ImageParameter, BaseHolder<ItemAnimationBinding>> {
        @Override
        public void bindData(BaseHolder<ItemAnimationBinding> viewHolder, int RealPosition, ImageParameter data) {
            RxImageLoader.with().load(data).into(viewHolder.getBinding().imgCover);

        }

        @Override
        protected void bindCustomData(BaseHolder viewHolder, int position, int itemViewType) {

        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_animation;
        }

        @Override
        protected BaseHolder onCreate(ViewDataBinding binding, int viewType) {
            return new BaseHolder(binding);
        }
    }
}
