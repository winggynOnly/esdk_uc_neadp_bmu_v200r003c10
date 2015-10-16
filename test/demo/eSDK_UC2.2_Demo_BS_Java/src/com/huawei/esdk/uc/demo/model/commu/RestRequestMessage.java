package com.huawei.esdk.uc.demo.model.commu;

import java.util.HashMap;
import java.util.Map;

public class RestRequestMessage
{
    /**
     * GET, POST, PUT, DELETE
     */
    private String httpMethod;
    
    /**
     * Query, Delete parameters
     */
    private Map<String, String> parameters;
    
    /**
     * Message payload, 传bean
     */
    private Object payload;
    
    public RestRequestMessage()
    {
        parameters = new HashMap<String, String>();
    }
    
    public String getHttpMethod()
    {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }
    
    public Map<String, String> getParameters()
    {
        return parameters;
    }
    
    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }
    
    public Object getPayload()
    {
        return payload;
    }
    
    public void setPayload(Object payload)
    {
        this.payload = payload;
    }
}
