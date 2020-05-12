package com.power.platform.lanmao.rw.pojo;

import java.io.Serializable;

/**
 * 懒猫网关接口参数基类实体
 */

 public class BaseGatewayVo<T> implements Serializable {

	private static final long serialVersionUID = 1L;
    private String serviceName;
    private String platformNo;
    private String userDevice;
    private T reqData;
    private String keySerial;
    private String sign;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(String platformNo) {
        this.platformNo = platformNo;
    }

    public String getUserDevice() {
        return userDevice;
    }

    public void setUserDevice(String userDevice) {
        this.userDevice = userDevice;
    }

    public T getReqData() {
        return reqData;
    }

    public void setReqData(T reqData) {
        this.reqData = reqData;
    }

    public String getKeySerial() {
        return keySerial;
    }

    public void setKeySerial(String keySerial) {
        this.keySerial = keySerial;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    


 }