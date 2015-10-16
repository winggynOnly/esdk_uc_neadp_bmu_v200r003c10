package com.huawei.esdk.uc.device;

import java.util.Date;
import java.util.UUID;

import com.huawei.esdk.platform.common.MessageContext;
import com.huawei.esdk.platform.common.ThreadLocalHolder;
import com.huawei.esdk.platform.common.bean.callback.CallbackItfInfo;
import com.huawei.esdk.platform.common.bean.callback.CallbackMessage;
import com.huawei.esdk.platform.common.bean.commu.RestReqMessage;
import com.huawei.esdk.platform.common.bean.log.InterfaceLogBean;
import com.huawei.esdk.platform.common.config.ConfigManager;
import com.huawei.esdk.platform.common.constants.ESDKConstant;
import com.huawei.esdk.platform.common.utils.ApplicationContextUtil;
import com.huawei.esdk.platform.common.utils.OSUtils;
import com.huawei.esdk.platform.common.utils.StringUtils;
import com.huawei.esdk.platform.commu.itf.ISDKProtocolAdapter;
import com.huawei.esdk.platform.exception.ProtocolAdapterException;
import com.huawei.esdk.platform.log.itf.IInterfaceLog;
import com.huawei.esdk.platform.nemgr.base.NotifyCollector;
import com.huawei.esdk.uc.common.ErrInfo;
import com.huawei.esdk.uc.common.UCConstant;
import com.huawei.esdk.uc.device.bean.XMLReqHeader;
import com.huawei.esdk.uc.device.bean.XMLReqMsg;
import com.huawei.esdk.uc.device.bean.XMLResMsg;

public abstract class UC23V2R3C10AbstractBMUCapability
{
    protected ISDKProtocolAdapter protocolAdapter;
    
    private NotifyCollector collector = ApplicationContextUtil.getBean("notifyCollector");
    
    private String targetIP;
    
    public UC23V2R3C10AbstractBMUCapability(ISDKProtocolAdapter protocolAdapter)
    {
        this.protocolAdapter = protocolAdapter;
        this.targetIP = StringUtils.retrieveMachineAddr(protocolAdapter.getServiceAccessPoint());
    }
    
    private void doInterfaceLogReq(String messageId, String interfaceName)
    {
        InterfaceLogBean bean = new InterfaceLogBean();
        bean.setTransactionId(messageId);
        bean.setProduct("UC");
        bean.setInterfaceType("2");
        bean.setProtocolType("REST");
        bean.setReq(true);
        bean.setName(interfaceName);
        bean.setSourceAddr(OSUtils.getLocalIP());
        bean.setTargetAddr(targetIP);
        bean.setReqTime(new Date());
        
        IInterfaceLog logger = ApplicationContextUtil.getBean("interfaceLogger");
        logger.info(bean);
    }
    
    private void doInterfaceLogRes(String messageId, String resultCode)
    {
        InterfaceLogBean bean = new InterfaceLogBean();
        bean.setTransactionId(messageId);
        bean.setReq(false);
        bean.setRespTime(new Date());
        bean.setResultCode(resultCode);
        
        IInterfaceLog logger = ApplicationContextUtil.getBean("interfaceLogger");
        logger.info(bean);
    }
    
    protected XMLResMsg sendMessage(RestReqMessage request, String apiName, String resObjClass)
        throws ProtocolAdapterException
    {
        String uuid = UUID.randomUUID().toString();
        doInterfaceLogReq(uuid, apiName);
        
        XMLReqMsg payload = (XMLReqMsg) request.getPayload();
        MessageContext mc = ThreadLocalHolder.get();
        XMLReqHeader head = payload.getHead();
        if (null == head)
        {
            head = new XMLReqHeader();
            payload.setHead(head);
        }
        payload.getHead().setAppId((String)mc.getEntities().get(UCConstant.APP_ID_DEV));
        payload.getHead().setTag((String) mc.getEntities().get(UCConstant.PWD_DEV));
        
        
        XMLResMsg xmlResMsg = (XMLResMsg)protocolAdapter.syncSendMessage(request, apiName, resObjClass);
        
        if (null != xmlResMsg && null != xmlResMsg.getHead() && ErrInfo.SDK_UC_APPAGENT_RET.equals(xmlResMsg.getHead().getRetCode()))
        {
            String devId = ConfigManager.getInstance().getValue("esdk.uc_bmu_device");
            
            synchronized (UC23V2R3C10AbstractBMUCapability.class)
            {
                publishMessage(devId, ESDKConstant.NOTIFY_ITFNAME_DISCONNECT, devId);
                
                publishMessage(devId, ESDKConstant.NOTIFY_ITFNAME_CONNECT, devId);
            }
            
            payload.getHead().setAppId((String)mc.getEntities().get(UCConstant.APP_ID_DEV));
            payload.getHead().setTag((String) mc.getEntities().get(UCConstant.PWD_DEV));
            
            xmlResMsg = (XMLResMsg)protocolAdapter.syncSendMessage(request, apiName, resObjClass);
        }

        doInterfaceLogRes(uuid, xmlResMsg.getHead().getRetCode());
        return xmlResMsg;
    }
    
    private void publishMessage(String devId, String itfName, String processorId)
    {
        CallbackMessage message = new CallbackMessage();
        CallbackItfInfo callbackItfInfo = new CallbackItfInfo();
        callbackItfInfo.setDevId(devId);
        callbackItfInfo.setItfName(itfName);
        callbackItfInfo.setNotifyMsgType(ESDKConstant.NOTIFY_MSG_TYPE_ESDK_EVENT);
        callbackItfInfo.setProcessorId(processorId);
        message.setCallbackItfInfo(callbackItfInfo);
        collector.publishNotify(message);
    }
}
