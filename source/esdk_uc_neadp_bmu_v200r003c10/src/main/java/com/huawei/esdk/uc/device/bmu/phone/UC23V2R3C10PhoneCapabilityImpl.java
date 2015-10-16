package com.huawei.esdk.uc.device.bmu.phone;

import java.util.List;

import org.apache.log4j.Logger;

import com.huawei.esdk.platform.common.SDKResult;
import com.huawei.esdk.platform.common.bean.commu.RestReqMessage;
import com.huawei.esdk.platform.commu.itf.ISDKProtocolAdapter;
import com.huawei.esdk.platform.exception.ProtocolAdapterException;
import com.huawei.esdk.uc.device.UC23V2R3C10AbstractBMUCapability;
import com.huawei.esdk.uc.device.bean.XMLResMsg;
import com.huawei.esdk.uc.device.bmu.IPhoneCapability;
import com.huawei.esdk.uc.device.bmu.bean.QueryPhoneState;
import com.huawei.esdk.uc.device.bmu.bean.QueryPhoneStateResponse;
import com.huawei.esdk.uc.device.bmu.phone.convert.UC23V2R3C10PhoneCapabilityConvert;
import com.huawei.esdk.uc.domain.model.bean.PhoneInfo;
import com.huawei.esdk.uc.domain.model.bean.PhoneStateList;

public class UC23V2R3C10PhoneCapabilityImpl extends UC23V2R3C10AbstractBMUCapability implements IPhoneCapability
{
    private static final Logger LOGGER = Logger.getLogger(UC23V2R3C10PhoneCapabilityImpl.class);
    
    private static final UC23V2R3C10PhoneCapabilityConvert PHONE_CAPABILITY_CONVERT = new UC23V2R3C10PhoneCapabilityConvert();
    
    public UC23V2R3C10PhoneCapabilityImpl(ISDKProtocolAdapter protocolAdapter)
    {
        super(protocolAdapter);
    }
    
    @Override
    public SDKResult<PhoneStateList> queryPhoneState(String userId, List<PhoneInfo> phoneInfoList)
    {
        LOGGER.debug("query phone state begin");
        
        SDKResult<PhoneStateList> result = new SDKResult<PhoneStateList>();
        
        // 拼装报文
        RestReqMessage reqMessage = PHONE_CAPABILITY_CONVERT.getQueryPhoneState(userId, phoneInfoList);
        
        try
        {
            // 发送消息
            XMLResMsg xmlResMsg =
                sendMessage(reqMessage, "queryPhoneState.action", QueryPhoneStateResponse.class.getName());
            result.setErrCode(Integer.valueOf(xmlResMsg.getHead().getRetCode()));
            result.setDescription(xmlResMsg.getHead().getRetContext());
            
            // 结果转换
            if (null != xmlResMsg.getBody())
            {
                List<QueryPhoneState> phoneStateList = ((QueryPhoneStateResponse)xmlResMsg.getBody()).getPhoneStateList();
                result.setResult(PHONE_CAPABILITY_CONVERT.queryPhoneStateRest2Model(phoneStateList));
            }
            
        }
        catch (ProtocolAdapterException e)
        {
            result.setErrCode(e.getErrorCode());
            LOGGER.error("query phone state error", e);
        }
        
        LOGGER.debug("query phone state end");
        
        return result;
    }
    
}
