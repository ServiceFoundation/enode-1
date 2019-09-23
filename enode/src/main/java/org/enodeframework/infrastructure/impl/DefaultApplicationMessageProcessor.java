package org.enodeframework.infrastructure.impl;

import org.enodeframework.infrastructure.IApplicationMessage;
import org.enodeframework.infrastructure.ProcessingApplicationMessage;

/**
 * @author anruence@gmail.com
 */
public class DefaultApplicationMessageProcessor extends DefaultMessageProcessor<ProcessingApplicationMessage, IApplicationMessage> {
    @Override
    public String getMessageName() {
        return "application message";
    }
}
