package com.ct.myim.framework.distruptor.base;

/**
 * @author CAOTAO
 */
public interface MessageConsumer {
    void receive(BaseEvent baseEvent) throws Exception;
}
