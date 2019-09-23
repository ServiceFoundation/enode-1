package org.enodeframework.tests;

import org.enodeframework.ENodeBootstrap;
import org.enodeframework.commanding.impl.DefaultCommandProcessor;
import org.enodeframework.commanding.impl.DefaultProcessingCommandHandler;
import org.enodeframework.eventing.impl.DefaultEventService;
import org.enodeframework.mysql.MysqlEventStore;
import org.enodeframework.mysql.MysqlPublishedVersionStore;
import org.enodeframework.queue.command.CommandResultProcessor;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(value = "org.enodeframework")
public class EnodeExtensionConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CommandResultProcessor commandResultProcessor() {
        CommandResultProcessor processor = new CommandResultProcessor();
        return processor;
    }

    @Bean(initMethod = "init")
    public ENodeBootstrap eNodeBootstrap() {
        ENodeBootstrap bootstrap = new ENodeBootstrap();
        bootstrap.setPackages(Lists.newArrayList("org.enodeframework.tests"));
        return bootstrap;
    }

    @Bean
    public DefaultProcessingCommandHandler defaultProcessingCommandHandler() {
        return new DefaultProcessingCommandHandler();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultEventService defaultEventService() {
        return new DefaultEventService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultCommandProcessor defaultCommandProcessor() {
        return new DefaultCommandProcessor();
    }

    @Bean
    public MysqlEventStore mysqlEventStore(HikariDataSource dataSource) {
        MysqlEventStore mysqlEventStore = new MysqlEventStore(dataSource, null);
        return mysqlEventStore;
    }

    @Bean
    public MysqlPublishedVersionStore mysqlPublishedVersionStore(HikariDataSource dataSource) {
        return new MysqlPublishedVersionStore(dataSource, null);
    }

//    @Bean
//    public MysqlEventStore mysqlEventStore(HikariDataSource dataSource) {
//        MysqlEventStore mysqlEventStore = new MysqlEventStore(dataSource, null);
//        return mysqlEventStore;
//    }
//
//    @Bean
//    public MysqlPublishedVersionStore mysqlPublishedVersionStore(HikariDataSource dataSource) {
//        return new MysqlPublishedVersionStore(dataSource, null);
//    }


    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(Constants.JDBC_URL);
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName(com.mysql.cj.jdbc.Driver.class.getName());
        return dataSource;
    }
}
