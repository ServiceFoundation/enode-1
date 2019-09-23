package org.enodeframework.eventing;

import org.enodeframework.infrastructure.SequenceMessage;

/**
 * Represents an abstract generic domain event.
 */
public abstract class DomainEvent<TAggregateRootId> extends SequenceMessage<TAggregateRootId> implements IDomainEvent<TAggregateRootId> {
}
