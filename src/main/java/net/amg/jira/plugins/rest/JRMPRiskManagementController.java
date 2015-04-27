package net.amg.jira.plugins.rest;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.util.collect.CollectionBuilder;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.query.Query;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controller used for validations and other useful things
 *
 * @author Adam Król
 */
@Path("/controller")
public class JRMPRiskManagementController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private SearchRequestService searchRequestService;

    private UserManager userManager;

    private UserUtil userUtil;

    private I18nResolver i18nResolver;

    private SearchService searchService;

    private JiraAuthenticationContext authenticationContext;

    public JRMPRiskManagementController(SearchRequestService searchRequestService,UserManager userManager,
                                        UserUtil userUtil, I18nResolver i18nResolver, SearchService searchService,
                                        JiraAuthenticationContext jiraAuthenticationContext) {
        this.userManager = userManager;
        this.searchRequestService = searchRequestService;
        this.userUtil = userUtil;
        this.i18nResolver = i18nResolver;
        this.searchService = searchService;
        this.authenticationContext = jiraAuthenticationContext;
    }

    @Path("/validate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @AnonymousAllowed
    public Response doValidation(@Context HttpServletRequest request) {
        ErrorCollection errorCollection = new ErrorCollection();
        errorCollection.setParameters(request.getParameterMap());
        String filter = request.getParameter(GadgetFieldEnum.FILTER.toString());
        String relativeDate = request.getParameter(GadgetFieldEnum.DATE.toString());
        String title = request.getParameter(GadgetFieldEnum.TITLE.toString());
        String refreshRate = request.getParameter(GadgetFieldEnum.REFRESH.toString());
        Gson gson = new Gson();
        ApplicationUser user = userUtil.getUserByName(userManager.getRemoteUsername(request));
        if(user == null)
        {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if(StringUtils.isBlank(filter))
        {

            errorCollection.addError(GadgetFieldEnum.FILTER.toString(),i18nResolver.getText("risk.management.validation.error.empty_filter"));
        }else{
            Query query = getQuery(filter.split("-")[1]);

            if(query != null)
            {
                MessageSet messageSet = searchService.validateQuery(user.getDirectoryUser(),query);
                if(messageSet.hasAnyErrors())
                {
                    errorCollection.addError(GadgetFieldEnum.FILTER.toString(),i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
                }
            }else{
                errorCollection.addError(GadgetFieldEnum.FILTER.toString(),i18nResolver.getText("risk.management.validation.error.filter_is_incorrect"));
            }
        }

        if(StringUtils.isBlank(relativeDate))
        {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.empty_date"));
        }

        if(StringUtils.isBlank(title))
        {
            errorCollection.addError(GadgetFieldEnum.TITLE.toString(), i18nResolver.getText("risk.management.validation.error.empty_title"));
        }

        if(StringUtils.isBlank(refreshRate))
        {
                errorCollection.addError(GadgetFieldEnum.REFRESH.toString(), i18nResolver.getText("risk.management.validation.error.empty_refresh"));
        }


        if(errorCollection.hasAnyErrors()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errorCollection)).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    private Query getQuery(String filter) {
        JqlClauseBuilder subjectBuilder = JqlQueryBuilder.newClauseBuilder().savedFilter(filter);
        return subjectBuilder.buildQuery();
    }
}
