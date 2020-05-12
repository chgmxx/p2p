package com.power.platform.cache.config;

/**
 * 通知相关AddressChangeListener更改服务器的地址
 * 
 * @author wulp
 */
public interface AddressChangeNotifier {

    void setAddressChangeListener(final AddressChangeListener listener);

}
