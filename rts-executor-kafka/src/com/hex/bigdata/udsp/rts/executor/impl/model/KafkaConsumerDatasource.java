package com.hex.bigdata.udsp.rts.executor.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by junjiem on 2017-2-20.
 */
public class KafkaConsumerDatasource extends Datasource {

    public KafkaConsumerDatasource(List<Property> propertyList) {
        super(propertyList);
    }

    public KafkaConsumerDatasource(Map<String, Property> propertyMap) {
        super(propertyMap);
    }

    // 线程数
    public int getThreadNum() {
        String value = getProperty("thread.num").getValue();
        if (StringUtils.isBlank(value))
            value = "1";
        return Integer.valueOf(value);
    }

    // zookeeper集群的IP和端口地址，多个地址用逗号分隔
    public String getZookeeperConnect() {
        return getProperty("zookeeper.connect").getValue();
    }

    // Kafka集群的IP和端口地址，多个地址用逗号分隔
    public String getMetadataBrokerList() {
        return getProperty("metadata.broker.list").getValue();
    }

    // 连接zookeeper的session超时时间
    public String getZookeeperSessionTimeoutMs() {
        return getProperty("zookeeper.session.timeout.ms").getValue();
    }

    // 客户端连接zookeeper的最大超时时间
    public String getZookeeperConnectionTimeoutMs() {
        return getProperty("zookeeper.connection.timeout.ms").getValue();
    }

    // zookeeper同步时间
    public String getZookeeperSyncTimeMs() {
        return getProperty("zookeeper.sync.time.ms").getValue();
    }

    // 消费者超时时间
    public String getConsumerTimeoutMs() {
        return getProperty("consumer.timeout.ms").getValue();
    }

    // 如果true,consumer定期地往zookeeper写入每个分区的offset
    public String getAutoCommitEnable() {
        return getProperty("auto.commit.enable").getValue();
    }

    // 消费者向zookeeper发送offset的时间
    public String getAutoCommitIntervalMs() {
        return getProperty("auto.commit.interval.ms").getValue();
    }

    // offset初始化或者达到上线时的处理方式
    public String getAutoOffsetReset() {
        return getProperty("auto.offset.reset").getValue();
    }

    // rebalance时的最大尝试次数
    public String getRebalanceMaxRetries() {
        return getProperty("rebalance.max.retries").getValue();
    }

    // 平衡补偿重试间隔时间
    public String getRebalanceBackoffMs() {
        return getProperty("rebalance.backoff.ms").getValue();
    }

    // 组ID
    public String getGroupId() {
        String value = getProperty("group.id").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("group.id不能为空");
        return value;
    }
}