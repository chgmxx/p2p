package com.power.platform.cache.config;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 提供缓存服务器的地址
 * @author wulp
 */
public interface AddressProvider {

    List<InetSocketAddress> getAddresses();

}
