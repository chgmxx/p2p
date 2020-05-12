package com.power.platform.cache.providers;

public interface CachedObject {
	/**
     * 存储在一个服务器上的最大大小.
     */
    int MAX_SIZE = 20 * 1024 * 1024;

    byte[] getData();

    int getFlags();
}
