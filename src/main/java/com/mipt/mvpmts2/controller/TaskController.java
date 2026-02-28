package com.mipt.mvpmts2.controller;

import com.mipt.mvpmts2.model.Task;
import com.mipt.mvpmts2.service.TaskService;
import com.mipt.mvpmts2.service.TaskStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final TaskStatisticsService statisticsService;

  public TaskController(TaskService taskService,
                        TaskStatisticsService statisticsService) {
    this.taskService = taskService;
    this.statisticsService = statisticsService;
  }


  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    Task saved = taskService.createTask(task);
    return ResponseEntity.created(URI.create("/api/tasks/" + saved.getId()))
        .body(saved);
  }


  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    return ResponseEntity.ok(taskService.getAllTasks());
  }


  @GetMapping("/{id}")
  public ResponseEntity<Task> getTask(@PathVariable Long id) {
    return taskService.getTask(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }


  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                         @RequestBody Task task) {
    task.setId(id);
    return ResponseEntity.ok(taskService.updateTask(task));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }


  @GetMapping("/statistics")
  public ResponseEntity<TaskStatisticsService.StatisticsResult> getStatistics() {
    return ResponseEntity.ok(statisticsService.compareRepositories());
  }


  @GetMapping("/cache-stats")
  public ResponseEntity<TaskService.CacheStatistics> getCacheStatistics() {
    return ResponseEntity.ok(taskService.getCacheStatistics());
  }


  @GetMapping("/app-info")
  public ResponseEntity<TaskService.AppInfo> getAppInfo() {
    return ResponseEntity.ok(taskService.getAppInfo());
  }


  @GetMapping("/request-info")
  public ResponseEntity<Map<String, Object>> getRequestInfo() {
    return ResponseEntity.ok(Map.of(
        "requestInfo", taskService.getRequestInfo(),
        "timestamp", System.currentTimeMillis()
    ));
  }


  @GetMapping("/prototype-info")
  public ResponseEntity<Map<String, Object>> getPrototypeInfo() {
    return ResponseEntity.ok(Map.of(
        "prototypeInfo", taskService.getPrototypeInfo(),
        "timestamp", System.currentTimeMillis()
    ));
  }
}