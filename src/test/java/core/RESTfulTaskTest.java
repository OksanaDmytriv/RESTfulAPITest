package core;

import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RESTfulTaskTest {

    private final String URI = "http://localhost:5000/todo/api/v1.0/tasks";

    private Invocation.Builder requestTo(String uri) {
        return ClientBuilder.newClient().target(uri).request();
    }

    private Invocation.Builder authorized(Invocation.Builder requestBuilder) {
        return requestBuilder.header("Authorization", "Basic " + Base64.getEncoder().encodeToString("miguel:python".getBytes()));
    }

    @Test
    public void testUnauthorizedReadTasks() {
        Response response = requestTo(URI).get();

        assertEquals(403, response.getStatus());
        assertEquals("Unauthorized access", response.readEntity(ErrorContainer.class).error);
    }

    @Test
    public void testReadTasks() {
        Response response = authorized(requestTo(URI)).get();
        List<Task> receivedTasks = response.readEntity(TasksContainer.class).getTasks();

        assertEquals(200, response.getStatus());
        assertEquals(2, receivedTasks.size());
        assertEquals("Buy groceries", receivedTasks.get(0).getTitle());
    }

    @Test
    public void testCreate() {
        Response response = authorized(requestTo(URI)).post(Entity.entity(new Task("give lesson"), MediaType.APPLICATION_JSON));

        assertEquals(201, response.getStatus());
        assertEquals("give lesson", response.readEntity(TaskContainer.class).getTask().getTitle());
    }

    @Test
    public void testUpdate() {
        Response response = authorized(requestTo(URI)).get();
        List<Task> receivedTasks = response.readEntity(TasksContainer.class).getTasks();
        Task task = receivedTasks.get(0);
        task.setDescription("New");
        requestTo(URI).put(
                Entity.entity(task, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());
        assertEquals("New", receivedTasks.get(0).description);
    }

    @Test
    public void testDelete() {
        Response response = authorized(requestTo(URI + "/1")).delete();

        if (response.getStatus() != 204) {
            fail("RESPONSE STATUS" + response.getStatus());
        }
    }

    @Test
    public void testCreateUpdateDelete() {
        Response response = authorized(requestTo(URI)).post(Entity.entity(new Task("additional task"), MediaType.APPLICATION_JSON));

        assertEquals("additional task", response.readEntity(TaskContainer.class).getTask().getTitle());

        response = authorized(requestTo(URI)).get();
        List<Task> receivedTasks = response.readEntity(TasksContainer.class).getTasks();
        Task task = receivedTasks.get(0);
        task.setDescription("additional task2");
        requestTo(URI).put(
                Entity.entity(task, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());
        assertEquals("additional task2", receivedTasks.get(0).description);

        response = authorized(requestTo(URI + "/1")).delete();

        if (response.getStatus() != 204) {
            fail("RESPONSE STATUS" + response.getStatus());
        }
    }

    static class TaskContainer {
        private Task task;

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }

    static class TasksContainer {
        public List<Task> getTasks() {
            return tasks;
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
        }

        List<Task> tasks;
    }

    static class Task {
        String description;
        boolean done;
        String title;

        public Task(String title) {
            this.title = title;
        }

        public Task() {

        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        String uri;

    }

    static class ErrorContainer {
        public String error;

        public ErrorContainer(String error) {
            this.error = error;
        }

        public ErrorContainer() {
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ErrorContainer that = (ErrorContainer) o;

            return error != null ? error.equals(that.error) : that.error == null;

        }

        @Override
        public int hashCode() {
            return error != null ? error.hashCode() : 0;
        }

        public boolean equals(ErrorContainer other) {
            return this.error.equals(other.error);
        }
    }
}
