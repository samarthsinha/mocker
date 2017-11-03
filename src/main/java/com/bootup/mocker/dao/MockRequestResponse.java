package com.bootup.mocker.dao;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author b0095753 on 11/2/17.
 */
@Document(collection = "mockreqres")
public class MockRequestResponse {

  @Id
  private String id;

  private long delayInSeconds;

  private Boolean soap = Boolean.FALSE ;

  private String soapAction;

  private String requestUri;

  private String requestMethod;

  private int httpResponseCode;

  private String responseContentType;

  private String contentEncoding;

  private String responseBody;

  private Map<String,String> responseHeaders;

  private Long createdTime;

  private Long updatedTime;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRequestUri() {
    return requestUri;
  }

  public long getDelayInSeconds() {
    return delayInSeconds;
  }

  public void setDelayInSeconds(long delayInSeconds) {
    this.delayInSeconds = delayInSeconds;
  }

  public void setRequestUri(String requestUri) {
    this.requestUri = requestUri;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public int getHttpResponseCode() {
    return httpResponseCode;
  }

  public void setHttpResponseCode(int httpResponseCode) {
    this.httpResponseCode = httpResponseCode;
  }

  public String getResponseContentType() {
    return responseContentType;
  }

  public void setResponseContentType(String responseContentType) {
    this.responseContentType = responseContentType;
    if(responseHeaders!=null){
      responseHeaders.put("Content-Type",responseContentType);
    }else{
      responseHeaders = new HashMap<>();
      responseHeaders.put("Content-Type",responseContentType);
    }
  }

  public String getContentEncoding() {
    return contentEncoding;
  }

  public void setContentEncoding(String contentEncoding) {
    this.contentEncoding = contentEncoding;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(String responseBody) {
    this.responseBody = responseBody;
  }

  public Map<String, String> getResponseHeaders() {
    return responseHeaders;
  }

  public void setResponseHeaders(Map<String, String> responseHeaders) {
    this.responseHeaders = responseHeaders;
    if(responseContentType!=null){
      this.responseHeaders.put("Content-Type",this.responseContentType);
    }
  }

  public Boolean getSoap() {
    return soap;
  }

  public void setSoap(Boolean soap) {
    this.soap = soap;
  }

  public String getSoapAction() {
    return soapAction;
  }

  public void setSoapAction(String soapAction) {
    this.soapAction = soapAction;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getUpdatedTime() {
    return updatedTime;
  }

  public void setUpdatedTime(Long updatedTime) {
    this.updatedTime = updatedTime;
  }
}
