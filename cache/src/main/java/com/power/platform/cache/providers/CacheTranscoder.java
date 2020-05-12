package com.power.platform.cache.providers;


public interface CacheTranscoder {

    /**
     * 解码缓存对象.
     * 
     * @param data
     *            the cached object to decode
     * @return decoded object
     */
    Object decode(final CachedObject data);

    /**
     * Encodes object.
     * 
     * @param o
     *            the object to encode
     * @return encoded object
     */
    CachedObject encode(final Object o);
}
