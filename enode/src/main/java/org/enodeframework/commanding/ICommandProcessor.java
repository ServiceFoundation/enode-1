package org.enodeframework.commanding;

public interface ICommandProcessor {
    void process(ProcessingCommand processingCommand);

    void start();

    void stop();
}
