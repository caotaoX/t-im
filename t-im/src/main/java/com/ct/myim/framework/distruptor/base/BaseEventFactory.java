package com.ct.myim.framework.distruptor.base;


import com.lmax.disruptor.EventFactory;

/**
 * @author CAOTAO
 */
public class BaseEventFactory implements EventFactory<BaseEvent> {
    @Override
    public BaseEvent newInstance() {
        return new BaseEvent();
    }

}
