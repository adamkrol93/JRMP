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

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.services.QueryBuilder;
import net.amg.jira.plugins.jrmp.services.RiskIssuesFinder;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Scrub test for RenderTemplateService.
 * @author Augustyn AugustynWilk@gmail.com
 * Creted on 18.07.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class RiskIssuesFinderTest {
    //Inject mocks to tested class RenderTemplateService:
    @InjectMocks
    private RiskIssuesFinder issuesFinder = new RiskIssuesFinder();
    //Tested class dependencies
    @Mock private WebResourceUrlProvider webResourceUrlProvider;
    @Mock private CustomFieldManager customFieldManager;
    @Mock private QueryBuilder queryBuilder;
    @Mock private SearchService searchService;
    @Mock private ApplicationProperties jiraProps;
    //required test fields
    private List<Issue> issues = new ArrayList<Issue>();
    @Mock private Issue issue;
    @Mock private Query query;
    //internal fields
    int probability = 0, consequence = 0;

    @Before
    public void init() {
        when(queryBuilder.buildFilterQuery(probability, consequence, query, DateModel.TODAY)).thenReturn(query);
        when(searchService.getJqlString(query)).thenReturn("?q=project=10100");
        when(issue.getUpdated()).thenReturn(new Timestamp(System.currentTimeMillis()));
        issues.add(issue);
    }



    @Test
    public void issuesFinderTest() {
        RiskIssues result = issuesFinder.fillAllFields(new ArrayList<Issue>(), query, DateModel.TODAY);
        assertEquals("RiskIssueFinder should return model with empty issue list when passing empty issues list to method",
                Collections.emptyList().size(), result.getIssues().size());
        assertEquals("RiskIssueFinder should return model with empty task list when passing empty issues list to method",
                Collections.emptyList().size(), result.getTasks().size());
        assertEquals("RiskIssueFinder should return model with empty field when passing empty issue list to method",
                0, result.getGreenTasks());
        assertEquals("RiskIssueFinder should return model with empty field when passing empty issue list to method",
                0, result.getYellowTasks());
        assertEquals("RiskIssueFinder should return model with empty field when passing empty issue list to method",
                0, result.getRedTasks());
        assertEquals("RiskIssueFinder should return model with "+RiskIssues.MATRIX_SIZE+" rows when passing empty issue list to method",
                RiskIssues.MATRIX_SIZE, result.getListOfRows().size());

        assertNotNull("Issue in issues List should not have empty field 'last update' ", issue.getUpdated());
        result = issuesFinder.fillAllFields(issues, query, DateModel.TODAY);
        assertNotNull("Result shouldn't be null", result);
        assertEquals("Tasks count should be the same as Issues in list", result.getTasks().size(), issues.size());
    }
}
