package com.huawei.esdk.uc.demo.model;

public class RestResponseKey
{
    private RestResponse header;
    
    private String publicKey;
    
    public RestResponse getHeader()
    {
        return header;
    }
    
    public void setHeader(RestResponse header)
    {
        this.header = header;
    }
    
    public String getPublicKey()
    {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey)
    {
        this.publicKey = publicKey;
    }
}
