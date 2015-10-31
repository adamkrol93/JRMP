/*
 * Licensed to Author or Authors under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * Author licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ut.net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.junit.rules.AvailableInContainer;
import com.atlassian.jira.junit.rules.MockComponentContainer;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.services.RenderTemplateServiceImpl;
import net.amg.jira.plugins.jrmp.services.VelocityContextFactory;
import net.amg.jira.plugins.jrmp.services.VelocityManagerFactory;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import net.amg.jira.plugins.jrmp.velocity.Row;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Scrub test for RenderTemplateService.
 * @author Augustyn AugustynWilk@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class RenderTemplateServiceTest {
    //Inject mocks to tested class RenderTemplateService:
    @InjectMocks
    private RenderTemplateServiceImpl templateService = new RenderTemplateServiceImpl();
    //Tested class dependencies
    @Rule public MockComponentContainer container = new MockComponentContainer(this);

    @Mock @AvailableInContainer private DateTimeFormatter dateTimeFormatter;
    @Mock @AvailableInContainer private I18nResolver i18nResolver;
    @Mock @AvailableInContainer private WebResourceUrlProvider webResourceUrlProvider;
    @Mock private VelocityContextFactory contextFactory;
    @Mock private VelocityManagerFactory managerFactory;
    @Mock private ProjectOrFilter pof;
    //Internal fields:
    @Mock private VelocityManager velocityManager;
    private List<Row> rowList = new ArrayList<Row>();
    @Mock private VelocityContext velocityContext;
    @Mock RiskIssues riskIssues;
    //Helper
    ResourceBundle bundle;
    String resultsString="\n" +
            "\n" +
            "<div class=\"amg jrmp matrix \">\n" +
            "    <div class=\"matrixHeaderRow\">\n" +
            "                    <div class=\"projectLabeL\">$titleLabel $matrixTitle </div>\n" +
            "                <div class=\"projectLabeL\">$projectLabel</div> <a href=\"$projectURL\">$projectName</a> &nbsp&nbsp <div class=\"riskDateLabel\"> $riskDateLabel $date</div><br>\n" +
            "    </div>\n" +
            "    <div class=\"matrixHeaderRow\">\n" +
            "                <div class=\"redTasksLabel\">$redTasksLabel</div> <div class=\"tasksValue\">$redTasksValue</div> <div class=\"separator\">|</div>\n" +
            "        <div class=\"yellowTasksLabel\">$yellowTasksLabel</div> <div class=\"tasksValue\">$yellowTasksValue</div> <div class=\"separator\">|</div>\n" +
            "        <div class=\"greenTasksLabel\">$greenTasksLabel</div> <div class=\"tasksValue\">$greenTasksValue</div>\n" +
            "    </div>\n" +
            "    <div class=\"table\">\n" +
            "        <div class=\"ylabel\">\n" +
            "            <p class=\"prob-label\">$probabilityLabel</p>\n" +
            "        </div>\n" +
            "        <table class=\"matrix\">\n" +
            "                                    <tr>\n" +
            "                <td class=\"empty\"></td>\n" +
            "                            </tr>\n" +
            "            <tr>\n" +
            "                <td class=\"empty\"></td>\n" +
            "                <td class=\"xlabel\" colspan=\"$matrixSize\">$consequenceLabel</td>\n" +
            "            </tr>\n" +
            "        </table>\n" +
            "    </div>\n" +
            "</div>\n";

    @Before
    public void setup() {
        this.bundle = ResourceBundle.getBundle("i18n/translations");
        when(webResourceUrlProvider.getBaseUrl()).thenReturn("http://localhost:2990/jira");
        when(riskIssues.getRedTasks()).thenReturn(1);
        when(riskIssues.getGreenTasks()).thenReturn(1);
        when(riskIssues.getYellowTasks()).thenReturn(1);
        when(riskIssues.getListOfRows()).thenReturn(rowList);
        when(contextFactory.getDefaultContext()).thenReturn(velocityContext);
        when(managerFactory.getVelocityManager()).thenReturn(velocityManager);
        when(velocityManager.getEncodedBody("templates/", "matrixTemplate.vm", null, "UTF-8", velocityContext)).thenReturn(resultsString);
        when(pof.isFilter()).thenReturn(true);
        when(pof.isProject()).thenReturn(false);
    }
    @Test
    public void renderMatrixTemplateTest() throws Exception {
        //when:
        String result = templateService.renderTemplate(pof, "Title", "default", riskIssues);
        //then:
        verify(pof, times(1)).isFilter();
        verify(pof, times(1)).isProject();
        verify(pof, times(1)).getName();
        verify(webResourceUrlProvider, times(1)).getBaseUrl();

        StringWriter sw = new StringWriter();
        Writer writer = new BufferedWriter(sw);
        VelocityContext context = new VelocityContext();

        Velocity.mergeTemplate("src/main/resources/templates/matrixTemplate.vm", "UTF-8", context, writer);
        writer.close();
        sw.close();
        String expectedResults = sw.getBuffer().toString();

        assertEquals("Template service result is not what expected", expectedResults, result);
    }
}
