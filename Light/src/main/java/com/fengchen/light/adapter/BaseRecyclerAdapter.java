package com.fengchen.light.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fengchen.light.R;
import com.fengchen.light.utils.FCUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ===============================================
 * = 作 者：风 尘
 * <p/>
 * = 版 权 所 有：有 璟 阁 股 份 有 限 公 司
 * <p/>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p/>
 * = 时 间：2015/12/28 18:11
 * <p/>
 * = 功 能；可添加头布局和设置点击事件的recyclerview
 * ================================================
 */
public abstract class BaseRecyclerAdapter<T, VH extends BaseHolder> extends RecyclerView.Adapter<BaseHolder> {

    /*正常布局*/
    public static final int TYPE_NORMAL = -1;

    //——————————————————————————————————————————————————————————————————————————————————————//

    /*数据集合*/
    private List<T> mDatas;
    /*头条目集合*/
    private List<ViewDataBinding> mHeaderBindings;
    /*包含镶嵌布局位置和布局的集合*/
    private SparseArray<ViewDataBinding> mInsertBindings;
    /*尾条目集合*/
    private List<ViewDataBinding> mTailBindings;
    /*条目点击事件*/
    private OnItemClickListener mListener;

    public BaseRecyclerAdapter() {
        super();
        mDatas = new ArrayList<>();
        mHeaderBindings = new ArrayList<>();
        mInsertBindings = new SparseArray<>();
        mTailBindings = new ArrayList<>();
    }

    /**
     * 设置条目点击事件
     */
    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    /**
     * 根据位置返回保存的数据
     */
    public T getData(int position) {
        return mDatas.get(position);
    }

    /**
     * 返回所有数据的集合
     */
    public List<T> getDatas() {
        return mDatas;
    }

    /**
     * 根据位置获取view
     *
     * @param position 位置
     * @return 镶嵌布局
     */
    public ViewDataBinding getInsertBinding(int position) {
        //头
        if (position < mHeaderBindings.size())
            return mHeaderBindings.get(position);
            //尾
        else if (position >= mHeaderBindings.size() + mDatas.size() + mInsertBindings.size())
            return mTailBindings.get(position - mHeaderBindings.size() - mDatas.size() - mInsertBindings.size());
        Log.e("err****", "position=" + position);
        //镶嵌布局
        return mInsertBindings.get(position);
    }

    /**
     * 添加头布局
     * <p>
     * 每次添加都会把上一次的头挤下去
     */
    public void addHeaderBinding(ViewDataBinding binding) {
        if (binding==null)return;
        if (mHeaderBindings.size() == 0) {
            mHeaderBindings.add(DataBindingUtil.inflate(
                    LayoutInflater.from(FCUtils.getContext()), R.layout.null_layout, null, false));
        }
        mHeaderBindings.add(binding);

//        notifyItemInserted(1);
    }

    /**
     * @return 头布局数量
     */
    public int getHeaderSize() {
        return mHeaderBindings.size();
    }

    /**
     * 添加一个镶嵌布局，在其它条目初始化之后
     *
     * @param position 位置
     * @param binding  镶嵌的布局
     */
    public void addInsertBinding(int position, ViewDataBinding binding) {
        //添加进集合
        mInsertBindings.put(position, binding);
//        notifyDataSetChanged();
        notifyItemInserted(position);
    }

    /**
     * @return 头布局数量
     */
    public int getInsertSize() {
        return mInsertBindings.size();
    }

    /**
     * 添加尾布局
     * <p>
     * 每次添加都会把上一次的尾挤上去
     */
    public void addTailBinding(ViewDataBinding binding) {
        if (binding==null)return;
        if (mHeaderBindings.size() == 0) {
            mHeaderBindings.add(DataBindingUtil.inflate(
                    LayoutInflater.from(FCUtils.getContext()), R.layout.null_layout, null, false));
        }
        mTailBindings.add(binding);

        //执行item动画
//        notifyItemInserted(getHeaderSize() + mDatas.size() + getTailSize() - 1);
    }

    /**
     * 删除一个尾布局
     * @param position 删除的尾布局位置
     */
    public void removedTail(int position) {
        mTailBindings.remove(position);
        notifyItemRemoved(getHeaderSize() + mDatas.size() + getInsertSize() + position);
    }

    /**
     * @return 头布局数量
     */
    public int getTailSize() {
        return mTailBindings.size();
    }

    /**
     * 添加数据
     */
    public void addDatas(List datas) {

        if (datas==null&&datas.size()<=0)return;

        mDatas.addAll(datas);//添加数据
        //执行item动画
        notifyItemRangeInserted(mHeaderBindings.size(), datas.size());
    }

    /**
     * 添加数据
     */
    public void addData(T data) {
        if (data==null)return;
        mDatas.add(data);
        //执行item动画
        notifyItemInserted(mHeaderBindings.size() + mDatas.size() - 1);
    }

    /**
     * 添加数据到前排
     */
    public void addTopData(T data) {
        if (data==null)return;
        mDatas.add(0, data);
        notifyItemInserted(mHeaderBindings.size());
    }

    /**
     * 删除所有数据，一条一条删
     */
    public void clearDatas() {
        int size2 = mTailBindings.size();
        int size1 = size2 + mDatas.size() + mInsertBindings.size();
        int size = size1 + mHeaderBindings.size();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (i >= size1) {
                    mHeaderBindings.remove(0);
                } else if (i >= size2) {
                    if (mInsertBindings.get(i) != null)
                        mInsertBindings.remove(i);
                    else
                        mDatas.remove(0);
                } else {
                    mTailBindings.remove(0);
                }
            }
            //执行item动画
            notifyItemRangeRemoved(0, size);
        }
    }

    /*直接删除所有数据*/
    public void clearAllData() {
        mHeaderBindings.clear();
        mTailBindings.clear();
        mInsertBindings.clear();
        clearData();
    }

    /*直接删除Data所有数据*/
    public void clearData() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 根据位置判断条目类型
     */
    @Override
    public int getItemViewType(int position) {

        //没有镶嵌布局时返回正常布局类型-1
        if (mHeaderBindings.size() == 0 && mInsertBindings.size() == 0) return TYPE_NORMAL;

        //镶嵌布局返回所在位置
        if (position < mHeaderBindings.size()
                || position >= mHeaderBindings.size() + mDatas.size() + mInsertBindings.size()
                || (mInsertBindings.size() > 0 && mInsertBindings.get(position) != null))
            return position;

        //默认布局
        return TYPE_NORMAL;
    }

    /**
     * 初始化条目
     */
    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        if (viewType != TYPE_NORMAL)
            return onCustomCreate(getInsertBinding(viewType), viewType);

        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                getItemLayoutId(viewType),
                parent,
                false);

        return onCreate(binding, viewType);
    }

    /**
     * 初始化对应条目数据
     */
    @Override
    public void onBindViewHolder(final BaseHolder viewHolder, int position) {
        //判断布局类型执行相关超作
        if (getItemViewType(position) != TYPE_NORMAL) {
            bindCustomData(viewHolder, position, getItemViewType(position));
        } else {
            final int pos = getRealPosition(viewHolder);
            final T data = mDatas.get(pos);
            bindData((VH) viewHolder, pos, data);

            if (mListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(viewHolder, pos, data);
                    }
                });
            }
        }
    }

    /**
     * 使Grid布局时头布局独占一行
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) != TYPE_NORMAL ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 使用瀑布流时使头布局独占一行
     */
    @Override
    public void onViewAttachedToWindow(BaseHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (getHeaderSize() > 0 && lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            boolean fullSpan = false;
            if (holder.getLayoutPosition() < getHeaderSize()) fullSpan = true;//头布局
            if (holder.getLayoutPosition() >= getHeaderSize() + getDatas().size() + getInsertSize())
                fullSpan = true;//尾布局
            p.setFullSpan(fullSpan);
        }
    }

    /**
     * 获取当前条目位置,此功能只对普通条目有效，镶嵌条目不计算
     */
    public int getRealPosition(BaseHolder holder) {

        int position = holder.getLayoutPosition();
        //减去头布局数量
        int realPosition = position - mHeaderBindings.size();

        //遍历map中的键
        for (int i = 0; i < mInsertBindings.size(); i++) {
            int index = mInsertBindings.keyAt(i);
            if (index <= realPosition)
                realPosition--;

        }
        return realPosition;
    }

    /**
     * 返回总共的条目数
     */
    @Override
    public int getItemCount() {
        return mDatas.size() + mHeaderBindings.size() + mInsertBindings.size() + mTailBindings.size();
    }

    /**
     * 对条目数据初始化
     */
    public abstract void bindData(VH viewHolder, int RealPosition, T data);

    /**
     * 对特殊条目的初始化
     */
    protected abstract void bindCustomData(BaseHolder viewHolder, int position, int itemViewType);

    /**
     * 布局引用
     */
    public abstract int getItemLayoutId(int viewType);

    /**
     * 初始化
     */
    protected abstract VH onCreate(ViewDataBinding binding, int viewType);

    /**
     * 可以被继承的特殊条目初始化
     *
     * @param insertBinding
     * @param viewType
     * @return
     */
    public BaseHolder onCustomCreate(ViewDataBinding insertBinding, int viewType) {
        return new BaseHolder(insertBinding);
    }


    /*条目点击事件接口*/
    public interface OnItemClickListener<T> {
        void onItemClick(BaseHolder viewHolder, int position, T data);
    }
}
