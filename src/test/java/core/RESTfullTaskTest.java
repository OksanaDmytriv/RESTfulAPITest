package core;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class RESTfullTaskTest {

    @Test
    public void testUnauthorizedRead(){
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:5000/todo/api/v1.0/tasks");

    Response response = target.request().get();

    assertEquals(200, response.getStatus());
    }
}
