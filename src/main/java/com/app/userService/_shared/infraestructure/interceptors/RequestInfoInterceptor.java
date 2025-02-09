package com.app.userService._shared.infraestructure.interceptors;

import com.app.userService._shared.application.service.RequestContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RequestInfoInterceptor implements HandlerInterceptor {
  private final RequestContext requestContext;

  public RequestInfoInterceptor(RequestContext requestContext) {
    this.requestContext = requestContext;
  }


  @Override
  public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
    requestContext.setClientIp(getClientIpFromRequest(request));
    requestContext.setUserAgent(request.getHeader("User-Agent"));
    requestContext.setRequestUrl(request.getRequestURL().toString());

    return true;
  }

  private String getClientIpFromRequest(HttpServletRequest request) {
    String clientIp = request.getHeader("X-Forwarded-For");

    if (clientIp == null || clientIp.isEmpty()) {
      clientIp = request.getRemoteAddr();
    }

    if (clientIp != null && clientIp.contains(",")) {
      clientIp = clientIp.split(",")[0];
    }

    return clientIp;
  }
}
