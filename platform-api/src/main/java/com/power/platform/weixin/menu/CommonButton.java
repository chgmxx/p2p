package com.power.platform.weixin.menu;

public class CommonButton extends Button {
    private String type;
    private String key;
    private String url;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
