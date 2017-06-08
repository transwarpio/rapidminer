package com.rapidminer.extension.jdbc.tools.jdbc.db;

import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liusheng on 5/12/16.
 */
public class AttributeStoreFactory {
    static private Map<String, AttributeStore> storeMap = new HashMap<>();
    static {
        storeMap.put("jdbc:hive2:", new InceptorAttributeStore());
    }
    static public boolean useInceptorStore(ConnectionEntry connectionEntry) {
        String url = connectionEntry.getURL();
        AttributeStore store =  storeMap.get(url.substring(0, getIndex(url)));
        return store != null && store instanceof InceptorAttributeStore;
    }

    static public AttributeStore getAttributeStore(ConnectionEntry connectionEntry){
        String url = connectionEntry.getURL();
        AttributeStore store =  storeMap.get(url.substring(0, getIndex(url)));
        if (store == null) {
            return new SimpleAttbuteStore();
        } else {
            return store;
        }
    }

    static private int getIndex(String url) {
        int index = url.indexOf("//");
        if (index == -1) {
            return url.indexOf("@");
        } else {
            return index;
        }
    }
}
