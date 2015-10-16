package com.huawei.esdk.uc.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.huawei.esdk.uc.demo.constant.UCConstant;

import com.huawei.esdk.uc.demo.model.RestResponse;
import com.huawei.esdk.uc.demo.model.commu.RestRequestMessage;
import com.huawei.esdk.uc.demo.util.PropertiesUtils;
import com.huawei.esdk.uc.demo.util.RestUtils;
import com.huawei.esdk.uc.demo.util.StringUtils;

/**
 * 帐号管理处理类
 * 用于查询账号
 * 
 * @author wWX233376
 * @see  [无]
 * @since  [eSDK UC V100R003C30]
 */
public class QueryAccountServlet extends HttpServlet
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 5053679104100767688L;
    
    /**
     * log日志对象
     */
    private static final Logger LOGGER = Logger.getLogger(AddAccountServlet.class);
    
    /**
     * Gson，用于进行对象和json之间的转换
     */
    private static final Gson GSON = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // 获取页面入参
        String userId = request.getParameter("userId");
        String exactSearch = request.getParameter("exactSearch");
        String condition = request.getParameter("condition");
        String pageCount = request.getParameter("pageCount");
        String pageNum = request.getParameter("pageNum");
        
        //拼装请求Map		
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("userId", userId);
        parameters.put("exactSearch", exactSearch);
        parameters.put("condition", condition);
        parameters.put("pageCount", pageCount);
        parameters.put("pageNum", pageNum);
        
        RestResponse result = new RestResponse();
        RestRequestMessage restRequestMessage = new RestRequestMessage();
        
        //设置请求类型为GET
        restRequestMessage.setHttpMethod(UCConstant.HTTP_METHOD_GET);
        restRequestMessage.setParameters(parameters);
        
        try
        {
            //发送rest消息，调用查询帐号接口
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
            LOGGER.error("query account error" + e);
        }
        catch (Exception e)
        {
            LOGGER.error("query account error" + e);
        }
        
        // 输出流
        PrintWriter pw = response.getWriter();
        pw.print(GSON.toJson(result));
        pw.close();
    }
    
}
