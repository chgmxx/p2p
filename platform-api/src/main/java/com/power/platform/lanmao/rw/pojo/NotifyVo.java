package com.power.platform.lanmao.rw.pojo;

/**
 * 通知参数对象
 */

public class NotifyVo { 
    private String serviceName; // 接口名称，见每个接口的详细定义
    private String platformNo; //  平台编号
    private String responseType; // 回调类型，见枚举“回调类型“，用来区分是浏览器返回还是服务端异步通知
    private String keySerial; //  证书序号，用于多证书密钥切换，默认值为 1
    private String respData; //  业务数据报文，JSON 格式，具体见各接口定义
    private String sign; //  对 respData 参数的签名，签名算法见下方“参数签名”章节

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

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getKeySerial() {
        return keySerial;
    }

    public void setKeySerial(String keySerial) {
        this.keySerial = keySerial;
    }

    public String getRespData() {
        return respData;
    }

    public void setRespData(String respData) {
        this.respData = respData;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    
}