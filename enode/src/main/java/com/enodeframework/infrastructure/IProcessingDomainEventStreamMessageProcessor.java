package com.enodeframework.infrastructure;

public interface IProcessingDomainEventStreamMessageProcessor {
    /**
     * Process the given message.
     *
     * @param processingMessage
     */
    void process(ProcessingDomainEventStreamMessage processingMessage);

    void start();

    void stop();
}
