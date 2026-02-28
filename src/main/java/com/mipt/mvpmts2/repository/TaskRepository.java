package com.mipt.mvpmts2.repository;

import com.mipt.mvpmts2.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  Task save(Task task);

  Optional<Task> findById(Long id);

  List<Task> findAll();

  void deleteById(Long id);

  Task update(Task task);
}
