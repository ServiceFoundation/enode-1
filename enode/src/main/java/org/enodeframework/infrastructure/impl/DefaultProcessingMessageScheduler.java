package org.enodeframework.infrastructure.impl;

import org.enodeframework.infrastructure.IMessage;
import org.enodeframework.infrastructure.IProcessingMessage;
import org.enodeframework.infrastructure.IProcessingMessageHandler;
import org.enodeframework.infrastructure.IProcessingMessageScheduler;
import org.enodeframework.infrastructure.ProcessingMessageMailbox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author anruence@gmail.com
 */
public class DefaultProcessingMessageScheduler<X extends IProcessingMessage<X, Y>, Y extends IMessage> implements IProcessingMessageScheduler<X, Y> {
    @Autowired
    private IProcessingMessageHandler<X, Y> messageHandler;

    public DefaultProcessingMessageScheduler<X, Y> setMessageHandler(IProcessingMessageHandler<X, Y> messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    @Override
    public void scheduleMessage(X processingMessage) {
        CompletableFuture.runAsync(() -> messageHandler.handleAsync(processingMessage));
    }

    @Override
    public void scheduleMailbox(ProcessingMessageMailbox<X, Y> mailbox) {
        CompletableFuture.runAsync(mailbox::run);
    }
}
