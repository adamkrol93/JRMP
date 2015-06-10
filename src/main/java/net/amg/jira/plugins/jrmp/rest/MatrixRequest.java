package net.amg.jira.plugins.jrmp.rest;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.sal.api.message.I18nResolver;
import net.amg.jira.plugins.jrmp.rest.model.GadgetFieldEnum;
import net.amg.jira.plugins.jrmp.rest.model.ProjectOrFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by jonatan on 30.05.15.
 */
public class MatrixRequest {

    private String filter;

    private String title;

    private String date;

    private String template;

    private String refreshRate;

    ProjectOrFilter projectOrFilter;

    public ErrorCollection doValidation(I18nResolver i18nResolver, JiraAuthenticationContext authenticationContext, SearchService searchService)
    {
        ErrorCollection errorCollection = new ErrorCollection();

        if(StringUtils.isBlank(filter))
        {
            errorCollection.addError(GadgetFieldEnum.FILTER.toString(),i18nResolver.getText("risk.management.validation.error.empty_filter"));
        }else {

            if (projectOrFilter.initProjectOrFilter(filter)){
                if (projectOrFilter.getQuery() != null) {
                    MessageSet messageSet = searchService.validateQuery(authenticationContext.getUser().getDirectoryUser(), projectOrFilter.getQuery());
                    if (messageSet.hasAnyErrors()) {
                        errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
                    }
                } else {
                    errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
                }
            } else {
                errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
            }


        }

        if(StringUtils.isBlank(date))
        {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.empty_date"));
        }

        if(StringUtils.isBlank(refreshRate))
        {
            errorCollection.addError(GadgetFieldEnum.REFRESH.toString(), i18nResolver.getText("risk.management.validation.error.empty_refresh"));
        }

        return errorCollection;
    }

    public ProjectOrFilter getProjectOrFilter() {
        return projectOrFilter;
    }

    public void setProjectOrFilter(ProjectOrFilter projectOrFilter) {
        this.projectOrFilter = projectOrFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }
}
