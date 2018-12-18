package com.mawujun.http;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mawujun.utils.string.StringUtils;

public class HttpClientUtils {
	private static Logger logger=LoggerFactory.getLogger(HttpClientUtils.class);
	
	private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";
    // 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }

    public static String doGetSSL(String url, Map<String, String> params) {
        return doGetSSL(url, params, CHARSET);
    }

    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, CHARSET);
    }
    /**
     * 
     * @param url
     * @param param json的内容
     * @return
     */
    public static String doPostJson(String url,String param){
    	return doPostJson( url,null,  param);
    }
    /**
     * 
     * @param url
     * @param headers 有些请求要带上自定义的头部信息
     * @param param xml的内容
     * @return
     */
    public static String doPostJson(String url,Map<String, String> headers, String param){
        return doPostBody(url,headers,ContentType.APPLICATION_JSON,param);
    }
    
    /**
     * 
     * @param url
     * @param param xml的内容
     * @return
     */
    public static String doPostXml(String url,String param){
    	return doPostXml( url,null,  param);
    }
    /**
     * 
     * @param url
     * @param headers 有些请求要带上自定义的头部信息
     * @param param json的内容
     * @return
     */
    public static String doPostXml(String url,Map<String, String> headers, String param){
        return doPostBody(url,headers,ContentType.APPLICATION_XML,param);
    }
    
    public static String doPostBody(String url,Map<String, String> headers,ContentType contentType, String param){
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        //json方式

        StringEntity entity = new StringEntity(param,contentType);//解决中文乱码问题,自带utf-8

        //entity.setContentType("application/json; charset=UTF-8");
        //httpPost.setHeader("Accept", "application/json");

        httpPost.setEntity(entity);
        if (headers!=null){

            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpPost.setHeader(e.getKey(), e.getValue());
            }
        }
        HttpResponse resp = null;
        try {
            resp = client.execute(httpPost);
            if(resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he,CHARSET);
            }
        } catch (IOException e) {
            logger.error("请求异常:"+url,e);
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("请求异常:"+url,e);
            }
        }
        return respContent;
    }
    
    /**
     * 
     * @param url
     * @param fileName 文件参数的名字
     * @param file 文件内容
     * @param paramName 其他参数
     * @param paramContent
     * @return
     */
    public static String doPostFile(String url,String fileName,File file,Map<String,Object> params){
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
        
        FileBody bin = new FileBody(file);
        FormBodyPart formBodyPart=FormBodyPartBuilder.create(fileName,bin)
        		.build();
        
        HttpEntity entity = MultipartEntityBuilder.create()
        		.addPart(formBodyPart)
        		.build();
        
//        //FileBody bin = new FileBody(file);
//        StringBody comment = new StringBody(paramContent, ContentType.TEXT_PLAIN);
//
//        HttpEntity entity = MultipartEntityBuilder.create()
//        		.addBinaryBody(fileName, file)
//                //.addPart("bin", bin)
//                .addPart(paramName, comment)
//                .addPart(bodyPart)
//                .build();

        httpPost.setEntity(entity);
//        if (headers!=null){
//
//            for (Map.Entry<String, String> e : headers.entrySet()) {
//                httpPost.setHeader(e.getKey(), e.getValue());
//            }
//        }
        HttpResponse resp = null;
        try {
            resp = client.execute(httpPost);
            if(resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                respContent = EntityUtils.toString(he,CHARSET);
            }
        } catch (IOException e) {
            logger.error("请求异常:"+url,e);
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("请求异常:"+url,e);
            }
        }
        return respContent;
    }


    /**
     * HTTP Get 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                // 将请求参数和url进行拼接
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset 编码格式
     * @return 页面内容
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, String charset) 
            throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (response != null)
                response.close();
        }
        return null;
    }

    /**
     * HTTPS Get 获取内容
     * @param url 请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset  编码格式
     * @return 页面内容
     */
    public static String doGetSSL(String url, Map<String, String> params, String charset) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);

            // https  注意这里获取https内容，使用了忽略证书的方式，当然还有其他的方式来获取https内容
            CloseableHttpClient httpsClient = HttpClientUtils.createSSLClientDefault();
            CloseableHttpResponse response = httpsClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里创建了忽略整数验证的CloseableHttpClient对象
     * @return
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

}
