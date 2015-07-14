package it.net.amg.jira.plugins.rest;

import net.amg.jira.plugins.jrmp.rest.model.MatrixRequest;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//import net.amg.jira.plugins.jrmp.rest.RESTRiskManagementResourceModel;

public class JRMPRiskManagementControllerFuncTest {
    MatrixRequest request;
    @Before
    public void setup() {
        request = new MatrixRequest();
        request.setDate("2015-07-09");
        request.setFilter("project-10000");
        request.setTemplate("default");
        request.setTitle(null);
    }

    @After
    public void tearDown() {
        request = null;
    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/jira-risk-management/1.0/controller/matrix";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);
        ClientResponse resp = resource.post(request);

        /*RESTRiskManagementResourceModel message = resource.get(RESTRiskManagementResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());*/
    }
}
