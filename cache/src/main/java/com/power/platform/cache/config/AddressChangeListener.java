package com.power.platform.cache.config;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 更改缓存服务器的地址
 * 
 * @author wulp
 * 
 */
public interface AddressChangeListener {

    void changeAddresses(final List<InetSocketAddress> addresses);

}