package org.enodeframework;

import org.enodeframework.commanding.impl.CommandAsyncHandlerProxy;
import org.enodeframework.commanding.impl.CommandHandlerProxy;
import org.enodeframework.commanding.impl.DefaultCommandAsyncHandlerProvider;
import org.enodeframework.commanding.impl.DefaultCommandHandlerProvider;
import org.enodeframework.commanding.impl.DefaultCommandRoutingKeyProvider;
import org.enodeframework.common.container.SpringObjectContainer;
import org.enodeframework.common.scheduling.ScheduleService;
import org.enodeframework.domain.impl.DefaultAggregateRepositoryProvider;
import org.enodeframework.domain.impl.DefaultAggregateRootFactory;
import org.enodeframework.domain.impl.DefaultAggregateRootInternalHandlerProvider;
import org.enodeframework.domain.impl.DefaultAggregateSnapshotter;
import org.enodeframework.domain.impl.DefaultMemoryCache;
import org.enodeframework.domain.impl.DefaultRepository;
import org.enodeframework.domain.impl.EventSourcingAggregateStorage;
import org.enodeframework.eventing.impl.DefaultEventSerializer;
import org.enodeframework.infrastructure.impl.DefaultApplicationMessageProcessor;
import org.enodeframework.infrastructure.impl.DefaultDomainEventProcessor;
import org.enodeframework.infrastructure.impl.DefaultMessageDispatcher;
import org.enodeframework.infrastructure.impl.DefaultMessageHandlerProvider;
import org.enodeframework.infrastructure.impl.DefaultProcessingMessageHandler;
import org.enodeframework.infrastructure.impl.DefaultProcessingMessageScheduler;
import org.enodeframework.infrastructure.impl.DefaultPublishableExceptionProcessor;
import org.enodeframework.infrastructure.impl.DefaultThreeMessageHandlerProvider;
import org.enodeframework.infrastructure.impl.DefaultTwoMessageHandlerProvider;
import org.enodeframework.infrastructure.impl.DefaultTypeNameProvider;
import org.enodeframework.infrastructure.impl.MessageHandlerProxy1;
import org.enodeframework.infrastructure.impl.MessageHandlerProxy2;
import org.enodeframework.infrastructure.impl.MessageHandlerProxy3;
import org.enodeframework.queue.SendReplyService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author anruence@gmail.com
 */
public class ENodeAutoConfiguration {
    @Bean
    public ScheduleService scheduleService() {
        return new ScheduleService();
    }

    @Bean
    public DefaultProcessingMessageScheduler defaultProcessingMessageScheduler() {
        return new DefaultProcessingMessageScheduler();
    }

    @Bean
    public DefaultTypeNameProvider defaultTypeNameProvider() {
        return new DefaultTypeNameProvider();
    }

    @Bean
    public DefaultProcessingMessageHandler defaultProcessingMessageHandler() {
        return new DefaultProcessingMessageHandler();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultPublishableExceptionProcessor defaultPublishableExceptionProcessor() {
        return new DefaultPublishableExceptionProcessor();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultApplicationMessageProcessor defaultApplicationMessageProcessor() {
        return new DefaultApplicationMessageProcessor();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultDomainEventProcessor defaultDomainEventProcessor() {
        return new DefaultDomainEventProcessor();
    }

    @Bean
    public SpringObjectContainer springObjectContainer() {
        SpringObjectContainer objectContainer = new SpringObjectContainer();
        ObjectContainer.container = objectContainer;
        return objectContainer;
    }

    /**
     * 原型模式获取bean，每次新建代理执行
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CommandHandlerProxy commandHandlerProxy() {
        return new CommandHandlerProxy();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CommandAsyncHandlerProxy commandAsyncHandlerProxy() {
        return new CommandAsyncHandlerProxy();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy1 messageHandlerProxy1() {
        return new MessageHandlerProxy1();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy2 messageHandlerProxy2() {
        return new MessageHandlerProxy2();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageHandlerProxy3 messageHandlerProxy3() {
        return new MessageHandlerProxy3();
    }

    @Bean
    public DefaultEventSerializer defaultEventSerializer() {
        return new DefaultEventSerializer();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SendReplyService sendReplyService() {
        return new SendReplyService();
    }

    @Bean
    public DefaultAggregateRootInternalHandlerProvider aggregateRootInternalHandlerProvider() {
        return new DefaultAggregateRootInternalHandlerProvider();
    }

    @Bean
    public DefaultMessageDispatcher defaultMessageDispatcher() {
        return new DefaultMessageDispatcher();
    }

    @Bean
    public DefaultRepository defaultRepository() {
        return new DefaultRepository();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultMemoryCache defaultMemoryCache() {
        return new DefaultMemoryCache();
    }

    @Bean
    public DefaultCommandRoutingKeyProvider commandRoutingKeyProvider() {
        return new DefaultCommandRoutingKeyProvider();
    }

    @Bean
    public DefaultAggregateRepositoryProvider aggregateRepositoryProvider() {
        return new DefaultAggregateRepositoryProvider();
    }

    @Bean
    public DefaultThreeMessageHandlerProvider threeMessageHandlerProvider() {
        return new DefaultThreeMessageHandlerProvider();
    }

    @Bean
    public DefaultTwoMessageHandlerProvider twoMessageHandlerProvider() {
        return new DefaultTwoMessageHandlerProvider();
    }

    @Bean
    public DefaultMessageHandlerProvider messageHandlerProvider() {
        return new DefaultMessageHandlerProvider();
    }

    @Bean
    public DefaultCommandAsyncHandlerProvider commandAsyncHandlerProvider() {
        return new DefaultCommandAsyncHandlerProvider();
    }

    @Bean
    public DefaultCommandHandlerProvider commandHandlerProvider() {
        return new DefaultCommandHandlerProvider();
    }

    @Bean
    public DefaultAggregateRootFactory aggregateRootFactory() {
        return new DefaultAggregateRootFactory();
    }

    @Bean
    public DefaultAggregateSnapshotter aggregateSnapshotter() {
        return new DefaultAggregateSnapshotter();
    }

    @Bean
    public EventSourcingAggregateStorage eventSourcingAggregateStorage() {
        return new EventSourcingAggregateStorage();
    }
}
