package com.power.platform.cache.providers.memcached;

import net.rubyeye.xmemcached.transcoders.CachedData;
import net.rubyeye.xmemcached.transcoders.CompressionMode;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.providers.CachedObject;


public class TranscoderAdapter implements Transcoder<Object> {

    private final CacheTranscoder transcoder;

    TranscoderAdapter(final CacheTranscoder transcoder) {
        this.transcoder = transcoder;
    }

     
    public Object decode(final CachedData d) {
        return transcoder.decode(new CachedObjectWrapper(d));
    }

     
    public CachedData encode(final Object o) {
        CachedObject cachedObject = transcoder.encode(o);
        return new CachedData(cachedObject.getFlags(), cachedObject.getData(), CachedObject.MAX_SIZE, -1);
    }

     
    public boolean isPackZeros() {
        throw new UnsupportedOperationException("TranscoderAdapter doesn't support pack zeros");
    }

     
    public boolean isPrimitiveAsString() {
        return false;
    }

     
    public void setCompressionThreshold(final int compressionThreshold) {
        throw new UnsupportedOperationException("TranscoderAdapter doesn't support compression threshold");
    }

     
    public void setPackZeros(final boolean packZeros) {
        throw new UnsupportedOperationException("TranscoderAdapter doesn't support pack zeros");
    }

     
    public void setPrimitiveAsString(final boolean primitiveAsString) {
        throw new UnsupportedOperationException("TranscoderAdapter doesn't support primitive as string");
    }

     
    public void setCompressionMode(final CompressionMode compressionMode) {
        throw new UnsupportedOperationException("TranscoderAdapter doesn't support compression mode");
    }

}
