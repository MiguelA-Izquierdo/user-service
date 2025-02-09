package com.app.userService._shared.infraestructure.config;

import com.app.userService._shared.infraestructure.interceptors.RequestInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private RequestInfoInterceptor requestInfoInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestInfoInterceptor).addPathPatterns("/**");
  }
}
