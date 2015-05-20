package ut.net.amg.jira.plugins.rest;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

import net.amg.jira.plugins.jrmp.rest.RESTRiskManagementResource;
import net.amg.jira.plugins.jrmp.rest.RESTRiskManagementResourceModel;
import javax.ws.rs.core.Response;

public class RESTRiskManagementResourceTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        RESTRiskManagementResource resource = new RESTRiskManagementResource();

        Response response = resource.getMessage();
        final RESTRiskManagementResourceModel message = (RESTRiskManagementResourceModel) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
