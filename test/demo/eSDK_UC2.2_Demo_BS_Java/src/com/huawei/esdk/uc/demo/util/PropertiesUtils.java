package com.huawei.esdk.uc.demo.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class PropertiesUtils
{
    private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class.getName());
    
    private static Properties properties = null;
    
    static
    {
        properties = loadProperty();
    }
    
    private static Properties loadProperty()
    {
        Properties p = new Properties();
        loadProp("rest_config.properties", p);
        
        return p;
    }
    
    private static void loadProp(String conf, Properties p)
    {
        InputStream is = null;
        try
        {
            is = getInputStream(conf);
            
            if (null != is)
            {
                p.load(is);
            }
        }
        catch (IOException e)
        {
            LOGGER.log(java.util.logging.Level.WARNING, "Exception happened in loadProp() " + conf, e);
        }
        finally
        {
            if (null != is)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    LOGGER.log(java.util.logging.Level.WARNING, "Exception happened in loadProperty()" + conf, e);
                }
            }
        }
    }
    
    public static String getValue(String key)
    {
        String value = properties.getProperty(key);
        
        return null == value ? "" : value;
    }
    
    private static InputStream getInputStream(String path)
        throws IOException
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (null == is)
        {
            throw new FileNotFoundException(path + " cannot be opened because it does not exist");
        }
        return is;
    }
}