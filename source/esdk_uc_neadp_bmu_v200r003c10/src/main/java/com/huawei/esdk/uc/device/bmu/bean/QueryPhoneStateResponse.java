package com.huawei.esdk.uc.device.bmu.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "body")
public class QueryPhoneStateResponse
{
    @XmlElementWrapper(name = "beans")
    @XmlElement(name = "bean")
    private List<QueryPhoneState> phoneStateList;

    public List<QueryPhoneState> getPhoneStateList()
    {
        return phoneStateList;
    }

    public void setPhoneStateList(List<QueryPhoneState> phoneStateList)
    {
        this.phoneStateList = phoneStateList;
    }
}
