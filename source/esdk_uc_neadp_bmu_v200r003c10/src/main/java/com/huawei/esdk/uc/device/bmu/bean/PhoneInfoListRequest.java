package com.huawei.esdk.uc.device.bmu.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "PhoneInfoListRequest")
public class PhoneInfoListRequest
{
    @XmlElement(name = "bean")
    private List<PhoneInfoRequest> phoneInfoList;

    public List<PhoneInfoRequest> getPhoneInfoList()
    {
        return phoneInfoList;
    }

    public void setPhoneInfoList(List<PhoneInfoRequest> phoneInfoList)
    {
        this.phoneInfoList = phoneInfoList;
    }
}
