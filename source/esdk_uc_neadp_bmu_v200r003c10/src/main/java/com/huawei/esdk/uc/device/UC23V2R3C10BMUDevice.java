package com.huawei.esdk.uc.device;

import org.apache.log4j.Logger;

import com.huawei.esdk.platform.common.MessageContext;
import com.huawei.esdk.platform.common.ThreadLocalHolder;
import com.huawei.esdk.platform.common.bean.aa.AccountInfo;
import com.huawei.esdk.platform.common.constants.ESDKConstant;
import com.huawei.esdk.platform.common.utils.ApplicationContextUtil;
import com.huawei.esdk.platform.commu.itf.IProtocolAdapterManager;
import com.huawei.esdk.platform.commu.itf.ISDKProtocolAdapter;
import com.huawei.esdk.platform.nemgr.base.MultiConnDeviceBase;
import com.huawei.esdk.platform.nemgr.itf.IDeviceConnection;
import com.huawei.esdk.platform.nemgr.itf.IDeviceManager;
import com.huawei.esdk.uc.device.bmu.IAttendeeCapability;
import com.huawei.esdk.uc.device.bmu.ICTCCapability;
import com.huawei.esdk.uc.device.bmu.IDepartmentCapability;
import com.huawei.esdk.uc.device.bmu.IPhoneCapability;
import com.huawei.esdk.uc.device.bmu.IRoleCapability;
import com.huawei.esdk.uc.device.bmu.IUserLevelCapability;
import com.huawei.esdk.uc.device.bmu.account.UC23V2R3C10AccountCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.conf.UC23V2R3C10ConfCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.attendee.UC23V2R3C10AttendeeCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.ctc.UC23V2R3C10CTCCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.department.UC23V2R3C10DepartmentCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.phone.UC23V2R3C10PhoneCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.role.UC23V2R3C10RoleCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.sip.UC23V2R3C10SIPCapabilityImpl;
import com.huawei.esdk.uc.device.bmu.userlevel.UC23V2R3C10UserLevelCapabilityImpl;
import com.huawei.esdk.uc.device.obg.IAccountCapability;
import com.huawei.esdk.uc.device.obg.IConfCapability;
import com.huawei.esdk.uc.device.obg.ISIPCapability;

public class UC23V2R3C10BMUDevice extends MultiConnDeviceBase
{
    private static final Logger LOGGER = Logger
            .getLogger(MultiConnDeviceBase.class);

    private IDeviceManager deviceManager = ApplicationContextUtil
            .getBean("deviceManager");

    protected IProtocolAdapterManager protocolAdapterManager = ApplicationContextUtil
            .getBean("protocolAdapterManager");

    /*
     * Service Access Point (URL)
     */
    private String sap;

    protected ISDKProtocolAdapter protocolAdapter;

    protected String getSap()
    {
        return sap;
    }

    protected void setSap(String sap)
    {
        this.sap = sap;
    }

    public UC23V2R3C10BMUDevice(String sap)
    {
        this.sap = sap;
        this.protocolAdapter = (ISDKProtocolAdapter) protocolAdapterManager
                .getProtocolInstanceByType(ESDKConstant.PROTOCOL_ADAPTER_TYPE_REST, sap);
        protocolAdapter.setSdkProtocolAdatperCustProvider(new UC23V2R3C10BMURestCustProvider());

        prepareDeviceCapability(sap);
    }

    protected void prepareDeviceCapability(String sap)
    {
        LOGGER.debug("The SAP = " + sap);
        addServiceObjectMap(ISIPCapability.class, new UC23V2R3C10SIPCapabilityImpl(
                this.protocolAdapter));
        addServiceObjectMap(IAccountCapability.class, new UC23V2R3C10AccountCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IConfCapability.class, new UC23V2R3C10ConfCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IDepartmentCapability.class, new UC23V2R3C10DepartmentCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IAttendeeCapability.class, new UC23V2R3C10AttendeeCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IRoleCapability.class, new UC23V2R3C10RoleCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IUserLevelCapability.class, new UC23V2R3C10UserLevelCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(ICTCCapability.class, new UC23V2R3C10CTCCapabilityImpl(
            this.protocolAdapter));
        addServiceObjectMap(IPhoneCapability.class, new UC23V2R3C10PhoneCapabilityImpl(
            this.protocolAdapter));
    }

    @Override
    public IDeviceConnection createConnection(String appId, String sap,
            String user, String pwd)
    {
        return new UC23V2R3C10BMUConnection(protocolAdapter, this, user, pwd);
    }
    
    @Override
    public void prepareAuthInfo(String user, String pwd)
    {
        prepareDevAuthInfo(user, pwd);
    }
    
    protected void prepareDevAuthInfo(String user, String pwd)
    {
        AccountInfo acctInfo = authorizePolicy.getDeviceAccountInfo(user, pwd);
        MessageContext mc = ThreadLocalHolder.get();
        mc.getEntities().put(ESDKConstant.DEVICE_USER_ID, acctInfo.getUserId());
        mc.getEntities().put(ESDKConstant.DEVICE_PLAIN_PWD, acctInfo.getPassword());
    }

    @Override
    public String getConnIdFromContext()
    {
        String id = "";
        MessageContext mc = ThreadLocalHolder.get();
        if (null != mc)
        {
            AccountInfo acctInfo = (AccountInfo) mc.getEntities().get(ESDKConstant.ACCT_INFO_ESDK);
            if (null != acctInfo)
            {
                AccountInfo devAcctInfo = authorizePolicy.getDeviceAccountInfo(acctInfo.getUserId(), acctInfo.getPassword());
                id = devAcctInfo.getUserId();// + acctInfo.getPassword();
            }
        }
        return id;
    }

    @Override
    public Boolean releaseConns()
    {
        for (String key : id2Connection.keySet())
        {
            IDeviceConnection conn = id2Connection.get(key);
            deviceManager.releaseConn(conn);
        }
        return true;
    }
    
}
