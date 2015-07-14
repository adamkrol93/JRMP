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

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.services.*;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Scrub test for MatrixGeneratorImpl.
 * @author Augustyn AugustynWilk@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class MatrixGeneratorImplTest {
    //Inject mocks to tested class matrixGenerator:
    @InjectMocks private MatrixGenerator matrixGenerator = new MatrixGeneratorImpl();
    //Tested class dependencies:
    @Mock private JRMPSearchService jrmpSearchService;
    @Mock private RenderTemplateService renderTemplate;
    @Mock private RiskIssuesFinder riskIssuesFinder;
    @Mock private OfBizDelegator ofBizDelegator;
    @Mock private ProjectOrFilter pof;
    //Tested class internal variables
    private RiskIssues risks;
    private List<Issue> issues;
    @Before
    public void setup() {
        //Before
        risks = new RiskIssues();
        issues = new ArrayList<Issue>();
        //matrixGenerator = new MatrixGeneratorImpl();
        Query query = Mockito.mock(Query.class);
        when(pof.getQuery()).thenReturn(query);
        when(pof.getName()).thenReturn("projectOrFilterName");

        //when
        when(jrmpSearchService.getAllQualifiedIssues(query, DateModel.TODAY)).thenReturn(issues);
        when(riskIssuesFinder.fillAllFields(issues, query, DateModel.TODAY)).thenReturn(risks);
        when(renderTemplate.renderTemplate(pof, "test title", "default", risks)).thenReturn("");
    }


    @Test
    public void matrixGenerateTest() {
        String result = matrixGenerator.generateMatrix(pof, "test title", "default", DateModel.TODAY);
        verify(jrmpSearchService, times(1)).getAllQualifiedIssues(pof.getQuery(), DateModel.TODAY);
        verify(riskIssuesFinder, times(1)).fillAllFields(new ArrayList<Issue>(), pof.getQuery(), DateModel.TODAY);
        verify(renderTemplate, Mockito.times(1)).renderTemplate(pof, "test title", "default", risks);
        assertEquals("Returned value is not expected", "", result);
    }

    @Ignore
    @Test
    public void ignoredDummyTest(){}
    @After
    public void tearDown() {}
}
