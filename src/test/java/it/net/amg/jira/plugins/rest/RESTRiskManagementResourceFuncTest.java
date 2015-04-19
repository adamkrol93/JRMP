package it.net.amg.jira.plugins.rest;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import net.amg.jira.plugins.rest.RESTRiskManagementResource;
import net.amg.jira.plugins.rest.RESTRiskManagementResourceModel;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

public class RESTRiskManagementResourceFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/restcontroller/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        RESTRiskManagementResourceModel message = resource.get(RESTRiskManagementResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());
    }
}
