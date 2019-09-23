package org.enodeframework.commanding;

import org.enodeframework.domain.IAggregateRoot;

import java.util.List;

public interface ITrackingContext {
    List<IAggregateRoot> getTrackedAggregateRoots();

    void clear();
}
