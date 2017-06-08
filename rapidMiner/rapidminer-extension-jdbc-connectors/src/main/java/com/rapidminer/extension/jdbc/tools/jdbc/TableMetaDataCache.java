package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.tools.ProgressListener;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableMetaDataCache {
    private Map<String, Map<TableName, List<ColumnIdentifier>>> tableMap = new ConcurrentHashMap();
    private Map<String, Long> lastQueryTimeMap = new ConcurrentHashMap();
    private Map<String, Object> lockMap = new ConcurrentHashMap();
    private boolean refreshCacheAfterInterval;
    private static final int CACHE_REFRESH_INTERVAL = 60000;
    private static TableMetaDataCache instance;

    private TableMetaDataCache(boolean refreshCacheAfterInterval) {
        this.refreshCacheAfterInterval = refreshCacheAfterInterval;
    }

    public static synchronized TableMetaDataCache getInstance() {
        if(instance == null) {
            instance = new TableMetaDataCache(false);
        }

        return instance;
    }

    public Map<TableName, List<ColumnIdentifier>> getAllTableMetaData(String connectionName, DatabaseHandler handler, ProgressListener progressListener, int minProgress, int maxProgress) throws SQLException {
        if(!this.lockMap.containsKey(connectionName)) {
            this.lockMap.put(connectionName, new Object());
        }

        synchronized(this.lockMap.get(connectionName)) {
            Map map = (Map)this.tableMap.get(connectionName);
            if(map == null || this.refreshCacheAfterInterval && System.currentTimeMillis() - ((Long)this.lastQueryTimeMap.get(connectionName)).longValue() > 60000L) {
                this.updateCache(connectionName, handler, progressListener, minProgress, maxProgress, true);
                map = (Map)this.tableMap.get(connectionName);
            }

            progressListener.setCompleted(maxProgress);
            return map;
        }
    }

    public Map<TableName, List<ColumnIdentifier>> getAllTableMetaData(String connectionName, DatabaseHandler handler) throws SQLException {
        return this.getAllTableMetaData(connectionName, handler, (ProgressListener)null, 0, 0);
    }

    public List<ColumnIdentifier> getAllColumnNames(String connectionName, DatabaseHandler handler, TableName tableName) throws SQLException {
        if(!this.lockMap.containsKey(connectionName)) {
            this.lockMap.put(connectionName, new Object());
        }

        synchronized(this.lockMap.get(connectionName)) {
            Map map = (Map)this.tableMap.get(connectionName);
            if(map == null || this.refreshCacheAfterInterval && System.currentTimeMillis() - ((Long)this.lastQueryTimeMap.get(connectionName)).longValue() > 60000L) {
                this.updateCache(connectionName, handler, (ProgressListener)null, 0, 0, true);
                map = (Map)this.tableMap.get(connectionName);
            }

            return (List)map.get(tableName);
        }
    }

    public void clearCache() {
        this.tableMap.clear();
    }

    private void updateCache(String connectionName, DatabaseHandler handler, ProgressListener progressListener, int minProgress, int maxProgress, boolean fetchColumns) throws SQLException {
        Map tableMetaMap = handler.getAllTableMetaData(progressListener, minProgress, maxProgress, fetchColumns);
        this.tableMap.put(connectionName, tableMetaMap);
        this.lastQueryTimeMap.put(connectionName, new Long(System.currentTimeMillis()));
    }
}
