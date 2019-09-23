package org.enodeframework.commanding.impl;

import org.enodeframework.commanding.ICommand;
import org.enodeframework.commanding.ICommandRoutingKeyProvider;

/**
 * @author anruence@gmail.com
 */
public class DefaultCommandRoutingKeyProvider implements ICommandRoutingKeyProvider {
    @Override
    public String getRoutingKey(ICommand command) {
        if (!(command.getAggregateRootId() == null || "".equals(command.getAggregateRootId().trim()))) {
            return command.getAggregateRootId();
        }
        return command.getId();
    }
}
