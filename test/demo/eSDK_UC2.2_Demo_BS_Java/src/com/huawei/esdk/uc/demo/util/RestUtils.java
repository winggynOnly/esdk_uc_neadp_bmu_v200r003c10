package com.huawei.esdk.uc.demo.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.huawei.esdk.uc.demo.constant.UCConstant;
import com.huawei.esdk.uc.demo.model.commu.RestRequestMessage;

public class RestUtils
{
    private static final Logger LOGGER = Logger.getLogger(RestUtils.class);
    
    private static final RestUtils REST_UTIL = new RestUtils();
    
    private static final String REST_URL_MAPPING = PropertiesUtils.getValue("rest.url.mapping");
    
    private static final String REST_URL = PropertiesUtils.getValue("rest.url");
    
    private static final Gson GSON = new Gson();
    
    private static String scheme;
    
    private static String hostname;
    
    private static int port;
    
    private static String userName;
    
    private static String password;
    
    private DefaultHttpClient httpClient;
    
    private BasicHttpContext localContext;
    
    private HttpHost target;
    
    private int serverNounceCount;
    
    static
    {
        scheme = REST_URL.substring(0, REST_URL.indexOf("://"));
        port = Integer.parseInt(REST_URL.substring(REST_URL.lastIndexOf(":") + 1));
        hostname = REST_URL.substring(REST_URL.indexOf("://") + 3, REST_URL.lastIndexOf(":"));
        
        userName = PropertiesUtils.getValue("username");
        password = PropertiesUtils.getValue("password");
    }
    
    private RestUtils()
    {
        
    }
    
    public synchronized static RestUtils getInstance()
    {
        return REST_UTIL;
    }
    
    public String sendMessage(RestRequestMessage message, String resourcePath)
        throws ClientProtocolException, URISyntaxException, IOException
    {
        adapterScheme();
        
        buildBasicHttpContext();
        
        HttpRequestBase request = buildRequestMessage(message, resourcePath);
        
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(target.getHostName(), target.getPort()),
            new UsernamePasswordCredentials(userName, password));
        
        HttpResponse response = null;
        
        try
        {
            response = httpClient.execute(target, request, localContext);
            
            if (response != null)
            {
                if (200 == response.getStatusLine().getStatusCode())
                {
                    HttpEntity entity = response.getEntity();
                    String responsePayload = EntityUtils.toString(entity);
                    
                    return responsePayload;
                }
                else if (401 == response.getStatusLine().getStatusCode())
                {
                    return "401";
                }
            }
            
            return null;
            
        }
        catch (Exception e)
        {
            LOGGER.error("httpclient error", e);
            return null;
        }
        finally
        {
            if (null != request)
            {
                request.releaseConnection();
            }
        }
        
    }
    
    private void adapterScheme()
    {
        ClientConnectionManager conMgr = new PoolingClientConnectionManager();
        httpClient = new DefaultHttpClient(conMgr);
        target = new HttpHost(hostname, port, scheme);
        if (UCConstant.HTTPS_SCHEME.equalsIgnoreCase(scheme))
        {
            try
            {
                SSLContext ctx = SSLContext.getInstance(UCConstant.SSL_SECURE_SOCKET_PROTOCOL);
                X509TrustManager tm = new X509TrustManager()
                {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers()
                    {
                        return new java.security.cert.X509Certificate[] {};
                    }
                    
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException
                    {
                    }
                    
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException
                    {
                    }
                    
                };
                ctx.init(null, new TrustManager[] {tm}, null);
                SSLSocketFactory sslSocketFactory =
                    new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry registry = conMgr.getSchemeRegistry();
                registry.register(new Scheme(scheme, port, sslSocketFactory));
            }
            catch (Exception ex)
            {
                LOGGER.error("https error", ex);
            }
        }
    }
    
    private void buildBasicHttpContext()
    {
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestScheme = new DigestScheme();
        digestScheme.overrideParamter("nc", String.valueOf(serverNounceCount++));
        digestScheme.overrideParamter("cnonce", UUID.randomUUID().toString().replaceAll("-", ""));
        digestScheme.overrideParamter("qop", "auth");
        authCache.put(target, digestScheme);
        
        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }
    
    private HttpRequestBase buildRequestMessage(RestRequestMessage message, String reourecePath)
        throws URISyntaxException, UnsupportedEncodingException
    {
        //HttpRequestBase,用作一些类的基类，这些类使 ASP.NET 可以读取客户端在 Web 请求过程中发送的 HTTP 值。
        HttpRequestBase request;
        
        if (UCConstant.HTTP_METHOD_GET.equalsIgnoreCase(message.getHttpMethod()))
        {
            HttpGet httpGet = new HttpGet(getServerURL(reourecePath));
            setParameters(httpGet, message.getParameters());
            request = httpGet;
        }
        else if (UCConstant.HTTP_METHOD_POST.equalsIgnoreCase(message.getHttpMethod()))
        {
            HttpPost httpPost = new HttpPost(getServerURL(reourecePath));
            httpPost.setEntity(new StringEntity(GSON.toJson(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPost;
        }
        else if (UCConstant.HTTP_METHOD_PUT.equalsIgnoreCase(message.getHttpMethod()))
        {
            HttpPut httpPut = new HttpPut(getServerURL(reourecePath));
            httpPut.setEntity(new StringEntity(GSON.toJson(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPut;
        }
        else if (UCConstant.HTTP_METHOD_DELETE.equalsIgnoreCase(message.getHttpMethod()))
        {
            HttpDelete httpDelete = new HttpDelete(getServerURL(reourecePath));
            setParameters(httpDelete, message.getParameters());
            request = httpDelete;
        }
        else
        {
            String msg = message.getHttpMethod() + " is not a valid HTTP method";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        
        // 设置编码格式
        request.addHeader("content-type", "application/json;charset=UTF-8");
        
        return request;
    }
    
    private void setParameters(HttpRequestBase httpRequest, Map<String, String> parameters)
        throws URISyntaxException
    {
        if (!parameters.isEmpty())
        {
            URIBuilder uriBuilder = new URIBuilder(httpRequest.getURI());
            
            for (Map.Entry<String, String> entry : parameters.entrySet())
            {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            
            httpRequest.setURI(uriBuilder.build());
        }
    }
    
    private String getServerURL(String resourcePath)
    {
        return REST_URL_MAPPING + resourcePath;
    }
}
