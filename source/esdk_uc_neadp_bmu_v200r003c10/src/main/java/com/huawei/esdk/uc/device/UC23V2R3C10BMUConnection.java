package com.huawei.esdk.uc.device;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

import com.huawei.esdk.platform.common.MessageContext;
import com.huawei.esdk.platform.common.ThreadLocalHolder;
import com.huawei.esdk.platform.common.bean.commu.RestReqMessage;
import com.huawei.esdk.platform.common.constants.ESDKErrorCodeConstant;
import com.huawei.esdk.platform.commu.itf.ISDKProtocolAdapter;
import com.huawei.esdk.platform.exception.ProtocolAdapterException;
import com.huawei.esdk.platform.nemgr.base.MultiConnDeviceBase;
import com.huawei.esdk.uc.common.UCConstant;
import com.huawei.esdk.uc.device.base.UCDeviceConnectionBase;
import com.huawei.esdk.uc.device.bean.XMLResMsg;
import com.huawei.esdk.uc.device.bmu.bean.UserAuthResBean;

public class UC23V2R3C10BMUConnection extends UCDeviceConnectionBase
{
    private static final Logger LOGGER = Logger.getLogger(UC23V2R3C10BMUConnection.class);

    private MultiConnDeviceBase device;

    private ISDKProtocolAdapter protocolAdapter;

    public UC23V2R3C10BMUConnection(ISDKProtocolAdapter protocolAdapter,
            MultiConnDeviceBase serviceProxy, String user, String pwd)
    {
        super(user, pwd);
        this.protocolAdapter = protocolAdapter;
        this.device = serviceProxy;
    }

    @Override
    public Object getServiceProxy(Class<?>[] itfs)
    {
        MessageContext mc = ThreadLocalHolder.get();
        if (null != getAdditionalData(UCConstant.APP_ID_DEV))
        {
            mc.getEntities().put(UCConstant.APP_ID_DEV, getAdditionalData(UCConstant.APP_ID_DEV));
            mc.getEntities().put(UCConstant.PWD_DEV, getAdditionalData(UCConstant.PWD_DEV));
        }
        
        if (itfs.length == 1)
        {
            if (itfs[0].isInstance(device))
            {
                return device;
            }
            return device.getService(itfs[0]);
        }
        else
        {
            LOGGER.debug("Intefaces number is not 1");
            return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    itfs, device.getService(itfs));
        }
    }

    @Override
    public boolean doHeartbeat(String connId)
    {
        return true;
    }

    @Override
    public boolean initConn(String connId)
    {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        
//        req.setMediaType("string");
//        req.setHttpMethod("POST");
//        String message = "<message><head><appid></appid></head></message>";
//        req.setPayload(message);
        
        MessageContext mc = ThreadLocalHolder.get();
        try
        {
            XMLResMsg result = (XMLResMsg) protocolAdapter.syncSendMessage(req, "/appAuth.action", UserAuthResBean.class.getName());
            
            String resultCode = result.getHead().getRetCode();
            mc.getEntities().put(UCConstant.UC_DEV_LOGIN_STATUS, resultCode);
            
            if ("0".equals(resultCode))
            {
                UserAuthResBean userAuthResBean = (UserAuthResBean) result.getBody();
                
                mc.getEntities().put(UCConstant.APP_ID_DEV, getLoginUser());
                mc.getEntities().put(UCConstant.PWD_DEV, userAuthResBean.getTag());
                setAdditionalData(UCConstant.APP_ID_DEV, getLoginUser());
                setAdditionalData(UCConstant.PWD_DEV, userAuthResBean.getTag());
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (ProtocolAdapterException e)
        {
            if (ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR == e.getErrorCode())
            {
                setAdditionalData("networkErrorFlag", "Y");
            }
            LOGGER.error("", e);
            mc.getEntities().put(UCConstant.UC_DEV_LOGIN_STATUS, "-1");
            return false;
        }
    }

    @Override
    public void destroyConn(String connId)
    {
        return;
    }

    @Override
    public int getKeepAliveTimes()
    {
        return 0;
    }

    @Override
    public int getKeepAlivePeriod()
    {
        return 9 * 60;
    }
}
