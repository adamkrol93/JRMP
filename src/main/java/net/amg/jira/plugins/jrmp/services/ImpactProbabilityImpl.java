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
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Adam Król
 */
@Service
public class ImpactProbabilityImpl implements ImpactProbability {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SearchService searchService;

    private JiraAuthenticationContext authenticationContext;

    private ConstantsManager constantsManager;

    private CustomFieldManager customFieldManager;


    @Override
    public int getMaxProbability(Query query)
    {
        if(query == null)
        {
            return 3;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 3x3, bo takie jest minimum
        }

        SearchResults searchResults;
        try {
           searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
           logger.info("Something went wrong while searching for issues",e);
            return 3;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 3x3, bo takie jest minimum
        }

        Double maxSize = 0.0;

        for(Issue issue : searchResults.getIssues())
        {
            if(issue.getIssueTypeObject().equals(constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, PluginListener.RISK_ISSUE_TYPE)))
            {
                Double riskConsequence = (Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF));
                Double riskProbability = (Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF));
                if(riskConsequence > maxSize)
                {
                    maxSize = riskConsequence;
                }
                if(riskProbability > maxSize)
                {
                    maxSize = riskProbability;
                }
            }

        }


        return maxSize.intValue();
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setAuthenticationContext(JiraAuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    public void setConstantsManager(ConstantsManager constantsManager) {
        this.constantsManager = constantsManager;
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
}
