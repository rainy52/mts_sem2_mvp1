package com.mipt.mvpmts2.repository;


import com.mipt.mvpmts2.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskRepositoryTest {

  private InMemoryTaskRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryTaskRepository();
  }

  @Test
  void save_Positive_Create() {

    Task task = new Task(null, "Новая задача", "Описание", false);


    Task saved = repository.save(task);


    assertNotNull(saved);
    assertNotNull(saved.getId());
    assertEquals("Новая задача", saved.getTitle());
  }

  @Test
  void save_Positive_Update() {

    Task task = new Task(null, "Задача", "Описание", false);
    Task saved = repository.save(task);
    saved.setTitle("Обновлённая задача");


    Task updated = repository.save(saved);


    assertEquals(saved.getId(), updated.getId());
    assertEquals("Обновлённая задача", updated.getTitle());
  }

  @Test
  void findById_Positive() {

    Task task = new Task(null, "Задача", "Описание", false);
    Task saved = repository.save(task);


    Optional<Task> found = repository.findById(saved.getId());


    assertTrue(found.isPresent());
    assertEquals(saved.getId(), found.get().getId());
  }

  @Test
  void findById_Negative_NotFound() {

    Optional<Task> found = repository.findById(999L);


    assertFalse(found.isPresent());
  }

  @Test
  void findAll_Positive() {

    repository.save(new Task(null, "Задача 1", "Описание 1", false));
    repository.save(new Task(null, "Задача 2", "Описание 2", true));


    List<Task> tasks = repository.findAll();


    assertNotNull(tasks);
    assertEquals(2, tasks.size());
  }

  @Test
  void deleteById_Positive() {

    Task task = new Task(null, "Задача", "Описание", false);
    Task saved = repository.save(task);


    repository.deleteById(saved.getId());


    Optional<Task> found = repository.findById(saved.getId());
    assertFalse(found.isPresent());
  }

  @Test
  void update_Positive() {

    Task task = new Task(null, "Задача", "Описание", false);
    Task saved = repository.save(task);
    saved.setTitle("Обновлённая");
    saved.setCompleted(true);


    Task updated = repository.update(saved);


    assertEquals("Обновлённая", updated.getTitle());
    assertTrue(updated.isCompleted());
  }

  @Test
  void update_Negative_NotFound() {

    Task task = new Task(999L, "Задача", "Описание", false);
    assertThrows(RuntimeException.class, () -> repository.update(task));
  }
}