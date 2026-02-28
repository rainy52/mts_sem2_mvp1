package com.mipt.mvpmts2.service;


import com.mipt.mvpmts2.model.Task;
import com.mipt.mvpmts2.repository.TaskRepository;
import com.mipt.mvpmts2.scope.PrototypeScopedBean;
import com.mipt.mvpmts2.scope.RequestScopedBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private RequestScopedBean requestScopedBean;

  @Mock
  private PrototypeScopedBean prototypeScopedBean;

  @InjectMocks
  private TaskService taskService;

  @BeforeEach
  void setUp() {
    lenient().when(taskRepository.findAll()).thenReturn(Collections.emptyList());

    lenient().when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      if (task.getId() == null) {
        task.setId(1L);
      }
      return task;
    });

    lenient().when(prototypeScopedBean.generateTaskId()).thenReturn("TASK-TEST-123");
    lenient().when(requestScopedBean.getRequestId()).thenReturn("req-123");

    taskService.init();


    reset(taskRepository, prototypeScopedBean, requestScopedBean);
    taskService.clearCacheForTesting();

    lenient().when(taskRepository.findAll()).thenReturn(Collections.emptyList());
    lenient().when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
      Task task = invocation.getArgument(0);
      if (task.getId() == null) {
        task.setId(1L);
      }
      return task;
    });
    lenient().when(prototypeScopedBean.generateTaskId()).thenReturn("TASK-TEST-123");
    lenient().when(requestScopedBean.getRequestId()).thenReturn("req-123");
  }

  @Test
  void getTask_Positive_Found() {

    Task task = new Task(1L, "Задача", "Описание", false);
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));


    Optional<Task> result = taskService.getTask(1L);


    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
    verify(taskRepository, times(1)).findById(1L);
  }

  @Test
  void getTask_Negative_NotFound() {

    when(taskRepository.findById(999L)).thenReturn(Optional.empty());


    Optional<Task> result = taskService.getTask(999L);


    assertFalse(result.isPresent());
    verify(taskRepository, times(1)).findById(999L);
  }

  @Test
  void getAllTasks_Positive() {

    List<Task> tasks = Arrays.asList(
        new Task(1L, "Задача 1", "Описание 1", false),
        new Task(2L, "Задача 2", "Описание 2", true)
    );
    when(taskRepository.findAll()).thenReturn(tasks);


    List<Task> result = taskService.getAllTasks();


    assertNotNull(result);
    assertEquals(2, result.size());
    verify(taskRepository, times(1)).findAll();
  }

  @Test
  void updateTask_Positive() {

    Task updatedTask = new Task(1L, "Обновлённая", "Описание", true);
    when(taskRepository.update(any(Task.class))).thenReturn(updatedTask);


    Task result = taskService.updateTask(updatedTask);


    assertNotNull(result);
    assertEquals("Обновлённая", result.getTitle());
    assertTrue(result.isCompleted());
    verify(taskRepository, times(1)).update(any(Task.class));
  }

  @Test
  void deleteTask_Positive() {

    doNothing().when(taskRepository).deleteById(1L);


    taskService.deleteTask(1L);


    verify(taskRepository, times(1)).deleteById(1L);
  }

  @Test
  void getCacheStatistics_Positive() {

    TaskService.CacheStatistics stats = taskService.getCacheStatistics();


    assertNotNull(stats);
    assertTrue(stats.total() >= 0);
    assertTrue(stats.completed() >= 0);
    assertTrue(stats.active() >= 0);
  }
}