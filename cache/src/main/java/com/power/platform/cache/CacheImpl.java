package com.power.platform.cache;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.power.platform.cache.format.SerializationType;
import com.power.platform.cache.providers.CacheClient;
import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.transcoder.JavaTranscoder;
import com.power.platform.cache.transcoder.LongToStringTranscoder;

public class CacheImpl implements Cache {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheImpl.class);
	
	private final String name;
	
	private final SerializationType defaultSerializationType;

    private final JavaTranscoder javaTranscoder;

    private final LongToStringTranscoder longToStringTranscoder = new LongToStringTranscoder();

    private final CacheTranscoder customTranscoder;

    private volatile CacheClient cacheClient;

    CacheImpl(final String name, final CacheClient cacheClient,
            final SerializationType defaultSerializationType, final JavaTranscoder javaTranscoder,
            final CacheTranscoder customTranscoder) {
        Assert.notNull(cacheClient, "'cacheClient' 不能为空");
        Assert.notNull(defaultSerializationType, "'defaultSerializationType' 不能为空");
        validateTranscoder(SerializationType.JAVA, javaTranscoder, "javaTranscoder");
        validateTranscoder(SerializationType.CUSTOM, customTranscoder, "customTranscoder");
        
        this.name = name;
        this.cacheClient = cacheClient;
        this.defaultSerializationType = defaultSerializationType;
        this.javaTranscoder = javaTranscoder;
        this.customTranscoder = customTranscoder;
    }

     
    public Collection<SocketAddress> getAvailableServers() {
        return cacheClient.getAvailableServers();
    }
    
	 
	@SuppressWarnings("unchecked")
	public <T> boolean add(String key, Object value) throws TimeoutException,
			CacheException {
        return add(key, 0, (T)value, SerializationType.PROVIDER, null);
	}

	 
	@SuppressWarnings("unchecked")
	public <T> boolean add(String key, int exp, Object value)
			throws TimeoutException, CacheException {
		return add(key, exp, (T)value, SerializationType.PROVIDER, null);
	}

     
    public <T> boolean add(final String cacheKey, final int expiration, final Object value, final SerializationType serializationType)
            throws TimeoutException, CacheException {
        final boolean added;

        switch (getSerializationType(serializationType)) {
        case JAVA:
            added = add(cacheKey, expiration, value, SerializationType.JAVA, javaTranscoder);
            break;
        case PROVIDER:
            added = add(cacheKey, expiration, value, SerializationType.PROVIDER, null);
            break;
        case CUSTOM:
            added = add(cacheKey, expiration, value, SerializationType.CUSTOM, customTranscoder);
            break;
        default:
            throw new IllegalArgumentException(String.format("不支持序列化类型 %s", serializationType));
        }

        return added;
    }
	
	 
	public <T> T get(String key) throws TimeoutException, CacheException {
		return get(key, SerializationType.PROVIDER, null);
	}

     
    public <T> T get(final String cacheKey, final SerializationType serializationType) throws TimeoutException, CacheException {

        switch (getSerializationType(serializationType)) {
        case JAVA:
            return get(cacheKey, SerializationType.JAVA, javaTranscoder);
        case PROVIDER:
            return get(cacheKey, SerializationType.PROVIDER, null);
        case CUSTOM:
            return get(cacheKey, SerializationType.CUSTOM, customTranscoder);
        default:
            throw new IllegalArgumentException(String.format("不支持序列化类型 %s", serializationType));
        }

    }

     
    @SuppressWarnings("unchecked")
    public <T> void set(final String cacheKey, final int expiration, final Object value, final SerializationType serializationType)
            throws TimeoutException, CacheException {

        switch (getSerializationType(serializationType)) {
        case JAVA:
            set(cacheKey, expiration, (T) value, SerializationType.JAVA, javaTranscoder);
            break;
        case PROVIDER:
            set(cacheKey, expiration, (T) value, SerializationType.PROVIDER, null);
            break;
        case CUSTOM:
            set(cacheKey, expiration, (T) value, SerializationType.CUSTOM, customTranscoder);
            break;
        default:
            throw new IllegalArgumentException(String.format("不支持序列化类型 %s", serializationType));
        }
    }
    
	 
	@SuppressWarnings("unchecked")
	public <T> void set(String key, Object value) throws TimeoutException,
			CacheException {
    	set(key, 0, (T) value, SerializationType.PROVIDER, null);
	}

	 
	@SuppressWarnings("unchecked")
	public <T> void set(String key, int exp, Object value)
			throws TimeoutException, CacheException {
		set(key, exp, (T) value, SerializationType.PROVIDER, null);
	}

	 
	@SuppressWarnings("unchecked")
	public <T> boolean replace(String key, Object value) throws TimeoutException,
			CacheException {
		return replace(key ,0 , (T) value, SerializationType.PROVIDER, null);
	}

	 
	@SuppressWarnings("unchecked")
	public <T> boolean replace(String key, int exp, Object value)
			throws TimeoutException, CacheException {
		return replace(key ,0 , (T) value, SerializationType.PROVIDER, null);
	}

	 
	@SuppressWarnings("unchecked")
	public <T> boolean replace(String key, int exp, Object value,
			SerializationType serializationType) throws TimeoutException,
			CacheException {
		switch (getSerializationType(serializationType)) {
        case JAVA:
            return replace(key, exp, (T) value, SerializationType.JAVA, javaTranscoder);
        case PROVIDER:
        	return replace(key, exp, (T) value, SerializationType.PROVIDER, null);
        case CUSTOM:
        	return replace(key, exp, (T) value, SerializationType.CUSTOM, customTranscoder);
        default:
            throw new IllegalArgumentException(String.format("不支持序列化类型 %s", serializationType));
        }
	}

    
     
	public Map<String, Object> getBulk(Collection<String> keys)
			throws TimeoutException, CacheException {
    	return getBulk(keys, SerializationType.PROVIDER, null);
	}

     
    public Map<String, Object> getBulk(final Collection<String> keys, final SerializationType serializationType) throws TimeoutException,
            CacheException {

        switch (getSerializationType(serializationType)) {
        case JAVA:
            return getBulk(keys, SerializationType.JAVA, javaTranscoder);
        case PROVIDER:
            return getBulk(keys, SerializationType.PROVIDER, null);
        case CUSTOM:
            return getBulk(keys, SerializationType.CUSTOM, customTranscoder);
        default:
            throw new IllegalArgumentException(String.format("不支持序列化类型 %s", serializationType));
        }
    }

     
    public long decr(final String key, final int by) throws TimeoutException, CacheException {
        return cacheClient.decr(key, by);
    }

     
    public boolean delete(final String key) throws TimeoutException, CacheException {
        return cacheClient.delete(key);
    }

     
    public void delete(final Collection<String> keys) throws TimeoutException, CacheException {
        cacheClient.delete(keys);
    }

     
    public void flush() throws TimeoutException, CacheException {
        cacheClient.flush();
    }

     
    public long incr(final String key, final int by, final long def) throws TimeoutException, CacheException {
        return cacheClient.incr(key, by, def);
    }

     
    public long incr(final String key, final int by, final long def, final int exp) throws TimeoutException, CacheException {
        return cacheClient.incr(key, by, def, exp);
    }

     
    public boolean isEnabled() {
        return true;
    }

     
    public Long getCounter(final String cacheKey) throws TimeoutException, CacheException {
        return cacheClient.get(cacheKey, longToStringTranscoder);
    }

     
    public void setCounter(final String cacheKey, final int expiration, final long value) throws TimeoutException, CacheException {
        cacheClient.set(cacheKey, expiration, value, longToStringTranscoder);
    }

     
    public void shutdown() {
        cacheClient.shutdown();
    }

    void changeCacheClient(final CacheClient newCacheClient) {
        if (newCacheClient != null) {
            LOGGER.info("替换cacheClient");
            CacheClient oldCacheClient = cacheClient;
            cacheClient = newCacheClient;
            LOGGER.info("cacheClient替换完成");
            LOGGER.info("关闭原有的cacheClient");
            oldCacheClient.shutdown();
            LOGGER.info("原有的cacheClient关闭完成");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(final String cacheKey, final SerializationType serializationType, final CacheTranscoder cacheTranscoder)
            throws TimeoutException, CacheException {
        if (SerializationType.PROVIDER.equals(serializationType)) {
            return (T) cacheClient.get(cacheKey);
        }

        if (cacheTranscoder == null) {
            throw new IllegalArgumentException(String.format("不能使用 %s 序列化,因为指定的缓存代码转换器为null!",
                    serializationType));
        }

        return (T) cacheClient.get(cacheKey, cacheTranscoder);
    }

    private <T> void set(final String cacheKey, final int expiration, final T value, final SerializationType serializationType,
            final CacheTranscoder cacheTranscoder) throws TimeoutException, CacheException {
        if (SerializationType.PROVIDER.equals(serializationType)) {
            cacheClient.set(cacheKey, expiration, value);
            return;
        }

        if (cacheTranscoder == null) {
            throw new IllegalArgumentException(String.format("不能使用 %s 序列化,因为指定的缓存代码转换器为null!",
                    serializationType));
        }

        cacheClient.set(cacheKey, expiration, value, cacheTranscoder);
    }

    private <T> boolean add(final String cacheKey, final int expiration, final Object value, final SerializationType serializationType,
            final CacheTranscoder cacheTranscoder) throws TimeoutException, CacheException {
        if (SerializationType.PROVIDER.equals(serializationType)) {
            return cacheClient.add(cacheKey, expiration, value);
        }

        if (cacheTranscoder == null) {
            throw new IllegalArgumentException(String.format("不能使用 %s 序列化,因为指定的缓存代码转换器为null!",
                    serializationType));
        }

        return cacheClient.add(cacheKey, expiration, value, cacheTranscoder);
    }
    
    private <T> boolean replace(final String cacheKey, final int expiration, final T value, final SerializationType serializationType,
            final CacheTranscoder cacheTranscoder) throws TimeoutException, CacheException {
        if (SerializationType.PROVIDER.equals(serializationType)) {
        	return cacheClient.replace(cacheKey, expiration, value);
        }

        if (cacheTranscoder == null) {
            throw new IllegalArgumentException(String.format("不能使用 %s 序列化,因为指定的缓存代码转换器为null!",
                    serializationType));
        }

        return cacheClient.replace(cacheKey, expiration, value, cacheTranscoder);
    }

    private Map<String, Object> getBulk(final Collection<String> keys, final SerializationType serializationType,
            final CacheTranscoder cacheTranscoder) throws TimeoutException, CacheException {
        if (SerializationType.PROVIDER.equals(serializationType)) {
            return cacheClient.getBulk(keys);
        }

        if (cacheTranscoder == null) {
            throw new IllegalArgumentException(String.format("不能使用 %s 序列化,因为指定的缓存代码转换器为null!",
                    serializationType));
        }

        return cacheClient.getBulk(keys, cacheTranscoder);
    }

    private SerializationType getSerializationType(final SerializationType serializationType) {
        return (serializationType != null) ? serializationType : defaultSerializationType;
    }

    @SuppressWarnings("unused")
	private void warn(final Exception e, final String format, final Object... args) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(String.format(format, args), e);
        }
    }

    private void validateTranscoder(final SerializationType serializationType, final CacheTranscoder cacheTranscoder,
            final String transcoderName) {
        if (defaultSerializationType == serializationType) {
            Assert.notNull(cacheTranscoder,
                    String.format("如果默认的序列化类型设置为 %s,'%s' 不能为null", transcoderName, serializationType));
        }
    }

	 
	public String getName() {
		return this.name;
	}
}
