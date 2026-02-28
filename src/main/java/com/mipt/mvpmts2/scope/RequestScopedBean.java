package com.mipt.mvpmts2.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequestScope
public class RequestScopedBean {

  private static final Logger logger = LoggerFactory.getLogger(RequestScopedBean.class);

  private final String requestId;
  private final LocalDateTime requestStartTime;

  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString();
    this.requestStartTime = LocalDateTime.now();
    logger.info("RequestScopedBean создан для запроса: {}", requestId);
  }

  public String getRequestId() {
    return requestId;
  }

  public LocalDateTime getRequestStartTime() {
    return requestStartTime;
  }

  public long getRequestDuration() {
    return java.time.Duration.between(requestStartTime, LocalDateTime.now()).toMillis();
  }

  @Override
  public String toString() {
    return "RequestScopedBean{" +
        "requestId='" + requestId + '\'' +
        ", requestStartTime=" + requestStartTime +
        ", duration=" + getRequestDuration() + "ms" +
        '}';
  }
}