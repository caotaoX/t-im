package com.ct.myim.framework.config;

import com.ct.myim.framework.distruptor.DisruptorProducer;
import com.ct.myim.framework.distruptor.DisruptorServiceHandler;
import com.ct.myim.framework.distruptor.base.BaseEvent;
import com.ct.myim.framework.distruptor.base.BaseEventFactory;
import com.ct.myim.framework.distruptor.base.MessageProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author CAOTAO
 */
@Configuration
public class DisruptorConfig {

    @Value("${disruptor.number}")
    private int numbe;

    @Value("${disruptor.bufferSize}")
    private int bufferSize;

    private Disruptor<BaseEvent> disruptor() {
        ThreadFactory producerFactory = Executors.defaultThreadFactory();
        BaseEventFactory eventFactory = new BaseEventFactory();
        Disruptor<BaseEvent> disruptor = new Disruptor<>(eventFactory, bufferSize, producerFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());
        //任何消息都会同时被多个消费者消费，消费者会根据type来判断哪个是该自己处理的
        //disruptor.handleEventsWith(new DisruptorServiceHandlerAll());
        //消息在多个消费者中只消费一次
        DisruptorServiceHandler[] disruptorServiceHandlers = new DisruptorServiceHandler[numbe];
        for (int i = 0; i < numbe; i++) {
            disruptorServiceHandlers[i] = new DisruptorServiceHandler();
        }
        disruptor.handleEventsWithWorkerPool(disruptorServiceHandlers);
//        disruptor.handleEventsWithWorkerPool(new DisruptorServiceHandler());
        disruptor.start();
        return disruptor;
    }

    @Bean
    public MessageProducer messageProducer() {
        return new DisruptorProducer(disruptor());
    }
}
