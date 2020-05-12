package com.power.platform.cache.providers.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.CachedData;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.cache.CacheException;
import com.power.platform.cache.providers.AbstractCacheClientWrapper;
import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.providers.CachedObject;
import com.power.platform.cache.providers.CachedObjectImpl;

public class MemcacheClientWrapper extends AbstractCacheClientWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemcacheClientWrapper.class);

    private final Map<CacheTranscoder, Object> adapters = new HashMap<CacheTranscoder, Object>();

    private final MemcachedClient memcachedClient;

    MemcacheClientWrapper(final MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

     
    public boolean add(final String key, final int exp, final Object value) throws TimeoutException, CacheException {
        try {
            return memcachedClient.add(key, exp, value);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> boolean add(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException {
        try {
            return memcachedClient.add(key, exp, value, getTranscoder(transcoder));
        } catch (MemcachedException e) {
            throw new CacheException(e);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        }
    }
    
     
    public boolean replace(final String key, final int exp, final Object value) throws TimeoutException, CacheException{
    	try {
            return memcachedClient.replace(key, exp, value);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> boolean replace(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException{
    	try {
            return memcachedClient.replace(key, exp, value, getTranscoder(transcoder));
        } catch (MemcachedException e) {
            throw new CacheException(e);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        }
    }

     
    public long decr(final String key, final int by) throws TimeoutException, CacheException {
        try {
            return memcachedClient.decr(key, by);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public long decr(final String key, final int by, final long def) throws TimeoutException, CacheException {
        try {
            return memcachedClient.decr(key, by, def);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public boolean delete(final String key) throws TimeoutException, CacheException {
        try {
            return memcachedClient.delete(key);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public void flush() throws TimeoutException, CacheException {
        try {
            memcachedClient.flushAll();
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public Object get(final String key) throws TimeoutException, CacheException {
        try {
            return memcachedClient.get(key);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> T get(final String key, final CacheTranscoder transcoder) throws TimeoutException, CacheException {
        try {
            return memcachedClient.get(key, this.<T> getTranscoder(transcoder));
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> T get(final String key, final CacheTranscoder transcoder, final long timeout) throws TimeoutException, CacheException {
        try {
            return memcachedClient.get(key, timeout, this.<T> getTranscoder(transcoder));
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public Collection<SocketAddress> getAvailableServers() {
        List<SocketAddress> servers = new ArrayList<SocketAddress>();
        Collection<InetSocketAddress> inetSocketAddresses = memcachedClient.getAvailableServers();

        if (inetSocketAddresses != null && !inetSocketAddresses.isEmpty()) {
            servers.addAll(inetSocketAddresses);
        }

        return servers;
    }

     
    public Map<String, Object> getBulk(final Collection<String> keys) throws TimeoutException, CacheException {
        Map<String, Object> result = null;
        try {
            result = memcachedClient.get(keys);
            return (result == null) ? Collections.<String, Object> emptyMap() : result;
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> Map<String, T> getBulk(final Collection<String> keys, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException {
        Map<String, T> result = null;
        try {
            result = memcachedClient.get(keys, this.<T> getTranscoder(transcoder));
            return (result == null) ? Collections.<String, T> emptyMap() : result;
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public long incr(final String key, final int by) throws TimeoutException, CacheException {
        try {
            return memcachedClient.incr(key, by);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public long incr(final String key, final int by, final long def) throws TimeoutException, CacheException {
        try {
            return memcachedClient.incr(key, by, def);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public long incr(final String key, final int by, final long def, final int expiration) throws TimeoutException, CacheException {
        try {
            return memcachedClient.incr(key, by, def, memcachedClient.getOpTimeout(), expiration);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public boolean set(final String key, final int exp, final Object value) throws TimeoutException, CacheException {
        try {
            return memcachedClient.set(key, exp, value);
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public <T> boolean set(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException {
        try {
            return memcachedClient.set(key, exp, value, getTranscoder(transcoder));
        } catch (InterruptedException e) {
            throw new CacheException(e);
        } catch (MemcachedException e) {
            throw new CacheException(e);
        }
    }

     
    public void shutdown() {
        try {
            memcachedClient.shutdown();
        } catch (IOException e) {
            LOGGER.error("An error occurred when closing memcache", e);
        }
    }

     
    @SuppressWarnings("unchecked")
    public CacheTranscoder getTranscoder() {
        return new TranscoderWrapper(memcachedClient.getTranscoder());
    }

     
    public Object getNativeClient() {
        return memcachedClient;
    }

    @SuppressWarnings("unchecked")
    private <T> Transcoder<T> getTranscoder(final CacheTranscoder transcoder) {
        Transcoder<T> transcoderAdapter = (Transcoder<T>) adapters.get(transcoder);
        if (transcoderAdapter == null) {
            transcoderAdapter = (Transcoder<T>) new TranscoderAdapter(transcoder);
            adapters.put(transcoder, transcoderAdapter);
        }

        return transcoderAdapter;
    }

    private static class TranscoderWrapper implements CacheTranscoder {

        private final Transcoder<Object> transcoder;

        public TranscoderWrapper(final Transcoder<Object> transcoder) {
            this.transcoder = transcoder;
        }

         
        public Object decode(final CachedObject data) {
            return transcoder.decode(new CachedData(data.getFlags(), data.getData()));
        }

         
        public CachedObject encode(final Object o) {
            CachedData cachedData = transcoder.encode(o);
            return new CachedObjectImpl(cachedData.getFlag(), cachedData.getData());
        }
    }
}
