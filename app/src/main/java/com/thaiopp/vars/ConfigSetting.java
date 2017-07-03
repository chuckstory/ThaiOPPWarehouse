package com.thaiopp.vars;

public class ConfigSetting {
    public String url;
    public String soapAction;
    public String operationName;
    public String nameSpace;
    public int timeout;
    public String pin;
    public String recLog;

    public ConfigSetting(String vUrl, String vSoapAction, String vOperationName, String vNameSpace, int vTimeout, String vPin, String vLog) {
        url = vUrl;
        soapAction = vSoapAction;
        operationName = vOperationName;
        nameSpace = vNameSpace;
        timeout = vTimeout;
        pin = vPin;
        recLog = vLog;
    }

}
