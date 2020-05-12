package com.power.platform.cache.providers.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;

import com.google.code.yanf4j.core.SocketOption;
import com.power.platform.cache.providers.CacheClient;
import com.power.platform.cache.providers.CacheClientFactory;
import com.power.platform.cache.providers.CacheConfiguration;
import com.power.platform.cache.providers.CachedObject;

public class MemcacheClientFactoryImpl implements CacheClientFactory {

     
    public CacheClient create(final List<InetSocketAddress> addrs, final CacheConfiguration conf) throws IOException {
        MemcachedClientBuilder builder = null;
        if (conf instanceof XMemcachedConfiguration) {
            int[] weights = ((XMemcachedConfiguration) conf).getWeights();
            if (weights != null && weights.length > 0) {
                builder = new XMemcachedClientBuilder(addrs, weights);
            }
        }

        if (builder == null) {
            builder = new XMemcachedClientBuilder(addrs);
        }

        if (conf.isConsistentHashing()) {
            builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        }

        if (conf.isUseBinaryProtocol()) {
            builder.setCommandFactory(new BinaryCommandFactory());
        }

        //缓存数据大于1M时允许使用序列化
        builder.setTranscoder(new SerializingTranscoder(CachedObject.MAX_SIZE));

        if (conf instanceof XMemcachedConfiguration) {
            setProviderBuilderSpecificSettings(builder, (XMemcachedConfiguration) conf);
        }

        MemcachedClient client = builder.build();
        if (conf.getOperationTimeout() != null) {
            client.setOpTimeout(conf.getOperationTimeout());
        }

        if (conf instanceof XMemcachedConfiguration) {
            setProviderClientSpecificSettings(client, (XMemcachedConfiguration) conf);
        }

        return new MemcacheClientWrapper(client);
    }

    private void setProviderBuilderSpecificSettings(final MemcachedClientBuilder builder, final XMemcachedConfiguration conf) {
        if (conf.getConnectionPoolSize() != null) {
            builder.setConnectionPoolSize(conf.getConnectionPoolSize());
        }

        if (conf.getConfiguration() != null) {
            builder.setConfiguration(conf.getConfiguration());
        }

        if (conf.getFailureMode() != null) {
            builder.setFailureMode(conf.getFailureMode());
        }

        if (conf.getSocketOptions() != null) {
            for (Map.Entry<SocketOption<?>, Object> entry : conf.getSocketOptions().entrySet()) {
                builder.setSocketOption(entry.getKey(), entry.getValue());
            }
        }

        if (conf.getDefaultTranscoder() != null) {
            builder.setTranscoder(conf.getDefaultTranscoder());
        }

        if (conf.getConnectionTimeout() != null) {
            builder.setConnectTimeout(conf.getConnectionTimeout());
        }

        if (conf.getMaxQueuedNoReplyOperations() != null) {
            builder.setMaxQueuedNoReplyOperations(conf.getMaxQueuedNoReplyOperations());
        }

        if (conf.getEnableHealSession() != null) {
            builder.setEnableHealSession(conf.getEnableHealSession());
        }

        if (conf.getAuthInfoMap() != null) {
            builder.setAuthInfoMap(conf.getAuthInfoMap());
        }

        if (conf.getStateListeners() != null) {
            builder.setStateListeners(conf.getStateListeners());
        }
    }

    private void setProviderClientSpecificSettings(final MemcachedClient client, final XMemcachedConfiguration conf) {
        if (conf.getMaxAwayTime() != null) {
            client.addStateListener(new ReconnectListener(conf.getMaxAwayTime()));
        }

        if (conf.getEnableHeartBeat() != null) {
            client.setEnableHeartBeat(conf.getEnableHeartBeat());
        }

        if (conf.getHealSessionInterval() != null) {
            client.setHealSessionInterval(conf.getHealSessionInterval());
        }

        if (conf.getMergeFactor() != null) {
            client.setMergeFactor(conf.getMergeFactor());
        }

        if (conf.getOptimizeGet() != null) {
            client.setOptimizeGet(conf.getOptimizeGet());
        }

        if (conf.getOptimizeMergeBuffer() != null) {
            client.setOptimizeMergeBuffer(conf.getOptimizeMergeBuffer());
        }

        if (conf.getPrimitiveAsString() != null) {
            client.setPrimitiveAsString(conf.getPrimitiveAsString());
        }

        if (conf.getSanitizeKeys() != null) {
            client.setSanitizeKeys(conf.getSanitizeKeys());
        }

    }

}
