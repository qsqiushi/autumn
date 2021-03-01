package com.autumn.bo;

import lombok.Data;

import java.util.HashMap;

/**
 * @Description: 调用http短信接口参数
 *
 * @author: qiushi
 * @date: 2018年6月7日 下午8:43:40
 */
@Data
public class HttpParams {
  /** http 接口地址 */
  private String url;
  /**
   * 编码，如果为空默认utf-8 example:utf-8
   *
   * <p>在以下代码中使用：
   *
   * <p>
   *
   * <p>
   *
   * <p>// 设置参数 httpPost.setEntity(new StringEntity(msgHttpParams.getContent(),
   * ContentType.create(msgHttpParams.getMimeType(), msgHttpParams.getCharset()))); // 设置参数
   * UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, msgHttpParams.getCharset()); //
   * 读取接口内容 responseString = EntityUtils.toString(closeableHttpResponse.getEntity(),
   * msgHttpParams.getCharset());
   */
  private String charset;
  /** http headers example:key:parameterTypevalue:XML */
  private HashMap<String, String> headers;
  /** ContentType example:text/xml */
  private String mimeType;
  /**
   * post 参数
   *
   * <p>处理方式如下 List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>(); for
   * (Map.Entry<String, String> entry : msgHttpParams.getParams().entrySet()) { list.add(new
   * BasicNameValuePair(entry.getKey(), entry.getValue())); // 请求参数 } UrlEncodedFormEntity entity =
   * new UrlEncodedFormEntity(list, msgHttpParams.getCharset()); // 设置post求情参数
   * httpPost.setEntity(entity);
   */
  private HashMap<String, String> params;
  /**
   * post参数
   *
   * <p>处理方式如下： httpPost.setEntity(new StringEntity(msgHttpParams.getContent(),
   * ContentType.create(msgHttpParams.getMimeType(), msgHttpParams.getCharset())));
   */
  private String content;

  // 设置超时时间
  // 设置请求和传输超时时间

  /** 传输时间 毫秒 */
  private Integer socketTimeout;

  /** 连接时间 毫秒 */
  private Integer connectTimeout;

  /** 从连接池中获取连接的超时时间 */
  private Integer connectionRequestTimeout;
}
