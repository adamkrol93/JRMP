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
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
           return Collections.emptyList();
        }

        QueryBuilder builder = new QueryBuilderImpl(customFieldManager);
        query = builder.buildQuery(query);

        SearchResults searchResults;
        try {
            searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            logger.info("getMatrixSize Error, searchResult are null : " + e.getMessage(),e);
            return Collections.emptyList();
        }

        if(searchResults.getIssues().isEmpty())
        {
            return Collections.emptyList();
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
