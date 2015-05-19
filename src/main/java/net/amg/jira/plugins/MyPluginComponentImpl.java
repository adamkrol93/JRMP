package net.amg.jira.plugins;

import com.atlassian.sal.api.ApplicationProperties;
import org.springframework.osgi.extensions.annotation.ServiceReference;

public class MyPluginComponentImpl implements MyPluginComponent
{
    private ApplicationProperties applicationProperties;

//    public MyPluginComponentImpl(ApplicationProperties applicationProperties)
//    {
//        this.applicationProperties = applicationProperties;
//    }


    @ServiceReference
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "myComponent:" + applicationProperties.getDisplayName();
        }
        
        return "myComponent";
    }
}