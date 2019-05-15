package com.enode.queue.command;

import com.enode.commanding.ICommand;
import com.enode.commanding.ICommandProcessor;
import com.enode.commanding.ProcessingCommand;
import com.enode.commanding.impl.CommandExecuteContext;
import com.enode.common.logging.ENodeLogger;
import com.enode.common.serializing.IJsonSerializer;
import com.enode.domain.IAggregateStorage;
import com.enode.domain.IRepository;
import com.enode.infrastructure.ITypeNameProvider;
import com.enode.queue.IMessageContext;
import com.enode.queue.IMessageHandler;
import com.enode.queue.QueueMessage;
import com.enode.queue.SendReplyService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandListener implements IMessageHandler {

    private static final Logger logger = ENodeLogger.getLog();

    @Autowired
    protected SendReplyService sendReplyService;

    @Autowired
    protected IJsonSerializer jsonSerializer;

    @Autowired
    protected ITypeNameProvider typeNameProvider;

    @Autowired
    protected ICommandProcessor processor;

    @Autowired
    protected IRepository repository;

    @Autowired
    protected IAggregateStorage aggregateRootStorage;

    @Override
    public void handle(QueueMessage queueMessage, IMessageContext context) {
        Map<String, String> commandItems = new HashMap<>();
        CommandMessage commandMessage = jsonSerializer.deserialize(queueMessage.getBody(), CommandMessage.class);
        Class commandType = typeNameProvider.getType(commandMessage.getCommandType());
        ICommand command = (ICommand) jsonSerializer.deserialize(commandMessage.getCommandData(), commandType);
        CommandExecuteContext commandExecuteContext = new CommandExecuteContext(repository, aggregateRootStorage, queueMessage, context, commandMessage, sendReplyService);
        commandItems.put("CommandReplyAddress", commandMessage.getReplyAddress());
        logger.info("ENode command message received, messageId: {}, aggregateRootId: {}", command.id(), command.getAggregateRootId());
        processor.process(new ProcessingCommand(command, commandExecuteContext, commandItems));
    }
}