/*Copyright 2015 AMG.net

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.model.RiskIssuesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatrixGeneratorImpl implements MatrixGenerator {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private JRMPSearchService jrmpSearchService;
    private WebResourceUrlProvider webResourceUrlProvider;
    private CustomFieldManager customFieldManager;
    private QueryBuilder queryBuilder;
    private SearchService searchService;
    private RenderTemplateService renderTemplate;


    @Override
    public String generateMatrix(ProjectOrFilter projectOrFilter, String matrixTitle, String matrixTemplate, DateModel dateModel) {
        logger.info("generateMatrix: Method start");
        List<Issue> listOfIssues = jrmpSearchService.getAllQualifiedIssues(projectOrFilter.getQuery(), dateModel);

        RiskIssuesModel riskIssuesModel = new RiskIssuesModel(listOfIssues, webResourceUrlProvider, customFieldManager,
                queryBuilder, projectOrFilter.getQuery(), dateModel, searchService);
        riskIssuesModel.fillAllFields();

        return renderTemplate.renderTemplate(projectOrFilter, matrixTitle, matrixTemplate, riskIssuesModel);
    }

    public RenderTemplateService getRenderTemplate() {
        return renderTemplate;
    }

    public void setRenderTemplate(RenderTemplateService renderTemplate) {
        this.renderTemplate = renderTemplate;
    }

    public void setJrmpSearchService(JRMPSearchService jrmpSearchService) {
        this.jrmpSearchService = jrmpSearchService;
    }

    public void setWebResourceUrlProvider(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

}
