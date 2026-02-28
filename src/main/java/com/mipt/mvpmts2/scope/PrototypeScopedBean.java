package com.mipt.mvpmts2.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeScopedBean {

  private static final Logger logger = LoggerFactory.getLogger(PrototypeScopedBean.class);

  private final String instanceId;
  private final long createdAt;

  public PrototypeScopedBean() {
    this.instanceId = UUID.randomUUID().toString();
    this.createdAt = System.currentTimeMillis();
    logger.info("PrototypeScopedBean создан: {}", instanceId);
  }

  public String generateTaskId() {
    String taskId = "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    logger.info("Сгенерирован ID задачи: {} (экземпляр: {})", taskId, instanceId);
    return taskId;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "PrototypeScopedBean{" +
        "instanceId='" + instanceId + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}