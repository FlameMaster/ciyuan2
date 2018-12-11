package com.fengchen.light.rxjava;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * ===========================================================
 * = 作 者：风 尘
 * <p>
 * = 版 权 所 有：7416064@qq.com
 * <p>
 * = 地 点：中 国 北 京 市 朝 阳 区
 * <p>
 * = 时 间：2018/12/3 18:34
 * <p>
 * = 分 类 说 明：支持背压的通讯器
 * ============================================================
 */
public class RxBus {

    private final FlowableProcessor<Object> mBus;

    private RxBus() {
        // toSerialized method made bus thread safe
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    public void post(Object obj) {
        mBus.onNext(obj);
    }

    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Flowable<Object> toFlowable() {
        return mBus;
    }

    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }




//        RxBus.get().toFlowable().map(new Function<Object, EventMessage>() {
//        @Override
//        public EventMessage apply(Object o){
//            return (EventMessage) o;
//        }
//    }).subscribe(new Consumer<EventMessage>() {
//        @Override
//        public void accept(EventMessage eventMsg){
//            if (eventMsg != null) {
//                getViewDataBinding().v1.setText(eventMsg.getMessage());
//            }
//        }
//    });
//
//    EventMessage eventMsg = new EventMessage();
//        eventMsg.setMessage("来自第二个页面发送过来的数据 --- 修改成功");
//        RxBus.get().post(eventMsg);
}
