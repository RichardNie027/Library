package com.nec.lib.android.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class MapUtil {

    public static <K, V> Entry<K, V> getByIndex(LinkedHashMap<K, V> map, int index) {
        if(index > map.size() || index < 0)
            return null;
        Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
        Entry<K, V> entry = null;
        int idx = 0;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if(idx++ == index)
                return entry;
        }
        return null;
    }
}
