package com.power.platform.cache;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.power.platform.cache.config.AddressChangeListener;
import com.power.platform.cache.config.AddressChangeNotifier;
import com.power.platform.cache.config.AddressProvider;
import com.power.platform.cache.format.SerializationType;
import com.power.platform.cache.providers.CacheClient;
import com.power.platform.cache.providers.CacheClientFactory;
import com.power.platform.cache.providers.CacheConfiguration;
import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.transcoder.JavaTranscoder;

public class CacheFactory implements AddressChangeListener, FactoryBean<Cache>, InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheFactory.class);

    private CacheConfiguration configuration = new CacheConfiguration();

    private AddressProvider addressProvider;

    private CacheClientFactory cacheClientFactory;

    private String cacheName = "default";

    private Cache cache;

    private AddressChangeNotifier addressChangeNotifier;

    private SerializationType defaultSerializationType = SerializationType.PROVIDER;

    private JavaTranscoder javaTranscoder;

    private CacheTranscoder customTranscoder;

    private boolean initializeTranscoders = true;

     
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(configuration, "'configuration'不能为空");
        Assert.notNull(addressProvider, "'addressProvider'必需的,不能为空");
        Assert.notNull(cacheClientFactory, "'cacheClientFactory'必需的,不能为空");
        //Assert.notNull(cacheName, "'cacheName'不能为空");
        Assert.notNull(defaultSerializationType, "'defaultSerializationType'不能为空");

        if (initializeTranscoders) {
            if (javaTranscoder == null) {
                javaTranscoder = new JavaTranscoder();
            }
        }

        validateTranscoder(SerializationType.JAVA, javaTranscoder, "javaTranscoder");
        validateTranscoder(SerializationType.CUSTOM, customTranscoder, "customTranscoder");

        if (addressChangeNotifier != null) {
            addressChangeNotifier.setAddressChangeListener(this);
        }
    }

     
    public Cache getObject() throws Exception {
    	if (cache != null)
        	return cache;
        return createCache();
    }

     
    public Class<?> getObjectType() {
        return Cache.class;
    }

     
    public boolean isSingleton() {
        return true;
    }

     
    public void destroy() throws Exception {
        if (cache != null) {
            LOGGER.info("关闭缓存{}", cacheName);
            cache.shutdown();
        }
    }

     
    public void changeAddresses(final List<InetSocketAddress> addresses) {
        if (isCacheDisabled()) {
            LOGGER.warn("缓存{}不可用", cacheName);
            return;
        }

        if (!(cache instanceof CacheImpl)) {
            LOGGER.warn("该客户端不支持改变memcached地址");
            return;
        }

        try {
            LOGGER.info("创建新的memcached客户端缓存{} 新地址为: {}", cacheName, addresses);
            CacheClient memcacheClient = createClient(addresses);
            LOGGER.info("创建新的memcached客户端缓存{} 新地址为: {}", cacheName, addresses);
            ((CacheImpl)cache).changeCacheClient(memcacheClient);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("不能改变memcache客户端到一个新的地址 %s", addresses), e);
            }
        }
    }

    /**
     * 创建一个唯一的缓存对象.
     * 
     * @return cache
     * @throws IOException
     */
    protected Cache createCache() throws IOException {
        // 工厂类只创建一个缓存对象
        if (cache != null) {
        	//return cache;
            throw new IllegalStateException(String.format("已经创建了memcached客户端缓存 %s", cacheName));
        }

        if (isCacheDisabled()) {
            LOGGER.warn("缓存{}不可用", cacheName);
            cache = (Cache) Proxy.newProxyInstance(Cache.class.getClassLoader(), new Class[] { Cache.class },
                    new DisabledCacheInvocationHandler(cacheName));
            return cache;
        }

        if (configuration == null) {
            throw new RuntimeException(String.format("缓存 %s 的MemcachedConnectionBean必需定义!", cacheName));
        }

        List<InetSocketAddress> addrs = addressProvider.getAddresses();
        cache = new CacheImpl(cacheName, createClient(addrs), defaultSerializationType, javaTranscoder,
                customTranscoder);

        return cache;
    }

    boolean isCacheDisabled() {
        return false;
    }

    private CacheClient createClient(final List<InetSocketAddress> addrs) throws IOException {
        if (addrs == null || addrs.isEmpty()) {
            throw new IllegalArgumentException(String.format("没有为缓存 %s 指定地址", cacheName));
        }

        return cacheClientFactory.create(addrs, configuration);
    }

    private void validateTranscoder(final SerializationType serializationType, final CacheTranscoder cacheTranscoder,
            final String transcoderName) {
        if (defaultSerializationType == serializationType) {
            Assert.notNull(cacheTranscoder,
                    String.format(" 如果默认的序列化类型设置为 %s,'%s' 不能为null", serializationType, transcoderName));
        }
    }

	public CacheConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CacheConfiguration configuration) {
		this.configuration = configuration;
	}

	public AddressProvider getAddressProvider() {
		return addressProvider;
	}

	public void setAddressProvider(AddressProvider addressProvider) {
		this.addressProvider = addressProvider;
	}

	public CacheClientFactory getCacheClientFactory() {
		return cacheClientFactory;
	}

	public void setCacheClientFactory(CacheClientFactory cacheClientFactory) {
		this.cacheClientFactory = cacheClientFactory;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public AddressChangeNotifier getAddressChangeNotifier() {
		return addressChangeNotifier;
	}

	public void setAddressChangeNotifier(AddressChangeNotifier addressChangeNotifier) {
		this.addressChangeNotifier = addressChangeNotifier;
	}

	public SerializationType getDefaultSerializationType() {
		return defaultSerializationType;
	}

	public void setDefaultSerializationType(
			SerializationType defaultSerializationType) {
		this.defaultSerializationType = defaultSerializationType;
	}

	public JavaTranscoder getJavaTranscoder() {
		return javaTranscoder;
	}

	public void setJavaTranscoder(JavaTranscoder javaTranscoder) {
		this.javaTranscoder = javaTranscoder;
	}

	public CacheTranscoder getCustomTranscoder() {
		return customTranscoder;
	}

	public void setCustomTranscoder(CacheTranscoder customTranscoder) {
		this.customTranscoder = customTranscoder;
	}

	public boolean isInitializeTranscoders() {
		return initializeTranscoders;
	}

	public void setInitializeTranscoders(boolean initializeTranscoders) {
		this.initializeTranscoders = initializeTranscoders;
	}
}
