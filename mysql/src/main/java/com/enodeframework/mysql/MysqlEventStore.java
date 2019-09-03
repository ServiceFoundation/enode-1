package com.enodeframework.mysql;

import com.enodeframework.ObjectContainer;
import com.enodeframework.common.io.AsyncTaskResult;
import com.enodeframework.common.io.AsyncTaskStatus;
import com.enodeframework.common.io.IOHelper;
import com.enodeframework.common.serializing.JsonTool;
import com.enodeframework.common.utilities.Ensure;
import com.enodeframework.configurations.DefaultDBConfigurationSetting;
import com.enodeframework.configurations.OptionSetting;
import com.enodeframework.eventing.DomainEventStream;
import com.enodeframework.eventing.EventAppendResult;
import com.enodeframework.eventing.IDomainEvent;
import com.enodeframework.eventing.IEventSerializer;
import com.enodeframework.eventing.IEventStore;
import com.enodeframework.eventing.impl.StreamRecord;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author anruence@gmail.com
 */
public class MysqlEventStore implements IEventStore {
    private static final Logger logger = LoggerFactory.getLogger(MysqlEventStore.class);
    private static final String EVENT_TABLE_NAME_FORMAT = "%s_%s";
    private final String tableName;
    private final int tableCount;
    private final String versionIndexName;
    private final String commandIndexName;
    private final int bulkCopyBatchSize;
    private final int bulkCopyTimeout;
    private final SQLClient sqlClient;

    @Autowired
    private IEventSerializer eventSerializer;

    public MysqlEventStore(DataSource ds, OptionSetting optionSetting) {
        Ensure.notNull(ds, "ds");
        if (optionSetting != null) {
            tableName = optionSetting.getOptionValue("TableName");
            tableCount = optionSetting.getOptionValue("TableCount") == null ? 1 : Integer.valueOf(optionSetting.getOptionValue("TableCount"));
            versionIndexName = optionSetting.getOptionValue("VersionIndexName");
            commandIndexName = optionSetting.getOptionValue("CommandIndexName");
            bulkCopyBatchSize = optionSetting.getOptionValue("BulkCopyBatchSize") == null ? 0 : Integer.valueOf(optionSetting.getOptionValue("BulkCopyBatchSize"));
            bulkCopyTimeout = optionSetting.getOptionValue("BulkCopyTimeout") == null ? 0 : Integer.valueOf(optionSetting.getOptionValue("BulkCopyTimeout"));
        } else {
            DefaultDBConfigurationSetting setting = new DefaultDBConfigurationSetting();
            tableName = setting.getEventTableName();
            tableCount = setting.getEventTableCount();
            versionIndexName = setting.getEventTableVersionUniqueIndexName();
            commandIndexName = setting.getEventTableCommandIdUniqueIndexName();
            bulkCopyBatchSize = setting.getEventTableBulkCopyBatchSize();
            bulkCopyTimeout = setting.getEventTableBulkCopyTimeout();
        }
        Ensure.notNull(tableName, "tableName");
        Ensure.notNull(versionIndexName, "eventIndexName");
        Ensure.notNull(commandIndexName, "commandIndexName");
        Ensure.positive(bulkCopyBatchSize, "bulkCopyBatchSize");
        Ensure.positive(bulkCopyTimeout, "bulkCopyTimeout");
        sqlClient = JDBCClient.create(ObjectContainer.vertx, ds);
    }

    @Override
    public CompletableFuture<AsyncTaskResult<EventAppendResult>> batchAppendAsync(List<DomainEventStream> eventStreams) {
        CompletableFuture<AsyncTaskResult<EventAppendResult>> future = new CompletableFuture<>();
        if (eventStreams.size() == 0) {
            throw new IllegalArgumentException("Event streams cannot be empty.");
        }
        Map<String, List<DomainEventStream>> eventStreamMap = eventStreams.stream().collect(Collectors.groupingBy(DomainEventStream::getAggregateRootId));
        for (List<DomainEventStream> domainEventStreams : eventStreamMap.values()) {
            String aggregateRootId = domainEventStreams.get(0).getAggregateRootId();
            String sql = String.format("INSERT INTO %s(AggregateRootId,AggregateRootTypeName,CommandId,Version,CreatedOn,Events) VALUES(?,?,?,?,?,?)", getTableName(aggregateRootId));
            int size = domainEventStreams.size();
            JsonArray array = new JsonArray();
            for (int i = 0; i < size; i++) {
                DomainEventStream domainEventStream = domainEventStreams.get(i);
                array.add(domainEventStream.getAggregateRootId());
                array.add(domainEventStream.getAggregateRootTypeName());
                array.add(domainEventStream.getCommandId());
                array.add(domainEventStream.getVersion());
                array.add(domainEventStream.getTimestamp().toInstant());
                array.add(JsonTool.serialize(eventSerializer.serialize(domainEventStream.events())));
                if (i == 0) {
                    continue;
                }
                sql = sql.concat(",(?,?,?,?,?,?)");
            }
            sqlClient.updateWithParams(sql, array, x -> {
                if (x.failed()) {
                    future.completeExceptionally(x.cause());
                    return;
                }
                future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.Success));
            });

        }
        return future.exceptionally(throwable -> {
            if (throwable instanceof SQLException) {
                SQLException ex = (SQLException) throwable;
                if (ex.getErrorCode() == 1062 && ex.getMessage().contains(versionIndexName)) {
                    return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateEvent);
                } else if (ex.getErrorCode() == 1062 && ex.getMessage().contains(commandIndexName)) {
                    return new AsyncTaskResult<>(AsyncTaskStatus.Success, EventAppendResult.DuplicateCommand);
                }
                logger.error("Batch append event has sql exception.", ex);
                return new AsyncTaskResult(AsyncTaskStatus.IOException, ex.getMessage(), EventAppendResult.Failed);
            }
            logger.error("Batch append event has unknown exception.", throwable);
            return new AsyncTaskResult(AsyncTaskStatus.Failed, throwable.getMessage(), EventAppendResult.Failed);
        });
    }

    @Override
    public CompletableFuture<AsyncTaskResult<List<DomainEventStream>>> queryAggregateEventsAsync(String aggregateRootId, String aggregateRootTypeName, int minVersion, int maxVersion) {
        return IOHelper.tryIOFuncAsync(() -> {
            CompletableFuture<AsyncTaskResult<List<DomainEventStream>>> future = new CompletableFuture<>();
            String sql = String.format("SELECT * FROM `%s` WHERE AggregateRootId = ? AND Version >= ? AND Version <= ? ORDER BY Version", getTableName(aggregateRootId));
            JsonArray array = new JsonArray();
            array.add(aggregateRootId);
            array.add(minVersion);
            array.add(maxVersion);
            sqlClient.queryWithParams(sql, array, x -> {
                if (x.succeeded()) {
                    List<StreamRecord> results = Lists.newArrayList();
                    x.result().getRows().forEach(row -> results.add(row.mapTo(StreamRecord.class)));
                    List<DomainEventStream> streams = results.stream().map(this::convertFrom).collect(Collectors.toList());
                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Success, streams));
                    return;
                }
                if (x.cause() instanceof SQLException) {
                    SQLException ex = (SQLException) x.cause();
                    String errorMessage = String.format("Failed to query aggregate events async, aggregateRootId: %s, aggregateRootType: %s", aggregateRootId, aggregateRootTypeName);
                    logger.error(errorMessage, ex);
                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage()));
                    return;
                }
                logger.error("Failed to query aggregate events async, aggregateRootId: {}, aggregateRootType: {}", aggregateRootId, aggregateRootTypeName, x.cause());
                future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, x.cause().getMessage()));
            });
            return future;
        }, "QueryAggregateEventsAsync");
    }

    @Override
    public CompletableFuture<AsyncTaskResult<DomainEventStream>> findAsync(String aggregateRootId, int version) {
        return IOHelper.tryIOFuncAsync(() -> {
            CompletableFuture<AsyncTaskResult<DomainEventStream>> future = new CompletableFuture<>();
            String sql = String.format("select * from `%s` where AggregateRootId=? and Version=?", getTableName(aggregateRootId));
            JsonArray array = new JsonArray();
            array.add(aggregateRootId);
            array.add(version);
            sqlClient.queryWithParams(sql, array, x -> {
                if (x.succeeded()) {
                    StreamRecord record = null;
                    if (x.result().getRows().size() >= 1) {
                        record = x.result().getRows().get(0).mapTo(StreamRecord.class);
                    }
                    DomainEventStream stream = record != null ? convertFrom(record) : null;

                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Success, stream));
                    return;
                }
                if (x.cause() instanceof SQLException) {
                    SQLException ex = (SQLException) x.cause();
                    logger.error("Find event by version has sql exception, aggregateRootId: {}, version: {}", aggregateRootId, version, ex);
                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage()));
                    return;
                }
                Throwable ex = x.cause();
                logger.error("Find event by version has unknown exception, aggregateRootId: {}, version: {}", aggregateRootId, version, ex);
                future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
            });
            return future;
        }, "FindEventByVersionAsync");

    }


    @Override
    public CompletableFuture<AsyncTaskResult<DomainEventStream>> findAsync(String aggregateRootId, String commandId) {
        return IOHelper.tryIOFuncAsync(() -> {
            CompletableFuture<AsyncTaskResult<DomainEventStream>> future = new CompletableFuture<>();
            String sql = String.format("select * from `%s` where AggregateRootId=? and CommandId=?", getTableName(aggregateRootId));
            JsonArray array = new JsonArray();
            array.add(aggregateRootId);
            array.add(commandId);
            sqlClient.queryWithParams(sql, array, x -> {
                if (x.succeeded()) {
                    StreamRecord record = null;
                    if (x.result().getRows().size() >= 1) {
                        record = x.result().getRows().get(0).mapTo(StreamRecord.class);
                    }
                    DomainEventStream stream = record != null ? convertFrom(record) : null;

                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Success, stream));
                    return;
                }
                if (x.cause() instanceof SQLException) {
                    SQLException ex = (SQLException) x.cause();
                    logger.error("Find event by commandId has sql exception, aggregateRootId: {}, commandId: {}", aggregateRootId, commandId, ex);
                    future.complete(new AsyncTaskResult<>(AsyncTaskStatus.IOException, ex.getMessage()));
                    return;
                }
                Throwable ex = x.cause();
                logger.error("Find event by commandId has unknown exception, aggregateRootId: {}, commandId: {}", aggregateRootId, commandId, ex);
                future.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
            });
            return future;
        }, "FindEventByCommandIdAsync");

    }

    private int getTableIndex(String aggregateRootId) {
        int hash = aggregateRootId.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash % tableCount;
    }

    private String getTableName(String aggregateRootId) {
        if (tableCount <= 1) {
            return tableName;
        }
        int tableIndex = getTableIndex(aggregateRootId);
        return String.format(EVENT_TABLE_NAME_FORMAT, tableName, tableIndex);
    }

    private DomainEventStream convertFrom(StreamRecord record) {
        return new DomainEventStream(
                record.CommandId,
                record.AggregateRootId,
                record.AggregateRootTypeName,
                record.Version,
                record.CreatedOn,
                eventSerializer.deserialize(JsonTool.deserialize(record.Events, Map.class), IDomainEvent.class),
                Maps.newHashMap());
    }

    private StreamRecord convertTo(DomainEventStream eventStream) {
        return new StreamRecord(eventStream.getCommandId(), eventStream.getAggregateRootId(), eventStream.getAggregateRootTypeName(),
                eventStream.getVersion(), eventStream.getTimestamp(),
                JsonTool.serialize(eventSerializer.serialize(eventStream.events())));
    }
}
