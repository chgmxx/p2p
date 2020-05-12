package com.power.platform.cache.providers.memcached;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientStateListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectListener implements MemcachedClientStateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectListener.class);

    private final Map<InetSocketAddress, Long> removedServers = new HashMap<InetSocketAddress, Long>();

    /**
     * 缓存服务器掉线超过指定时间后会重连
     */
    private final int maxAwayTime;

    ReconnectListener(final int maxAwayTime) {
        this.maxAwayTime = maxAwayTime;
    }

     
    public void onConnected(final MemcachedClient memcachedClient, final InetSocketAddress inetSocketAddress) {
        Long removedTime = removedServers.get(inetSocketAddress);

        if (removedTime != null && System.currentTimeMillis() - removedTime >= TimeUnit.SECONDS.toMillis(maxAwayTime)) {
            LOGGER.info("Memcached server {} is back and will be flushed", inetSocketAddress);
            new Thread(new Runnable() {

                 
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    try {
                        LOGGER.info("Flushing on memcached server {}", inetSocketAddress);
                        memcachedClient.flushAll(inetSocketAddress);
                        LOGGER.info("Memcached server {} flushed successfuly", inetSocketAddress);
                    } catch (Exception e) {
                        LOGGER.error("An error occured while flushing " + inetSocketAddress.toString(), e);
                    }
                }
            }).start();
        }

        removedServers.remove(inetSocketAddress);
    }

     
    public void onDisconnected(final MemcachedClient memcachedClient, final InetSocketAddress inetSocketAddress) {
        removedServers.put(inetSocketAddress, System.currentTimeMillis());
    }

     
    public void onException(final MemcachedClient memcachedClient, final Throwable throwable) {

    }

     
    public void onShutDown(final MemcachedClient memcachedClient) {

    }

     
    public void onStarted(final MemcachedClient memcachedClient) {

    }

}
