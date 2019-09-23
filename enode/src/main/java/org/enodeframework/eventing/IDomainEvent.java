package org.enodeframework.eventing;

import org.enodeframework.infrastructure.ISequenceMessage;

public interface IDomainEvent<TAggregateRootId> extends ISequenceMessage {
    TAggregateRootId getAggregateRootId();

    void setAggregateRootId(TAggregateRootId aggregateRootId);
}
