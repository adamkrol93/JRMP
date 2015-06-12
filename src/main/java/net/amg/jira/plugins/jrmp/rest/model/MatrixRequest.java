package net.amg.jira.plugins.jrmp.rest.model;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.sal.api.message.I18nResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jonatan on 30.05.15.
 */
public class MatrixRequest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String filter;

    private String title;

    private String date;

    private String template;

    private String refreshRate;

    ProjectOrFilter projectOrFilter;

    DateModel dateModel;

    public ErrorCollection doValidation(I18nResolver i18nResolver, JiraAuthenticationContext authenticationContext, SearchService searchService) {
        ErrorCollection errorCollection = new ErrorCollection();

        if (StringUtils.isBlank(filter)) {
            errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.empty_filter"));
        } else {
            projectOrFilter = new ProjectOrFilter();
            if (!projectOrFilter.initProjectOrFilter(filter)) {
                errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
            } else {
                if (projectOrFilter.getQuery() == null) {
                    errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
                } else {
                    MessageSet messageSet = searchService.validateQuery(authenticationContext.getUser().getDirectoryUser(), projectOrFilter.getQuery());
                    if (messageSet.hasAnyErrors()) {
                        logger.warn("Query is invalid. Errors listed below");
                        for(String s : messageSet.getErrorMessagesInEnglish())
                        {
                            logger.warn(s);
                        }
                        errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
                    }
                }
            }


        }

        if (StringUtils.isBlank(date)) {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.empty_date"));
        }

        try {
            dateModel = DateModel.values()[Integer.valueOf(date)];
        } catch (IndexOutOfBoundsException e) {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.wrong_date"));
        } catch (NumberFormatException e) {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.wrong_date"));
        }

        if (StringUtils.isBlank(refreshRate)) {
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

    public DateModel getDateModel() {
        return dateModel;
    }

    public void setDateModel(DateModel dateModel) {
        this.dateModel = dateModel;
    }
}