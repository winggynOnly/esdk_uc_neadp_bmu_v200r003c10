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
import com.huawei.esdk.uc.demo.model.CallBean;
import com.huawei.esdk.uc.demo.model.RestResponse;
import com.huawei.esdk.uc.demo.model.commu.RestRequestMessage;
import com.huawei.esdk.uc.demo.util.PropertiesUtils;
import com.huawei.esdk.uc.demo.util.RestUtils;
import com.huawei.esdk.uc.demo.util.StringUtils;

/**
 * CTD呼叫处理类
 * 用于发起CTD呼叫
 * 
 * @author  cWX191990
 * @see  [无]
 * @since  [eSDK UC V100R003C30]
 */
public class CTDServlet extends HttpServlet
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 5053679104100767681L;
    
    /**
     * log日志对象
     */
    private static final Logger LOGGER = Logger.getLogger(CTDServlet.class);
    
    /**
     * Gson，用于进行对象和json之间的转换
     */
    private static final Gson GSON = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // 获取页面入参
        String account = request.getParameter("account");
        String caller = request.getParameter("caller");
        String callee = request.getParameter("callee");
        
        // 拼装请求bean
        CallBean callBean = new CallBean();
        callBean.setAccount(account);
        callBean.setCaller(caller);
        callBean.setCallee(callee);
        
        RestResponse result = new RestResponse();
        RestRequestMessage restRequestMessage = new RestRequestMessage();
        
        // 设置请求类型为POST
        restRequestMessage.setHttpMethod(UCConstant.HTTP_METHOD_POST);
        restRequestMessage.setPayload(callBean);
        
        try
        {
            // 发送rest消息，调用发起CTD呼叫接口
            String responsePayload =
                RestUtils.getInstance().sendMessage(restRequestMessage, PropertiesUtils.getValue("ctd.path"));
            if (!StringUtils.isEmpty(responsePayload))
            {
                // 返回401为鉴权失败，检查rest_config.properties中的用户密码是否配置正确
                if ("401".equals(responsePayload))
                {
                    result.setResultCode(401L);
                }
                else
                {
                    // 把JSON结果转换成JavaBean
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
            LOGGER.error("make CTD call error" + e);
        }
        catch (Exception e)
        {
            LOGGER.error("make CTD call error" + e);
        }
        
        // 输出流
        PrintWriter pw = response.getWriter();
        pw.print(GSON.toJson(result));
        pw.close();
    }
}
