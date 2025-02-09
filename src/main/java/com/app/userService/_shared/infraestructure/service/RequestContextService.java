package com.app.userService._shared.infraestructure.service;

import com.app.userService._shared.application.service.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class RequestContextService implements RequestContext {

  private String clientIp;
  private String userAgent;
  private String requestUrl;

  @Override
  public String getClientIp() {
    return clientIp;
  }

  @Override
  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  @Override
  public String getUserAgent() {
    return userAgent;
  }

  @Override
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  @Override
  public String getRequestUrl() {
    return requestUrl;
  }

  @Override
  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }
}
