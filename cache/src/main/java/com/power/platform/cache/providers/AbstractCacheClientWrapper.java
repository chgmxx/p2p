package com.power.platform.cache.providers;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import com.power.platform.cache.CacheException;

public abstract class AbstractCacheClientWrapper implements CacheClient {

     
    public void delete(final Collection<String> keys) throws TimeoutException, CacheException {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (final String key : keys) {
            if (key != null) {
                delete(key);
            }
        }

    }
}
