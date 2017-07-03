package com.thaiopp.utils;

import android.util.Log;

import com.thaiopp.vars.ConfigSetting;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebserviceUtil {


    private static Object resultRequestSOAP = null;


    public static String getWebServiceResult(String parms0, String parms1) {
        ConfigSetting cfg = GlobalVar.getInstance().getConfigSetting();
        String errMsg = "";
        SoapObject request = new SoapObject(cfg.nameSpace, cfg.operationName);
        request.addProperty("parms0", parms0);
        request.addProperty("parms1", parms1);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = request;
        HttpsUtil.allowAllSSL();
        HttpTransportSE androidHttpTransport = new HttpTransportSE(cfg.url, cfg.timeout);
        androidHttpTransport.debug = true;

        try {
            int v = 0;
            androidHttpTransport.call(cfg.soapAction, envelope);
            resultRequestSOAP = envelope.getResponse();

            Log.i("webservice", "soap response" + androidHttpTransport.responseDump);

            return resultRequestSOAP.toString();
        } catch (Exception aE) {
            aE.printStackTrace();
            errMsg = aE.getMessage();
            Log.i("webservice", "Error" + aE.getMessage());
        }
        return "{\"Table\":[{\"STAT\":\"F\",\"STAT_DESC\":\"WEBSERVICE ERROR >> " + errMsg + "\"}]}";
    }


}