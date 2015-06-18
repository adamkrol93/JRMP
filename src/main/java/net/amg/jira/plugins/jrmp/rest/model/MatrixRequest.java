package net.amg.jira.plugins.jrmp.rest.model;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.sal.api.message.I18nResolver;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonatan on 30.05.15.
 */
public class MatrixRequest {

    public static final String BUNDLE_ERROR_EMPTY_FILTER = "risk.management.validation.error.empty_filter";
    public static final String BUNDLE_ERROR_FILTER_IS_INCORRECT = "risk.management.validation.error.filter_is_incorrect";
    public static final String BUNDLE_ERROR_EMPTY_DATE = "risk.management.validation.error.empty_date";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String filter;

    private String title;

    private String date;

    private String template;

    private String refreshRate;

    private ProjectOrFilter projectOrFilter;

    private DateModel dateModel;

    public ErrorCollection doValidation(I18nResolver i18nResolver, JiraAuthenticationContext authenticationContext, SearchService searchService, OfBizDelegator ofBizDelegator) {
        ErrorCollection errorCollection = new ErrorCollection();

        if (StringUtils.isBlank(filter)) {
            errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText(BUNDLE_ERROR_EMPTY_FILTER));
        } else {
            projectOrFilter = ProjectOrFilter.createProjectOrFilter(filter,ofBizDelegator);
            if (!projectOrFilter.isValid()) {
                errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText(BUNDLE_ERROR_FILTER_IS_INCORRECT));
            } else {
                if (projectOrFilter.getQuery() == null) {
                    errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText(BUNDLE_ERROR_FILTER_IS_INCORRECT));
                } else {
                    MessageSet messageSet = searchService.validateQuery(authenticationContext.getUser().getDirectoryUser(), projectOrFilter.getQuery());
                    if (messageSet.hasAnyErrors()) {
                        logger.warn("Query is invalid. Enable info for search errors list");
                        if (logger.isInfoEnabled()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Search error messages: \n");
                            for (String msg : messageSet.getErrorMessagesInEnglish()) {
                                sb.append(msg +"\n");
                            }
                            logger.info(sb.toString());
                        }
                        errorCollection.addError(GadgetFieldEnum.FILTER.toString(), i18nResolver.getText(BUNDLE_ERROR_FILTER_IS_INCORRECT));
                    }
                }
            }


        }

        if (StringUtils.isBlank(date)) {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText(BUNDLE_ERROR_EMPTY_DATE));
        }

        try {
            dateModel = DateModel.valueOf(date);
        } catch (NullPointerException e) {
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

    public Map<String,String> getParameters()
    {
        Map<String,String> parameters = new HashMap<String, String>();
        parameters.put(GadgetFieldEnum.DATE.toString(),this.date);
        parameters.put(GadgetFieldEnum.FILTER.toString(),this.filter);
        parameters.put(GadgetFieldEnum.REFRESH.toString(),this.refreshRate);
        parameters.put(GadgetFieldEnum.TEMPLATE.toString(),this.template);
        parameters.put(GadgetFieldEnum.TITLE.toString(),this.title);
        return parameters;
    }
}
