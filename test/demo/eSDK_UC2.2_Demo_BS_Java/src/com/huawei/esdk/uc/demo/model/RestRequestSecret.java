package com.huawei.esdk.uc.demo.model;

public class RestRequestSecret
{
    private String secretType;
    
    private String secretKey;
    
    private String iv;
    
    public String getSecretType()
    {
        return secretType;
    }
    
    public void setSecretType(String secretType)
    {
        this.secretType = secretType;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public String getIv()
    {
        return iv;
    }
    
    public void setIv(String iv)
    {
        this.iv = iv;
    }
    
}
