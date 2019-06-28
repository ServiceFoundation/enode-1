package com.enodeframework.rocketmq.message;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.enodeframework.common.serializing.JsonTool;
import com.enodeframework.queue.QueueMessage;
import com.enodeframework.queue.applicationmessage.AbstractApplicationMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author anruence@gmail.com
 */
public class RocketMQApplicationMessageListener extends AbstractApplicationMessageListener implements MessageListenerConcurrently {

    private final Logger logger = LoggerFactory.getLogger(RocketMQApplicationMessageListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            QueueMessage queueMessage = RocketMQTool.covertToQueueMessage(msgs);
            handle(queueMessage, message -> {
            });
        } catch (Exception e) {
            logger.error("Ops, consume ApplicationMessage failed, msgs:{}", JsonTool.serialize(msgs), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
