import fr.togepic.webservices.GlobalWSBean;
import fr.togepic.webservices.UserWSBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.EnableServices;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

@EnableServices(value = "jaxrs")
@RunWith(ApplicationComposer.class)
public class AuthenticateWSTest {

        @Module
        @Classes({ UserWSBean.class, GlobalWSBean.class })
        public WebApp app() {
                return new WebApp().contextRoot("togepic");
        }

        @Test
        public void test() {
                WebClient webClient = WebClient.create("http://localhost:4204/togepic/");
                Response response = webClient.path("/login")
                                .type(MediaType.APPLICATION_FORM_URLENCODED)
                                .post(new Form().param("email", "jean").param("password", "azerty123"));
                assertEquals(Response.Status.CONFLICT.getStatusCode(),
                                response.getStatus());

                response = webClient.path("/create")
                                .type(MediaType.APPLICATION_FORM_URLENCODED)
                                .post(new Form().param("email", "jean").param("password", "azerty123"));
                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

                response = webClient.path("/create")
                                .type(MediaType.APPLICATION_FORM_URLENCODED)
                                .post(new Form().param("email", "jean").param("password", "azerty123"));
                assertEquals(Response.Status.CONFLICT.getStatusCode(),
                                response.getStatus());

                response = webClient.path("/login")
                                .type(MediaType.APPLICATION_FORM_URLENCODED)
                                .post(new Form().param("email", "jean").param("password", "azerty123"));
                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
}
