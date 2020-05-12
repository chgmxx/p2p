package com.power.platform.cache.config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Address provider that can be used to configure addresses of memcached servers as a string with comma or whitespace
 * separated addresses: "host:port host2:port" or "host:port,host2:port".
 * 
 * @author Jakub Białek
 * @author Nelson Carpentier
 * @since 2.0.0
 * 
 */
public class DefaultAddressProvider implements AddressProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAddressProvider.class);

    private String address;

    public DefaultAddressProvider() {

    }

    /**
     * 
     * @param address
     *            comma or whitespace separated list of servers' addresses
     */
    public DefaultAddressProvider(final String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Comma or whitespace separated list of servers' addresses.
     * <p>
     * Examples:
     * 
     * <pre>
     * setAddress(&quot;10.0.2.1:11211&quot;);
     * setAddress(&quot;myhost.com:11210,10.0.2.1:11211&quot;);
     * </pre>
     * 
     * @param addresses
     *            servers' addresses
     */
    public void setAddress(final String addresses) {
        this.address = addresses;
    }

     
    public List<InetSocketAddress> getAddresses() {
        LOGGER.info(String.format("Defined values %s will be used as memcached addresses", getAddress()));
        return getAddresses(address);
    }

    /**
     * Split a string containing whitespace or comma separated host or IP addresses and port numbers of the form
     * "host:port host2:port" or "host:port, host2:port" into a List of InetSocketAddress instances suitable for
     * instantiating a MemcachedClient.
     * 
     * Note that colon-delimited IPv6 is also supported. For example: ::1:11211
     */
    protected List<InetSocketAddress> getAddresses(final String s) {
        if (s == null) {
            throw new NullPointerException("Null host list");
        }
        if (s.trim().isEmpty()) {
            throw new IllegalArgumentException("No hosts in list: '" + s + "'");
        }

        List<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();
        for (String hoststuff : s.split("(?:\\s|,)+")) {
            if ("".equals(hoststuff)) {
                continue;
            }

            int finalColon = hoststuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server '" + hoststuff + "' in list:  " + s);

            }
            String hostPart = hoststuff.substring(0, finalColon);
            String portNum = hoststuff.substring(finalColon + 1);

            addrs.add(new InetSocketAddress(hostPart, Integer.parseInt(portNum)));
        }
        return addrs;
    }

    protected Logger getLogger() {
        return LOGGER;
    }

}
