package com.power.platform.cache.providers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public interface CacheClientFactory {
	/**
     * 创建缓存客户端.
     * 
     * @param addrs
     *            缓存服务器的地址
     * @param configuration
     *            设置创建的缓存客户端
     * @return 缓存客户端
     * @throws IOException
     */
    CacheClient create(List<InetSocketAddress> addrs, CacheConfiguration configuration) throws IOException;
}
