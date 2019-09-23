package org.enodeframework.infrastructure.impl;

import org.enodeframework.infrastructure.IPublishableException;
import org.enodeframework.infrastructure.ProcessingPublishableExceptionMessage;

/**
 * @author anruence@gmail.com
 */
public class DefaultPublishableExceptionProcessor extends DefaultMessageProcessor<ProcessingPublishableExceptionMessage, IPublishableException> {
    @Override
    public String getMessageName() {
        return "exception message";
    }
}
