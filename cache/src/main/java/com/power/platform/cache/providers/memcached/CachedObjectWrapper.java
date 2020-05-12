package com.power.platform.cache.providers.memcached;

import net.rubyeye.xmemcached.transcoders.CachedData;

import com.power.platform.cache.providers.CachedObject;

class CachedObjectWrapper implements CachedObject {

    private final CachedData cachedData;

    CachedObjectWrapper(final CachedData cachedData) {
        this.cachedData = cachedData;
    }

     
    public byte[] getData() {
        return cachedData.getData();
    }

     
    public int getFlags() {
        return cachedData.getFlag();
    }

     
    public String toString() {
        return "CachedObjectWrapper [cachedData=" + cachedData + "]";
    }
}
