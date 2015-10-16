package com.huawei.esdk.uc.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.huawei.esdk.uc.demo.constant.UCConstant;
import com.huawei.esdk.uc.demo.model.Account;
import com.huawei.esdk.uc.demo.model.AccountBean;
import com.huawei.esdk.uc.demo.model.RestResponse;
import com.huawei.esdk.uc.demo.model.commu.RestRequestMessage;
import com.huawei.esdk.uc.demo.util.AESCbc128Utils;
import com.huawei.esdk.uc.demo.util.Base64Utils;
import com.huawei.esdk.uc.demo.util.KeyManagerUtils;
import com.huawei.esdk.uc.demo.util.PropertiesUtils;
import com.huawei.esdk.uc.demo.util.RestUtils;
import com.huawei.esdk.uc.demo.util.StringUtils;

/**
 * 帐号管理处理类
 * 用于添加账号
 * 
 * @author wWX233376
 * @see  [无]
 * @since  [eSDK UC V100R003C30]
 */
public class AddAccountServlet extends HttpServlet
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 5053679104100767687L;
    
    /**
     * log日志对象
     */
    private static final Logger LOGGER = Logger.getLogger(AddAccountServlet.class);
    
    /**
     * Gson，用于进行对象和json之间的转换
     */
    private static final Gson GSON = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        addAccount(request, response);
    }
    
    protected void addAccount(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        //获取页面入参
        String userId = request.getParameter("userId");
        
        String accountType = request.getParameter("accountType");
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String sex = request.getParameter("sex");
        String homePhone = request.getParameter("homePhone");
        String cellPhone = request.getParameter("cellPhone");
        String officePhone = request.getParameter("officePhone");
        String shortPhone = request.getParameter("shortPhone");
        String otherPhone = request.getParameter("otherPhone");
        String fax = request.getParameter("fax");
        String email = request.getParameter("email");
        String addr = request.getParameter("addr");
        String title = request.getParameter("title");
        String departmentId = request.getParameter("departmentId");
        String userLevel = request.getParameter("userLevel");
        String roleId = request.getParameter("roleId");
        
        // 密钥协商
        KeyManagerUtils.setSecretkey();
        
        String passwordCode = null;
        try
        {
            //将密码用AES128加密，并使用BASE64编码
            passwordCode =
                Base64Utils.encode(AESCbc128Utils.encode(password.getBytes(),
                    KeyManagerUtils.getBtKey(),
                    KeyManagerUtils.getBtIv()));
        }
        catch (Exception e)
        {
            LOGGER.error("password encrypt failed" + e);
        }
        
        //拼装请求bean
        Account account = new Account();
        
        account.setAccountType(accountType);
        account.setLoginName(loginName);
        account.setPassword(passwordCode);
        account.setName(name);
        account.setSex(sex);
        account.setHomePhone(homePhone);
        account.setCellPhone(cellPhone);
        account.setOfficePhone(officePhone);
        account.setShortPhone(shortPhone);
        account.setOtherPhone(otherPhone);
        account.setFax(fax);
        account.setEmail(email);
        account.setAddr(addr);
        account.setTitle(title);
        account.setDepartmentId(departmentId);
        account.setUserLevel(userLevel);
        account.setRoleId(roleId);
        
        AccountBean addAccountBean = new AccountBean();
        
        addAccountBean.setUserId(userId);
        addAccountBean.setAccount(account);
        
        RestResponse result = new RestResponse();
        RestRequestMessage restRequestMessage = new RestRequestMessage();
        
        //设置请求类型为POST
        restRequestMessage.setHttpMethod(UCConstant.HTTP_METHOD_POST);
        restRequestMessage.setPayload(addAccountBean);
        
        try
        {
            //发送rest消息，调用添加帐号接口
            String responsePayload =
                RestUtils.getInstance().sendMessage(restRequestMessage, PropertiesUtils.getValue("account.path"));
            
            if (!StringUtils.isEmpty(responsePayload))
            {
                //返回401为鉴权失败，检查rest_config.properties用户密码是否配置正确
                if ("401".equals(responsePayload))
                {
                    result.setResultCode(401L);
                }
                else
                {
                    //将JSON结果转化成JavaBean
                    result = GSON.fromJson(responsePayload, RestResponse.class);
                }
            }
            else
            {
                // 消息为空,可能是网络存在问题，需要定义个错误码，1默认为操作失败
                result.setResultCode(1L);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("add account error" + e);
        }
        catch (Exception e)
        {
            LOGGER.error("add account error" + e);
        }
        
        // 输出流
        PrintWriter pw = response.getWriter();
        pw.print(GSON.toJson(result));
        pw.close();
    }
    
}
