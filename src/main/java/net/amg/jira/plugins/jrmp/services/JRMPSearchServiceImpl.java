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
 *//*
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

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Adam Król
 * @author augustynwilk@gmail.com
 */
@Service
public class JRMPSearchServiceImpl implements JRMPSearchService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SearchService searchService;
    @Autowired
    private JiraAuthenticationContext authenticationContext;
    @Autowired
    private QueryBuilder builder;

    JRMPSearchServiceImpl(){}

    @Override
    public List<Issue> getAllQualifiedIssues(Query query, DateModel dateModel) {
        if(query == null) {
            logger.info("Got null Query, returning empty result");
            return Collections.emptyList();
        }

        query = builder.buildQuery(query,dateModel);

        SearchResults searchResults;
        try {
            searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            logger.info("getMatrixSize Error, searchResult are null : " + e.getMessage(),e);
            return Collections.emptyList();
        }

        if(searchResults.getIssues().isEmpty())
        {
            logger.info("Search query: " + query.toString() + " didn't return any result. Returning empty result.");
            return Collections.emptyList();
        }
        logger.debug("Search Result with collection size: " + searchResults.getIssues().size());
        return searchResults.getIssues();
    }
}
