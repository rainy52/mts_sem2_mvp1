package com.mipt.mvpmts2.lifecycle;

import com.mipt.mvpmts2.repository.TaskRepository;
import com.mipt.mvpmts2.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

  private static final Logger logger = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService) {
      logger.info("BEFORE initialization: {}", beanName);
    }
    if (bean instanceof TaskRepository) {
      logger.info("BEFORE initialization: {}", beanName);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService) {
      logger.info("AFTER initialization: {}", beanName);
    }
    if (bean instanceof TaskRepository) {
      logger.info("AFTER initialization: {}", beanName);
    }
    return bean;
  }
}