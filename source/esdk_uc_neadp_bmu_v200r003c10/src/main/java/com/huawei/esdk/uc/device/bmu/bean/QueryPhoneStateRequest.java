package com.huawei.esdk.uc.device.bmu.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "body")
public class QueryPhoneStateRequest
{
    @XmlElement(name = "userid")
    private String userId;
    
    @XmlElement(name = "paramlist")
    private PhoneInfoListRequest phoneInfoList;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public PhoneInfoListRequest getPhoneInfoList()
    {
        return phoneInfoList;
    }

    public void setPhoneInfoList(PhoneInfoListRequest phoneInfoList)
    {
        this.phoneInfoList = phoneInfoList;
    }
}
