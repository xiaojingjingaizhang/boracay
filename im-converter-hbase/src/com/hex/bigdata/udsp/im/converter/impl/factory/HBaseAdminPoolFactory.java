package com.hex.bigdata.udsp.im.converter.impl.factory;

import com.hex.bigdata.udsp.im.converter.impl.model.HBaseDatasource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.File;
import java.io.IOException;

/**
 * Created by JunjieM on 2017-9-11.
 */
public class HBaseAdminPoolFactory {
    static {
        // 解决winutils.exe不存在的问题
        try {
            File workaround = new File(".");
            System.getProperties().put("hadoop.home.dir",
                    workaround.getAbsolutePath());
            new File("./bin").mkdirs();
            new File("./bin/winutils.exe").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private GenericObjectPool pool;

    public HBaseAdminPoolFactory(GenericObjectPool.Config config, HBaseDatasource datasource) {
        HBaseAdminFactory factory = new HBaseAdminFactory(datasource);
        pool = new GenericObjectPool(factory, config);
    }

    public HBaseAdmin getHBaseAdmin() throws Exception {
        return (HBaseAdmin) pool.borrowObject();
    }

    public void releaseHBaseAdmin(HBaseAdmin admin) {
        try {
            pool.returnObject(admin);
        } catch (Exception e) {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e1) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closePool() {
        if (pool != null) {
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class HBaseAdminFactory extends BasePoolableObjectFactory {
    private Configuration conf;

    public HBaseAdminFactory(HBaseDatasource datasource) {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", datasource.getZkQuorum());
        conf.set("hbase.zookeeper.property.clientPort", datasource.getZkPort());
        if (StringUtils.isNotBlank(datasource.getRpcTimeout()))
            conf.set("hbase.rpc.timeout", datasource.getRpcTimeout());
        if (StringUtils.isNotBlank(datasource.getClientRetriesNumber()))
            conf.set("hbase.client.retries.number", datasource.getClientRetriesNumber());
        if (StringUtils.isNotBlank(datasource.getClientPause()))
            conf.set("hbase.client.pause", datasource.getClientPause());
        if (StringUtils.isNotBlank(datasource.getZkRecoveryRetry()))
            conf.set("zookeeper.recovery.retry", datasource.getZkRecoveryRetry());
        if (StringUtils.isNotBlank(datasource.getZkRecoveryRetryIntervalmill()))
            conf.set("zookeeper.recovery.retry.intervalmill", datasource.getZkRecoveryRetryIntervalmill());
        if (StringUtils.isNotBlank(datasource.getClientOperationTimeout()))
            conf.set("hbase.client.operation.timeout", datasource.getClientOperationTimeout());
//        if (StringUtils.isNotBlank(datasource.getRegionserverLeasePeriod()))
//            conf.set("hbase.regionserver.lease.period", datasource.getRegionserverLeasePeriod()); // 已被弃用
        if (StringUtils.isNotBlank(datasource.getClientScannerTimeoutPeriod()))
            conf.set("hbase.client.scanner.timeout.period", datasource.getClientScannerTimeoutPeriod());
    }

    @Override
    public Object makeObject() throws Exception {
        HBaseAdmin admin = new HBaseAdmin(conf);
        return admin;
    }

    public void destroyObject(Object obj) throws Exception {
        if (obj instanceof HBaseAdmin) {
            ((HBaseAdmin) obj).close();
        }
    }

    public boolean validateObject(Object obj) {
        if (obj instanceof HBaseAdmin) {
            HBaseAdmin admin = ((HBaseAdmin) obj);
            if (admin.isAborted()) {
                return false;
            }
            return true;
        }
        return false;
    }
}
