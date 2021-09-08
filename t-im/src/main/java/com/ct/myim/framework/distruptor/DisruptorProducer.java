package com.ct.myim.framework.distruptor;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * 所有消息，都进入这里，然后publish出去，供消费者消费
 * @author CAOTAO
 */
public class DisruptorProducer implements MessageProducer {
    private Disruptor<BaseEvent> disruptor;


    public DisruptorProducer(Disruptor<BaseEvent> disruptor) {
        this.disruptor = disruptor;
    }

    @Override
    public void publish(BaseEvent baseEvent) {
        RingBuffer<BaseEvent> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();
        try {
            BaseEvent event = ringBuffer.get(sequence);
            event.setMsg(baseEvent.getMsg());
         }finally {
            ringBuffer.publish(sequence);
        }
    }
}
