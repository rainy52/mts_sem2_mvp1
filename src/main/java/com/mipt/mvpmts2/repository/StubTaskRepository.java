package com.mipt.mvpmts2.repository;


import com.mipt.mvpmts2.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StubTaskRepository implements TaskRepository {

  private final List<Task> stubTasks = Arrays.asList(
      new Task(1L, "Stub Task 1", "something", false),
      new Task(2L, "Stub Task 2", "literally something", true),
      new Task(3L, "Stub Task 3", "wow its something", false)
  );

  @Override
  public Task save(Task task) {
    System.out.println("save");
    return task;
  }

  @Override
  public Optional<Task> findById(Long id) {
    System.out.println("findById");
    return stubTasks.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
  }

  @Override
  public List<Task> findAll() {
    System.out.println("findAll");
    return new ArrayList<>(stubTasks);
  }

  @Override
  public void deleteById(Long id) {
    System.out.println("deleteById for " + id);
  }

  @Override
  public Task update(Task task) {
    System.out.println("update");
    return task;
  }
}