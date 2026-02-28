package com.mipt.mvpmts2.service;

import com.mipt.mvpmts2.model.Task;
import com.mipt.mvpmts2.repository.TaskRepository;
import com.mipt.mvpmts2.scope.PrototypeScopedBean;
import com.mipt.mvpmts2.scope.RequestScopedBean;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

  private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
  private final TaskRepository taskRepository;
  private final RequestScopedBean requestScopedBean;
  private final PrototypeScopedBean prototypeScopedBean;
  private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();
  // ← Инжекция кастомных свойств через @Value
  @Value("${app.name:Unknown Application}")
  private String appName;
  @Value("${app.version:0.0.0}")
  private String appVersion;
  @Value("${app.environment:default}")
  private String environment;
  @Value("${app.debug-mode:false}")
  private boolean debugMode;

  public TaskService(TaskRepository taskRepository,
                     RequestScopedBean requestScopedBean,
                     PrototypeScopedBean prototypeScopedBean) {
    this.taskRepository = taskRepository;
    this.requestScopedBean = requestScopedBean;
    this.prototypeScopedBean = prototypeScopedBean;
    logger.info("TaskService создан. {} v{}", appName, appVersion);
  }

  @PostConstruct
  public void init() {
    logger.info("Инициализация TaskService");
    logger.info("{} v{} | {}",
        appName, appVersion, environment);
    logger.info("Debug: {}", debugMode);

    List<Task> allTasks = taskRepository.findAll();
    for (Task task : allTasks) {
      taskCache.put(task.getId(), task);
    }

    if (taskCache.isEmpty()) {
      logger.info("Репозиторий пуст");
      createTestTasks();
    }

    logger.info("Кэш инициализирован. Всего задач: {}", taskCache.size());
  }

  @PreDestroy
  public void cleanup() {
    logger.info("Очистка ресурсов TaskService");
    logger.info("Статистика: {} задач в кэше", taskCache.size());
    saveStatisticsToFile();
    taskCache.clear();
  }

  private void createTestTasks() {
    Task task1 = taskRepository.save(new Task(null, "Тест1", "Описание1", false));
    Task task2 = taskRepository.save(new Task(null, "Тест2", "Описание2", true));
    taskCache.put(task1.getId(), task1);
    taskCache.put(task2.getId(), task2);
  }

  private void saveStatisticsToFile() {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    String filename = "logs/task_statistics_" + timestamp + ".txt";

    try (FileWriter writer = new FileWriter(filename)) {
      writer.write("TaskService Statistics\n");
      writer.write("Application: " + appName + "\n");
      writer.write("Version: " + appVersion + "\n");
      writer.write("Environment: " + environment + "\n");
      writer.write("Timestamp: " + LocalDateTime.now() + "\n");
      writer.write("Total tasks: " + taskCache.size() + "\n");
    } catch (IOException e) {
      logger.error("Ошибка сохранения: {}", e.getMessage());
    }
  }

  public Task createTask(Task task) {
    String taskId = prototypeScopedBean.generateTaskId();
    logger.info("task create with id: {}", taskId);

    Task saved = taskRepository.save(task);
    taskCache.put(saved.getId(), saved);
    return saved;
  }

  public Optional<Task> getTask(Long id) {
    logger.info("get task: {} (request: {})", id, requestScopedBean.getRequestId());

    if (taskCache.containsKey(id)) {
      return Optional.of(taskCache.get(id));
    }

    Optional<Task> task = taskRepository.findById(id);
    task.ifPresent(t -> taskCache.put(id, t));
    return task;
  }

  public List<Task> getAllTasks() {
    logger.info("get all tasks (Request: {})", requestScopedBean.getRequestId());
    return taskRepository.findAll();
  }

  public void deleteTask(Long id) {
    logger.info("delete task {}", id);
    taskRepository.deleteById(id);
    taskCache.remove(id);
  }

  public Task updateTask(Task task) {
    logger.info("update task {}", task.getId());
    Task updated = taskRepository.update(task);
    taskCache.put(updated.getId(), updated);
    return updated;
  }

  public String getRequestInfo() {
    return requestScopedBean.toString();
  }

  public String getPrototypeInfo() {
    return prototypeScopedBean.toString();
  }

  public AppInfo getAppInfo() {
    return new AppInfo(appName, appVersion, environment, debugMode);
  }

  public CacheStatistics getCacheStatistics() {
    return new CacheStatistics(
        taskCache.size(),
        (int) countCompleted(),
        (int) (taskCache.size() - countCompleted())
    );
  }

  private long countCompleted() {
    return taskCache.values().stream()
        .filter(Task::isCompleted)
        .count();
  }

  public void clearCacheForTesting() {
    taskCache.clear();
  }

  public record AppInfo(String name, String version, String environment, boolean debugMode) {
  }

  public record CacheStatistics(int total, int completed, int active) {
  }
}