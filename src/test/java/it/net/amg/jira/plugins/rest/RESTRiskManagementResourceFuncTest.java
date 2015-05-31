package it.net.amg.jira.plugins.rest;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//import net.amg.jira.plugins.jrmp.rest.RESTRiskManagementResourceModel;

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

        /*RESTRiskManagementResourceModel message = resource.get(RESTRiskManagementResourceModel.class);

        assertEquals("wrong message","Hello World",message.getMessage());*/
    }
}
