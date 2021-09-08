package com.ct.myim.framework.distruptor.base;

/**
 * @author CAOTAO
 */
public interface MessageProducer {
    void publish(BaseEvent baseEvent);
}
