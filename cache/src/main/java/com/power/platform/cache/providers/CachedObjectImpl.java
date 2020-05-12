package com.power.platform.cache.providers;

import java.util.Arrays;

public class CachedObjectImpl implements CachedObject {

    private final int flags;

    private final byte[] data;

    public CachedObjectImpl(final int flags, final byte[] data) {
        this.flags = flags;
        this.data = data;
    }

     
    public String toString() {
        return "CachedObjectImpl [flags=" + flags + ", data=" + Arrays.toString(data) + "]";
    }

	 
	public byte[] getData() {
		return data;
	}

	 
	public int getFlags() {
		return flags;
	}

}