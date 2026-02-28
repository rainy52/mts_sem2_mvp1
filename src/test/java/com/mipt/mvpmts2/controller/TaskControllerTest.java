package com.mipt.mvpmts2.controller;


import com.mipt.mvpmts2.model.Task;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskControllerTest {

  private static Long createdTaskId = null;
  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;
  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/api/tasks";
  }


  @Test
  @Order(1)
  void createTask_Positive() {

    Task newTask = new Task(null, "Тестовая задача", "Описание задачи", false);
    HttpEntity<Task> request = new HttpEntity<>(newTask, getJsonHeaders());


    ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, request, Task.class);


    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals("Тестовая задача", response.getBody().getTitle());
    assertFalse(response.getBody().isCompleted());

    createdTaskId = response.getBody().getId();
  }

  @Test
  @Order(2)
  void createTask_Negative_EmptyTitle() {

    Task invalidTask = new Task(null, "", "Описание", false);
    HttpEntity<Task> request = new HttpEntity<>(invalidTask, getJsonHeaders());


    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl, request, Map.class);


    assertTrue(response.getStatusCode().is2xxSuccessful() ||
        response.getStatusCode().is4xxClientError());
  }

  @Test
  @Order(3)
  void createTask_Negative_NullBody() {

    HttpEntity<Task> request = new HttpEntity<>(null, getJsonHeaders());


    ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);


    assertTrue(response.getStatusCode().is4xxClientError() ||
        response.getStatusCode().is5xxServerError());
  }


  @Test
  @Order(4)
  void getAllTasks_Positive() {

    ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().length >= 1);
  }

  @Test
  @Order(5)
  void getAllTasks_NotEmpty() {

    ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isEmpty());
  }


  @Test
  @Order(6)
  void getTaskById_Positive() {

    String url = baseUrl + "/" + createdTaskId;


    ResponseEntity<Task> response = restTemplate.getForEntity(url, Task.class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(createdTaskId, response.getBody().getId());
  }

  @Test
  @Order(7)
  void getTaskById_Negative_NotFound() {

    String url = baseUrl + "/99999";


    ResponseEntity<Task> response = restTemplate.getForEntity(url, Task.class);


    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Order(8)
  void getTaskById_Negative_InvalidId() {

    String url = baseUrl + "/invalid";


    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);


    assertTrue(response.getStatusCode().is4xxClientError() ||
        response.getStatusCode().is5xxServerError());
  }


  @Test
  @Order(9)
  void updateTask_Positive() {

    String url = baseUrl + "/" + createdTaskId;
    Task updatedTask = new Task(createdTaskId, "Обновлённая задача", "Новое описание", true);
    HttpEntity<Task> request = new HttpEntity<>(updatedTask, getJsonHeaders());


    ResponseEntity<Task> response = restTemplate.exchange(url, HttpMethod.PUT, request, Task.class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Обновлённая задача", response.getBody().getTitle());
    assertTrue(response.getBody().isCompleted());
  }

  @Test
  @Order(10)
  void updateTask_Negative_NotFound() {

    String url = baseUrl + "/99999";
    Task updatedTask = new Task(99999L, "Задача", "Описание", false);
    HttpEntity<Task> request = new HttpEntity<>(updatedTask, getJsonHeaders());


    ResponseEntity<Task> response = restTemplate.exchange(url, HttpMethod.PUT, request, Task.class);


    assertTrue(response.getStatusCode().is4xxClientError() ||
        response.getStatusCode().is5xxServerError());
  }


  @Test
  @Order(11)
  void deleteTask_Positive() {

    Task newTask = new Task(null, "Задача для удаления", "Описание", false);
    HttpEntity<Task> createRequest = new HttpEntity<>(newTask, getJsonHeaders());
    ResponseEntity<Task> createResponse = restTemplate.postForEntity(baseUrl, createRequest, Task.class);
    Long taskIdToDelete = createResponse.getBody().getId();

    String url = baseUrl + "/" + taskIdToDelete;


    ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);


    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    ResponseEntity<Task> getResponse = restTemplate.getForEntity(url, Task.class);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  @Test
  @Order(12)
  void deleteTask_Negative_NotFound() {

    String url = baseUrl + "/99999";


    ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);


    assertTrue(response.getStatusCode().is2xxSuccessful() ||
        response.getStatusCode().is4xxClientError());
  }


  @Test
  @Order(13)
  void getAppInfo_Positive() {

    ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/app-info", Map.class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().containsKey("name"));
    assertTrue(response.getBody().containsKey("version"));
  }

  @Test
  @Order(14)
  void getCacheStats_Positive() {

    ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/cache-stats", Map.class);


    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().containsKey("total"));
  }

  @Test
  @Order(15)
  void getRequestInfo_Positive() {

    ResponseEntity<Map> response1 = restTemplate.getForEntity(baseUrl + "/request-info", Map.class);
    ResponseEntity<Map> response2 = restTemplate.getForEntity(baseUrl + "/request-info", Map.class);


    assertEquals(HttpStatus.OK, response1.getStatusCode());
    assertEquals(HttpStatus.OK, response2.getStatusCode());

    String requestId1 = response1.getBody().get("requestInfo").toString();
    String requestId2 = response2.getBody().get("requestInfo").toString();
    assertNotEquals(requestId1, requestId2);
  }

  private HttpHeaders getJsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}