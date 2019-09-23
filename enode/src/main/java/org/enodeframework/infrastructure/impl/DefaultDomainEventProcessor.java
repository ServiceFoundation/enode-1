package org.enodeframework.infrastructure.impl;

import org.enodeframework.eventing.DomainEventStreamMessage;
import org.enodeframework.infrastructure.ProcessingDomainEventStreamMessage;

/**
 * @author anruence@gmail.com
 */
public class DefaultDomainEventProcessor extends DefaultMessageProcessor<ProcessingDomainEventStreamMessage, DomainEventStreamMessage> {
    @Override
    public String getMessageName() {
        return "event message";
    }
}
