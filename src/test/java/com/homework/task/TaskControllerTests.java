package com.homework.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.task.database.services.TokenBlacklistService;
import com.homework.task.database.templates.Task;
import com.homework.task.requests.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskRequest {
	private Task.Status status;
	private String name;
	private String description;

	public TaskRequest() {
	}

	public TaskRequest(Task.Status status, String name, String description) {
		this.status = status;
		this.name = name;
		this.description = description;
	}

	public Task.Status getStatus() {
		return status;
	}

	public void setStatus(Task.Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerTests {

	private static final String BASE_URL = "http://localhost:8080/";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final UserRequest basicUserRequest = new UserRequest("user", "user");


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@MockitoBean
	private TokenBlacklistService tokenBlacklistService;

	private void createTask(TaskRequest task, String token) throws Exception {
		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	private void registerBasicUser() throws Exception {
		mockMvc.perform(post(BASE_URL + "/register")
						.content(objectMapper.writeValueAsString(basicUserRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	private String loginBasicUser() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/login")
						.content(objectMapper.writeValueAsString(basicUserRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		return mvcResult.getResponse().getContentAsString();
	}

	@AfterEach
	void resetAutoIncrement() {
		jdbcTemplate.execute("ALTER TABLE tasks ALTER COLUMN id RESTART WITH 1");
		jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
	}

	@AfterEach
	void resetMocks() {
		Mockito.reset(tokenBlacklistService);
	}

	@Test
	void addTaskWithoutAuthentication() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

	}

	@Test
	void addTask() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	}

	@Test
	void addTaskWithoutStatus() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setName("Task 1");
		task1.setDescription("Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addTaskWithEmptyDescription() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setStatus(Task.Status.ACTIVE);
		task1.setName("Task 1");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addTaskWithEmptyName() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setStatus(Task.Status.ACTIVE);
		task1.setDescription("Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(objectMapper.writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addEmptyTask() throws Exception {
		TaskRequest task1 = new TaskRequest();

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void addTaskWithUnexpectedVariable() throws Exception {

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		mockMvc.perform(post(BASE_URL + "/tasks")
						.content("{\"status\": \"ACTIVE\",\"name\": \"Task 1\",\"description\": \"Task 1 desc\",\"surprise\": \"This is an unexpected field\"}")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void getTaskWithValidId() throws Exception {

		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		mockMvc.perform(get(BASE_URL + "/tasks/1")
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value("Task 1 desc"));

	}

	@Test
	void getTaskWithInvalidId() throws Exception {
		mockMvc.perform(get(BASE_URL + "/tasks/999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	void getTasksByStatus() throws Exception {

		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");
		TaskRequest task2 = new TaskRequest(Task.Status.INACTIVE, "Task 2", "Task 2 desc");
		TaskRequest task3 = new TaskRequest(Task.Status.ACTIVE, "Task 3", "Task 3 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);
		createTask(task2, token);
		createTask(task3, token);

		mockMvc.perform(get(BASE_URL + "/tasks?status=ACTIVE")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].status").value("ACTIVE"))
				.andExpect(jsonPath("$[0].name").value("Task 1"))
				.andExpect(jsonPath("$[0].description").value("Task 1 desc"))
				.andExpect(jsonPath("$[1].id").value(3))
				.andExpect(jsonPath("$[1].status").value("ACTIVE"))
				.andExpect(jsonPath("$[1].name").value("Task 3"))
				.andExpect(jsonPath("$[1].description").value("Task 3 desc"));

	}

	@Test
	void getTasksByUnknownStatus() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");
		TaskRequest task2 = new TaskRequest(Task.Status.INACTIVE, "Task 2", "Task 2 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);
		createTask(task2, token);

		mockMvc.perform(get(BASE_URL + "/tasks?status=SILLY")
						.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getTasksByEmptyStatus() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");
		TaskRequest task2 = new TaskRequest(Task.Status.INACTIVE, "Task 2", "Task 2 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);
		createTask(task2, token);

		mockMvc.perform(get(BASE_URL + "/tasks?status=")
						.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}

	@Test
	void changeTaskStatusToAValidStatus() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		TaskRequest newTask = new TaskRequest(Task.Status.INACTIVE, "Task 1", "Task 1 desc");

		mockMvc.perform(put(BASE_URL + "/tasks/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTask))
						.header("Authorization", "Bearer " + token)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_URL + "/tasks/1")
				.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("INACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value("Task 1 desc"));

	}

	@Test
	void changeTaskStatusToAnInvalidStatus() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		mockMvc.perform(put(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\": \"SILLY\",\"name\": \"Task 1\",\"description\": \"Task 1 desc\"}")
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value("Task 1 desc"));

	}

	@Test
	void changeTaskName() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		TaskRequest newTask = new TaskRequest(Task.Status.ACTIVE, "Task 10 now", "Task 1 desc");

		mockMvc.perform(put(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newTask))
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 10 now"))
				.andExpect(jsonPath("description").value("Task 1 desc"));

	}

	@Test
	void changeTaskDescription() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		TaskRequest newTask = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 10 desc now");

		mockMvc.perform(put(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newTask))
								.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value("Task 10 desc now"));

	}

	@Test
	void changeTaskDescriptionToBlank() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		registerBasicUser();
		String token = loginBasicUser().split(":")[1];

		createTask(task1, token);

		TaskRequest newTask = new TaskRequest(Task.Status.ACTIVE, "Task 1", "");

		mockMvc.perform(put(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newTask))
						.header("Authorization", "Bearer " + token)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mockMvc.perform(get(BASE_URL + "/tasks/1")
						.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value(""));

	}

}
