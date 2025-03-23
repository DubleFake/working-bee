package com.homework.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.task.database.Task;
import com.homework.task.database.TaskService;
import com.homework.task.web.controllers.GeneralController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
class GeneralControllerTests {

	private static final String BASE_URL = "http://localhost:8080/tasks";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private TaskService taskService;

	@Test
	void addTask() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void addTaskWithoutStatus() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setName("Task 1");
		task1.setDescription("Task 1 desc");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addTaskWithEmptyDescription() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setStatus(Task.Status.ACTIVE);
		task1.setName("Task 1");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addTaskWithEmptyName() throws Exception {
		TaskRequest task1 = new TaskRequest();
		task1.setStatus(Task.Status.ACTIVE);
		task1.setDescription("Task 1 desc");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	@Test
	void addEmptyTask() throws Exception {
		TaskRequest task1 = new TaskRequest();
		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void addTaskWithUnexpectedVariable() throws Exception {

		class UnexpectedTaskRequest {
			private Task.Status status;
			private String name;
			private String description;
			private String surprise;

			public UnexpectedTaskRequest(Task.Status status, String surprise, String description, String name) {
				this.status = status;
				this.surprise = surprise;
				this.description = description;
				this.name = name;
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

			public String getSurprise() {
				return surprise;
			}

			public void setSurprise(String surprise) {
				this.surprise = surprise;
			}
		}

		UnexpectedTaskRequest unexpectedTaskRequest = new UnexpectedTaskRequest(Task.Status.ACTIVE, "This field is unexpected", "Task 1 desc", "Task 1");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(unexpectedTaskRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void getTaskWithValidId() throws Exception {
		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		mockMvc.perform(post(BASE_URL)
						.content(new ObjectMapper().writeValueAsString(task1))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		mockMvc.perform(get(BASE_URL + "/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1))
				.andExpect(jsonPath("status").value("ACTIVE"))
				.andExpect(jsonPath("name").value("Task 1"))
				.andExpect(jsonPath("description").value("Task 1 desc"));

	}

	@Test
	void getTaskWithInvalidId() throws Exception {
		mockMvc.perform(get(BASE_URL + "/999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	void testGetTasksByStatus() throws Exception {

		TaskRequest task1 = new TaskRequest(Task.Status.ACTIVE, "Task 1", "Task 1 desc");

		mockMvc.perform(post(BASE_URL)
					.content(new ObjectMapper().writeValueAsString(task1))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		// Perform the GET request and verify the response
		mockMvc.perform(get(BASE_URL + "?status=ACTIVE")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].status").value("ACTIVE"))
				.andExpect(jsonPath("$[0].name").value("Task 1"))
				.andExpect(jsonPath("$[0].description").value("Task 1 desc"));

	}

}
