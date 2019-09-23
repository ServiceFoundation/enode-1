package org.enodeframework.samples.controller;

import org.enodeframework.queue.TopicData;
import org.enodeframework.rocketmq.message.RocketMQCommandService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.context.annotation.Bean;

import static org.enodeframework.samples.QueueProperties.COMMAND_TOPIC;
import static org.enodeframework.samples.QueueProperties.DEFAULT_PRODUCER_GROUP;
import static org.enodeframework.samples.QueueProperties.NAMESRVADDR;

public class RocketMQConfig {
    @Bean
    public RocketMQCommandService rocketMQCommandService(DefaultMQProducer producer) {
        RocketMQCommandService rocketMQCommandService = new RocketMQCommandService();
        rocketMQCommandService.setDefaultMQProducer(producer);
        TopicData topicData = new TopicData(COMMAND_TOPIC, "*");
        rocketMQCommandService.setTopicData(topicData);
        return rocketMQCommandService;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer commandProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(NAMESRVADDR);
        producer.setProducerGroup(DEFAULT_PRODUCER_GROUP);
        return producer;
    }
}
