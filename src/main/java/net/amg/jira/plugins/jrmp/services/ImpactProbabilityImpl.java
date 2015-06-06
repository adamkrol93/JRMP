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

/**
 * @author Adam Król
 */
@Service
public class ImpactProbabilityImpl implements ImpactProbability {

    public static final int MAX_PROBABILITY = 10;
    public static final int MIN_PROBABILITY = 3;
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
            return MIN_PROBABILITY;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 3x3, bo takie jest minimum
        }

        SearchResults searchResults;
        try {
            /*Iterator<Clause> iterator = query.getWhereClause().getClauses().iterator();

            while(iterator.hasNext()) {

                Clause c = iterator.next();
                if(c.getName().contains("issuetype"))
                {
                    query.getWhereClause().getClauses().remove(c);
                }
            }
            JqlQueryBuilder builder = JqlQueryBuilder.newBuilder(query);


            builder.where().and().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF).getIdAsLong()).isNotEmpty()
                    .and().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF).getIdAsLong()).isNotEmpty()
                    .and().issueType(PluginListener.RISK_ISSUE_TYPE);

            query = builder.buildQuery();*/

            QueryBuiler builder = new QueryBuilderImpl(customFieldManager);
            query = builder.buildQuery(query);

           searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            logger.info("Something went wrong while searching for issues",e);
            return MIN_PROBABILITY;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 3x3, bo takie jest minimum
        }

        int maxSize = MIN_PROBABILITY;

        for(Issue issue : searchResults.getIssues())
        {
            if(issue.getIssueTypeObject().equals(constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, PluginListener.RISK_ISSUE_TYPE)))
            {
                int riskConsequence = 0;
                int riskProbability = 0;

                try {
                    riskProbability = ((Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF))).intValue();
                }catch (Exception e)
                {
                    logger.info("Failed to get Risk Consequence/Probability from issue " + issue.getKey());
                }

                try {
                    riskConsequence = ((Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF))).intValue();
                }catch (Exception e)
                {
                    logger.info("Failed to get Risk Consequence/Probability from issue " + issue.getKey());
                }

                if (riskConsequence > maxSize) {
                    maxSize = riskConsequence;
                }
                if (riskProbability > maxSize) {
                    maxSize = riskProbability;
                }
                if(maxSize > MAX_PROBABILITY)
                {
                    return MAX_PROBABILITY;
                }
            }

        }

        return maxSize;
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
