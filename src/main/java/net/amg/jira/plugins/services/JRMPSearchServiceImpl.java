package net.amg.jira.plugins.services;

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
import net.amg.jira.plugins.exceptions.NoIssuesFoundException;
import net.amg.jira.plugins.listeners.PluginListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.extensions.annotation.ServiceReference;

import java.util.Iterator;
import java.util.List;

/**
 * @author Adam Król
 */
public class JRMPSearchServiceImpl implements JRMPSearchService {


    private SearchService searchService;
    private JiraAuthenticationContext authenticationContext;
    private CustomFieldManager customFieldManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

//    public JRMPSearchServiceImpl(SearchService searchService, JiraAuthenticationContext authenticationContext, CustomFieldManager customFieldManager) {
//        this.searchService = searchService;
//        this.authenticationContext = authenticationContext;
//        this.customFieldManager = customFieldManager;
//    }


    @ServiceReference
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @ServiceReference
    public void setAuthenticationContext(JiraAuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @ServiceReference
    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    @Override
    public List<Issue> getAllQualifiedIssues(Query query) throws NoIssuesFoundException {
        if(query == null)
        {
           throw new NoIssuesFoundException();
        }

        Iterator<Clause> iterator = query.getWhereClause().getClauses().iterator();

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
        query = builder.buildQuery();


        SearchResults searchResults;
        try {
            searchResults =  searchService.search(authenticationContext.getUser().getDirectoryUser(), query, PagerFilter.getUnlimitedFilter());
        } catch (SearchException e) {
            logger.info("getMatrixSize Error, searchResult are null",e);
            throw new NoIssuesFoundException("searchService returned Exception",e);
        }

        if(searchResults.getIssues().isEmpty())
        {
            throw new NoIssuesFoundException("No issues found for this filter");
        }

        return searchResults.getIssues();
    }
}
