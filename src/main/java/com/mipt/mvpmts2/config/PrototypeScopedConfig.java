package com.mipt.mvpmts2.config;

import com.mipt.mvpmts2.scope.PrototypeScopedBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PrototypeScopedConfig {

  @Bean
  @Scope("prototype")
  public PrototypeScopedBean prototypeScopedBean() {
    return new PrototypeScopedBean();
  }
}