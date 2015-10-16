package com.huawei.esdk.uc.demo.util;

import java.io.IOException;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.huawei.esdk.uc.demo.constant.UCConstant;
import com.huawei.esdk.uc.demo.model.RestResponse;
import com.huawei.esdk.uc.demo.model.RestResponseKey;
import com.huawei.esdk.uc.demo.model.RestRequestSecret;
import com.huawei.esdk.uc.demo.model.commu.RestRequestMessage;

/**
 * 密钥管理类
 * 用于获取公钥，以及密钥协商
 * 
 * @author  wWX233376
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class KeyManagerUtils
{
    /**
     * log日志对象
     */
    private static final Logger LOGGER = Logger.getLogger(KeyManagerUtils.class);
    
    /**
     * Gson，用于进行对象和json之间的转换
     */
    private static final Gson GSON = new Gson();
    
    /**
     * 128 bit
     */
    private static final int AES_128_KEY_LEN = 16;
    
    /**
     * BT_KEY,16位长度byte类型的安全随机数
     */
    private static final byte[] BT_KEY = SecureRandom.getSeed(AES_128_KEY_LEN);
    
    /**
     * BT_IV,16位长度byte类型的安全随机数
     */
    private static final byte[] BT_IV = SecureRandom.getSeed(AES_128_KEY_LEN);
    
    public static byte[] getBtKey()
    {
        return BT_KEY;
    }
    
    public static byte[] getBtIv()
    {
        return BT_IV;
    }
    
    /**
     * 获取公钥接口
     * 用于获取公钥
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getPublicKey()
    {
        RestResponseKey result = new RestResponseKey();
        RestRequestMessage restRequestMessage = new RestRequestMessage();
        
        //设置请求类型为GET
        restRequestMessage.setHttpMethod(UCConstant.HTTP_METHOD_GET);
        
        String responsePayload = null;
        try
        {
            //发送rest消息，调用获取公钥接口
            responsePayload =
                RestUtils.getInstance().sendMessage(restRequestMessage, PropertiesUtils.getValue("publicKey.path"));
            
            if (!StringUtils.isEmpty(responsePayload))
            {
                //返回401为鉴权失败，检查rest_config.properties用户密码是否配置正确
                if ("401".equals(responsePayload))
                {
                    result.getHeader().setResultCode(401L);
                }
                else
                {
                    //将JSON结果转化成JavaBean
                    result = GSON.fromJson(responsePayload, RestResponseKey.class);
                }
            }
            else
            {
                // 消息为空,可能是网络存在问题，需要定义个错误码，1默认为操作失败
                result.getHeader().setResultCode(1L);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("get public key error" + e);
        }
        catch (Exception e)
        {
            LOGGER.error("get public key error" + e);
        }
        
        String publicKey = result.getHeader().getResultCode() == 0 ? result.getPublicKey() : null;
        return publicKey;
    }
    
    /**
     * 密钥协商接口
     * 将协商的工作密钥传给eSDK
     * 
     * @see [类、类#方法、类#成员]
     */
    public static void setSecretkey()
    {
        //设置参数
        String secretType = "AES128";
        String secretKeyString = byte2Hex(BT_KEY);
        String ivString = byte2Hex(BT_IV);
        
        //获取公钥
        String publicKey = getPublicKey();
        
        if (publicKey == null)
        {
            LOGGER.error("get public key failed");
            return;
        }
        
        //将公钥设置到RSA2048Utils类里
        RSA2048Utils.setPublicKey(publicKey);
        
        String secretKeyCode = null;
        String ivCode = null;
        try
        {
            //将密码用RSA2048加密，并使用BASE64编码
            secretKeyCode = Base64Utils.encode(RSA2048Utils.encode(secretKeyString.getBytes("UTF-8")));
            ivCode = Base64Utils.encode(RSA2048Utils.encode(ivString.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            LOGGER.error("password encrypt failed" + e);
        }
        
        //拼装请求bean
        RestRequestSecret restRequestSecret = new RestRequestSecret();
        
        restRequestSecret.setSecretType(secretType);
        restRequestSecret.setSecretKey(secretKeyCode);
        restRequestSecret.setIv(ivCode);
        
        RestResponse result = new RestResponse();
        RestRequestMessage restRequestMessage = new RestRequestMessage();
        
        //设置请求类型为POST
        restRequestMessage.setHttpMethod(UCConstant.HTTP_METHOD_POST);
        restRequestMessage.setPayload(restRequestSecret);
        
        try
        {
            //发送rest消息，调用密钥协商接口
            String responsePayload =
                RestUtils.getInstance().sendMessage(restRequestMessage, PropertiesUtils.getValue("secretkey.path"));
            
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
            LOGGER.error("set secret key error" + e);
        }
        catch (Exception e)
        {
            LOGGER.error("set secret key error" + e);
        }
        
    }
    
    public static String byte2Hex(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            
            sb.append(hex);
        }
        
        return sb.toString();
    }
}
