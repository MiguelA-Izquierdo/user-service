package com.app.userService._shared.application.service;

import java.util.HashMap;
import java.util.Map;

public interface RequestContext {
  String getClientIp();
  void setClientIp(String clientIp);

  String getUserAgent();
  void setUserAgent(String userAgent);

  String getRequestUrl();
  void setRequestUrl(String requestUrl);

  default Map<String, String> getMetaData() {
    Map<String, String> metaData = new HashMap<>();
    metaData.put("clientIp", getClientIp());
    metaData.put("requestUrl", getRequestUrl());
    metaData.put("userAgent", getUserAgent());
    return metaData;
  }
}
