package org.enodeframework.tests.EventHandlers;

import org.enodeframework.annotation.Event;
import org.enodeframework.annotation.Subscribe;
import org.enodeframework.common.io.AsyncTaskResult;
import org.enodeframework.tests.Domain.TestAggregateCreated;

@Event
public class TestAggregateEventHandler {
    @Subscribe
    public AsyncTaskResult HandleAsync(TestAggregateCreated evnt) {
        System.out.println("this was executed" + evnt.Title);
        //DO NOTHING
        return AsyncTaskResult.Success;
    }
}
