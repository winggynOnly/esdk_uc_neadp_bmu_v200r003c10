package com.huawei.esdk.uc.device.bmu.phone.convert;

import java.util.ArrayList;
import java.util.List;

import com.huawei.esdk.platform.common.bean.commu.RestReqMessage;
import com.huawei.esdk.uc.device.bean.XMLReqMsg;
import com.huawei.esdk.uc.device.bmu.bean.PhoneInfoListRequest;
import com.huawei.esdk.uc.device.bmu.bean.PhoneInfoRequest;
import com.huawei.esdk.uc.device.bmu.bean.QueryPhoneState;
import com.huawei.esdk.uc.device.bmu.bean.QueryPhoneStateRequest;
import com.huawei.esdk.uc.domain.model.bean.PhoneInfo;
import com.huawei.esdk.uc.domain.model.bean.PhoneState;
import com.huawei.esdk.uc.domain.model.bean.PhoneStateList;

public class UC23V2R3C10PhoneCapabilityConvert
{
    public RestReqMessage getQueryPhoneState(String userId, List<PhoneInfo> phoneInfoList)
    {
        RestReqMessage request = new RestReqMessage();
        request.setHttpMethod("POST");
        
        XMLReqMsg payload = new XMLReqMsg();
        
        QueryPhoneStateRequest queryPhoneStateRequst = new QueryPhoneStateRequest();
        queryPhoneStateRequst.setUserId(userId);
        PhoneInfoListRequest infoListRequest = new PhoneInfoListRequest();
        List<PhoneInfoRequest> pInfoList = new ArrayList<PhoneInfoRequest>();
        PhoneInfoRequest infoRequest = null;
        
        for (PhoneInfo info : phoneInfoList)
        {
            infoRequest = new PhoneInfoRequest();
            infoRequest.setGwIp(info.getGwIp());
            infoRequest.setNumber(info.getNumber());
            infoRequest.setSubPbx(info.getSubPbx());
            pInfoList.add(infoRequest);
        }
        
        infoListRequest.setPhoneInfoList(pInfoList);
        queryPhoneStateRequst.setPhoneInfoList(infoListRequest);
        payload.setBody(queryPhoneStateRequst);
        request.setPayload(payload);
        
        return request;
    }
    
    public PhoneStateList queryPhoneStateRest2Model(List<QueryPhoneState> phoneStateList)
    {
        if (null == phoneStateList || phoneStateList.isEmpty())
        {
            return null;
        }
        
        PhoneStateList list = new PhoneStateList();
        List<PhoneState> pStateList = new ArrayList<PhoneState>();
        PhoneState phoneState = null;
        
        for (QueryPhoneState queryPhoneState : phoneStateList)
        {
            phoneState = new PhoneState();
            phoneState.setGwIp(queryPhoneState.getGwIp());
            phoneState.setNumber(queryPhoneState.getNumber());
            phoneState.setState(queryPhoneState.getState());
            phoneState.setSubPbx(queryPhoneState.getSubPbx());
            pStateList.add(phoneState);
        }
        
        list.setPhoneStateList(pStateList);
        
        return list;
    }
}
