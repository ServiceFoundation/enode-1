package org.enodeframework.queue.domainevent;

import org.enodeframework.common.serializing.JsonTool;
import org.enodeframework.common.utilities.Ensure;
import org.enodeframework.eventing.DomainEventStreamMessage;
import org.enodeframework.eventing.IEventSerializer;
import org.enodeframework.messaging.IMessagePublisher;
import org.enodeframework.queue.QueueMessage;
import org.enodeframework.queue.QueueMessageTypeCode;
import org.enodeframework.queue.TopicData;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDomainEventPublisher implements IMessagePublisher<DomainEventStreamMessage> {
    @Autowired
    protected IEventSerializer eventSerializer;

    private TopicData topicData;

    public void setEventSerializer(IEventSerializer eventSerializer) {
        this.eventSerializer = eventSerializer;
    }

    public TopicData getTopicData() {
        return topicData;
    }

    public void setTopicData(TopicData topicData) {
        this.topicData = topicData;
    }

    protected QueueMessage createDomainEventStreamMessage(DomainEventStreamMessage eventStream) {
        Ensure.notNull(eventStream.getAggregateRootId(), "aggregateRootId");
        Ensure.notNull(topicData, "topicData");
        EventStreamMessage eventMessage = createEventMessage(eventStream);
        String data = JsonTool.serialize(eventMessage);
        String routeKey = eventMessage.getAggregateRootId();
        QueueMessage queueMessage = new QueueMessage();
        queueMessage.setCode(QueueMessageTypeCode.DomainEventStreamMessage.getValue());
        queueMessage.setTopic(topicData.getTopic());
        queueMessage.setTags(topicData.getTags());
        queueMessage.setBody(data);
        queueMessage.setKey(eventStream.getId());
        queueMessage.setRouteKey(routeKey);
        queueMessage.setVersion(eventStream.getVersion());
        return queueMessage;
    }

    private EventStreamMessage createEventMessage(DomainEventStreamMessage eventStream) {
        EventStreamMessage message = new EventStreamMessage();
        message.setId(eventStream.getId());
        message.setCommandId(eventStream.getCommandId());
        message.setAggregateRootTypeName(eventStream.getAggregateRootTypeName());
        message.setAggregateRootId(eventStream.getAggregateRootId());
        message.setTimestamp(eventStream.getTimestamp());
        message.setVersion(eventStream.getVersion());
        message.setEvents(eventSerializer.serialize(eventStream.getEvents()));
        message.setItems(eventStream.getItems());
        return message;
    }
}
