package net.amg.jira.plugins.services;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import net.amg.jira.plugins.listeners.PluginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Adam Król
 */
@Service
public class ImpactPropabilityImpl implements ImpactPropability {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SearchService searchService;

    private JiraAuthenticationContext authenticationContext;

    private ConstantsManager constantsManager;

    private CustomFieldManager customFieldManager;

//    public ImpactPropabilityImpl(SearchService searchService, JiraAuthenticationContext jiraAuthenticationContext, ConstantsManager constantsManager, CustomFieldManager customFieldManager) {
//        this.searchService = searchService;
//        this.authenticationContext = jiraAuthenticationContext;
//        this.constantsManager = constantsManager;
//        this.customFieldManager = customFieldManager;
//    }

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

    @Override
    public double getMaxPropability(Query query)
    {
        if(query == null)
        {
            return 1;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 1x1, bo czemu nie
        }

        SearchResults searchResults;
        try {
           searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
           logger.info("getMatrixSize Error, searchResult are null",e);
            return 1;//Jak coś się nie powiedzie to zwracamy użytkownikowi macierz 1x1, bo czemu nie
        }

        double maxSize = 0;

        for(Issue issue : searchResults.getIssues())
        {
            if(issue.getIssueTypeObject().equals(constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, PluginListener.RISK_ISSUE_TYPE)))
            {
                double riskConsequence = (Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF));
                double riskProbability = (Double) issue.getCustomFieldValue(this.customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF));
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


        return maxSize;
    }
}
