package com.mipt.mvpmts2.service;


import com.mipt.mvpmts2.model.Task;
import com.mipt.mvpmts2.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatisticsService {

  private final TaskRepository primaryRepository;


  private final TaskRepository stubRepository;

  public TaskStatisticsService(
      TaskRepository primaryRepository,
      @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
    this.primaryRepository = primaryRepository;
    this.stubRepository = stubRepository;
  }

  public StatisticsResult compareRepositories() {
    List<Task> primaryTasks = primaryRepository.findAll();
    List<Task> stubTasks = stubRepository.findAll();

    return new StatisticsResult(
        "InMemoryTaskRepository",
        "StubTaskRepository",
        primaryTasks.size(),
        stubTasks.size(),
        primaryTasks,
        stubTasks
    );
  }

  public record StatisticsResult(String primaryRepo, String stubRepo, int primaryCount, int stubCount,
                                 List<Task> primaryTasks, List<Task> stubTasks) {
  }
}