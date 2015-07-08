/*
 * Licensed to AMG.net under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * AMG.net licenses this file to you under the Apache License,
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
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.Issue;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatrixGeneratorImpl implements MatrixGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JRMPSearchService jrmpSearchService;
    @Autowired
    private RenderTemplateService renderTemplate;
    @Autowired
    private RiskIssuesFinder riskIssuesFinder;

    @Override
    public String generateMatrix(ProjectOrFilter projectOrFilter, String matrixTitle, String matrixTemplate, DateModel dateModel) {

        logger.info("generateMatrix: Method start");

        List<Issue> issues = jrmpSearchService.getAllQualifiedIssues(projectOrFilter.getQuery(), dateModel);

        logger.info("Found {} issues", issues);
        RiskIssues riskIssues = riskIssuesFinder.fillAllFields(issues, projectOrFilter.getQuery(), dateModel);

        return renderTemplate.renderTemplate(projectOrFilter, matrixTitle, matrixTemplate, riskIssues);
    }

    public void setJrmpSearchService(JRMPSearchService jrmpSearchService) {
        this.jrmpSearchService = jrmpSearchService;
    }

    public void setRenderTemplate(RenderTemplateService renderTemplate) {
        this.renderTemplate = renderTemplate;
    }

    public void setRiskIssuesFinder(RiskIssuesFinder riskIssuesFinder) {
        this.riskIssuesFinder = riskIssuesFinder;
    }
}
