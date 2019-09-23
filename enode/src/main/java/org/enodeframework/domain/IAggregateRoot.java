package org.enodeframework.domain;

import org.enodeframework.eventing.DomainEventStream;
import org.enodeframework.eventing.IDomainEvent;

import java.util.List;

/**
 * Represents an aggregate root.
 */
public interface IAggregateRoot {
    String uniqueId();

    int getVersion();

    List<IDomainEvent> getChanges();

    /**
     * Accept changes of the aggregate root.
     */
    void acceptChanges();

    /**
     * Replay the given event streams.
     *
     * @param eventStreams
     */
    void replayEvents(List<DomainEventStream> eventStreams);
}
