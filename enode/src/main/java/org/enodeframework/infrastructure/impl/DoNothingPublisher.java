package org.enodeframework.infrastructure.impl;

import org.enodeframework.common.io.AsyncTaskResult;
import org.enodeframework.infrastructure.IMessage;
import org.enodeframework.infrastructure.IMessagePublisher;

import java.util.concurrent.CompletableFuture;

/**
 * @author anruence@gmail.com
 */
public class DoNothingPublisher<TMessage extends IMessage> implements IMessagePublisher<TMessage> {
    private static final CompletableFuture<AsyncTaskResult> SUCCESSRESULTTASK = CompletableFuture.completedFuture(AsyncTaskResult.Success);

    @Override
    public CompletableFuture<AsyncTaskResult> publishAsync(TMessage message) {
        return SUCCESSRESULTTASK;
    }
}
