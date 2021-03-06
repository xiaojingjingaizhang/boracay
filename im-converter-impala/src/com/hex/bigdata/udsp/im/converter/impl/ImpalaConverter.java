package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.metadata.db.ClientFactory;
import com.hex.bigdata.metadata.db.model.Column;
import com.hex.bigdata.metadata.db.util.AcquireType;
import com.hex.bigdata.metadata.db.util.DBType;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.im.converter.RealtimeTargetConverter;
import com.hex.bigdata.udsp.im.converter.impl.util.ImpalaSqlUtil;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.JdbcWrapper;
import com.hex.bigdata.udsp.im.converter.model.FileFormat;
import com.hex.bigdata.udsp.im.converter.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.model.WhereProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.ImpalaConverter")
public class ImpalaConverter extends JdbcWrapper implements RealtimeTargetConverter {
    private static Logger logger = LogManager.getLogger(ImpalaConverter.class);

    @Override
    protected List<String> createSchemaSqls(String tableName, List<TableColumn> columns, String tableComment) {
        String[] sqls = {ImpalaSqlUtil.createTable(false, tableName, columns, tableComment,
                FileFormat.HIVE_FILE_FORMAT_PARQUET)};
        return Arrays.asList(sqls);
    }

    @Override
    protected String dropSchemaSql(String tableName) {
        return ImpalaSqlUtil.dropTable(true, tableName);
    }

    @Override
    protected List<String> addColumnSqls(String tableName, List<TableColumn> addColumns) {
        String[] sqls = {ImpalaSqlUtil.addColumns(tableName, addColumns)};
        return Arrays.asList(sqls);
    }

    @Override
    protected DataType getColType(String type) {
        type = type.toUpperCase();
        DataType dataType = null;
        switch (type) {
            case "VARCHAR":
                dataType = DataType.VARCHAR;
                break;
            case "STRING":
                dataType = DataType.STRING;
                break;
            case "DECIMAL":
                dataType = DataType.DECIMAL;
                break;
            case "CHAR":
                dataType = DataType.CHAR;
                break;
            case "FLOAT":
                dataType = DataType.FLOAT;
                break;
            case "DOUBLE":
                dataType = DataType.DOUBLE;
                break;
            case "TIMESTAMP":
            case "DATE":
                dataType = DataType.TIMESTAMP;
                break;
            case "INT":
                dataType = DataType.INT;
                break;
            case "BIGINT":
                dataType = DataType.BIGINT;
                break;
            case "TINYINT":
                dataType = DataType.TINYINT;
                break;
            case "SMALLINT":
                dataType = DataType.SMALLINT;
                break;
            default:
                dataType = null;
        }
        return dataType;
    }

    @Override
    protected List<Column> getColumns(Connection conn, String dbName, String tbName) throws SQLException {
        // 方式一：通过JDBCAPI方式获取字段信息
        // 通过JDBC的API接口获取，可以拿到字段名、类型、长度、注释、主键、索引、分区等信息
        return ClientFactory.createMetaClient(AcquireType.JDBCAPI, DBType.IMPALA, conn)
                .getColumns(dbName, tbName);
//        // 方式二：通过JDBCINFO方式获取字段信息
//        // 通过select * from dbName.tbName获取，只能拿到字段名、类型、长度等信息
//        return ClientFactory.createMetaClient(AcquireType.JDBCINFO, DBType.HIVE, conn)
//                .getColumns(dbName, tbName);
//        // 方式三：通过JDBCAPI方式获取字段信息
//        // 查询元数据表，可以获取最为详细的字段信息
//        return ClientFactory.createMetaClient(AcquireType.JDBCSQL, DBType.HIVE, conn)
//                .getColumns(dbName, tbName);
    }

    @Override
    protected String insertSql(String tableName, List<ValueColumn> valueColumns) {
        return ImpalaSqlUtil.insert(tableName, valueColumns);
    }

    @Override
    protected String updateSql(String tableName, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) {
        return ImpalaSqlUtil.update(tableName, valueColumns, whereProperties);
    }
}