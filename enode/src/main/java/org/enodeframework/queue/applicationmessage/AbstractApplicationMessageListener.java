package org.enodeframework.queue.applicationmessage;

import org.enodeframework.common.serializing.JsonTool;
import org.enodeframework.infrastructure.IApplicationMessage;
import org.enodeframework.infrastructure.IMessageProcessor;
import org.enodeframework.infrastructure.ITypeNameProvider;
import org.enodeframework.infrastructure.ProcessingApplicationMessage;
import org.enodeframework.infrastructure.impl.DefaultMessageProcessContext;
import org.enodeframework.queue.IMessageContext;
import org.enodeframework.queue.IMessageHandler;
import org.enodeframework.queue.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractApplicationMessageListener implements IMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationMessageListener.class);
    @Autowired
    protected ITypeNameProvider typeNameProvider;
    @Autowired
    protected IMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> applicationMessageProcessor;

    public AbstractApplicationMessageListener setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
        return this;
    }

    public AbstractApplicationMessageListener setApplicationMessageProcessor(IMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> applicationMessageProcessor) {
        this.applicationMessageProcessor = applicationMessageProcessor;
        return this;
    }

    @Override
    public void handle(QueueMessage queueMessage, IMessageContext context) {
        String msg = queueMessage.getBody();
        ApplicationDataMessage appDataMessage = JsonTool.deserialize(msg, ApplicationDataMessage.class);
        Class applicationMessageType = typeNameProvider.getType(appDataMessage.getApplicationMessageType());
        IApplicationMessage message = (IApplicationMessage) JsonTool.deserialize(appDataMessage.getApplicationMessageData(), applicationMessageType);
        DefaultMessageProcessContext processContext = new DefaultMessageProcessContext(queueMessage, context);
        ProcessingApplicationMessage processingMessage = new ProcessingApplicationMessage(message, processContext);
        if (logger.isDebugEnabled()) {
            logger.debug("ENode application message received, messageId: {}, routingKey: {}", message.getId(), message.getRoutingKey());
        }
        applicationMessageProcessor.process(processingMessage);
    }
}
