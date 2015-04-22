package ut.net.amg.jira.plugins;

import org.junit.Test;
import net.amg.jira.plugins.MyPluginComponent;
import net.amg.jira.plugins.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}