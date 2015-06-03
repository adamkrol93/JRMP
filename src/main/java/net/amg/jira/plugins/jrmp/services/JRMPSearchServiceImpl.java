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
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adam Król
 */
@Service
public class JRMPSearchServiceImpl implements JRMPSearchService {


    private SearchService searchService;
    private JiraAuthenticationContext authenticationContext;
    private CustomFieldManager customFieldManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<Issue> getAllQualifiedIssues(Query query) {
        if(query == null)
        {
           return new LinkedList<Issue>();
        }

        QueryBuiler builder = new QueryBuilderImpl(customFieldManager);
        builder.buildQuery(query);

        /*Iterator<Clause> iterator = query.getWhereClause().getClauses().iterator();

        while(iterator.hasNext()) {

            Clause c = iterator.next();
            if(c.getName().contains("issuetype") || c.getName().contains(PluginListener.RISK_CONSEQUENCE_TEXT_CF) || c.getName().contains(PluginListener.RISK_PROBABILITY_TEXT_CF))
            {
                query.getWhereClause().getClauses().remove(c);
            }
        }

        JqlQueryBuilder builder = JqlQueryBuilder.newBuilder(query);


        builder.where().and().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF).getIdAsLong()).isNotEmpty()
                .and().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF).getIdAsLong()).isNotEmpty()
                .and().issueType(PluginListener.RISK_ISSUE_TYPE);
        query = builder.buildQuery();*/


        SearchResults searchResults;
        try {
            searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            logger.info("getMatrixSize Error, searchResult are null",e);
            return new LinkedList<Issue>();
        }

        if(searchResults.getIssues().isEmpty())
        {
            return new LinkedList<Issue>();
        }

        return searchResults.getIssues();
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setAuthenticationContext(JiraAuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
}
