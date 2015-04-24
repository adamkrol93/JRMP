package net.amg.jira.plugins.rest;

import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
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

    public JRMPRiskManagementController(SearchRequestService searchRequestService,UserManager userManager,
                                        UserUtil userUtil) {
        this.userManager = userManager;
        this.searchRequestService = searchRequestService;
        this.userUtil = userUtil;
    }

    @Path("/validate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @AnonymousAllowed
    public Response doValidation(@QueryParam("Filter") String filter,
                                 @QueryParam("Date") String relativeDate,
                                 @QueryParam("Title") String title,
                                 @QueryParam("refresh") Integer refreshRate,
                                 @Context HttpServletRequest request) {
        ErrorCollection errorCollection = new ErrorCollection();
        errorCollection.setParameters(request.getParameterMap());

        Gson gson = new Gson();
        logger.info("----------------->Witam w Walidacji");
        ApplicationUser user = userUtil.getUserByName(userManager.getRemoteUsername(request));
        if(user == null)
        {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if(filter==null || "".equals(filter))
        {

            errorCollection.addError("filter","filter is empty");
        }

        if(relativeDate == null || "".equals(relativeDate))
        {
            errorCollection.addError("Date", "Date is empty");
        }

        if(title == null || "".equals(title))
        {
            errorCollection.addError("Title", "Title is empty");
        }

        if(refreshRate == null || refreshRate == 0)
        {
                errorCollection.addError("refresh", "RefreshRate is empty");
        }

        if(searchRequestService == null || searchRequestService.getFilter(new JiraServiceContextImpl(user),Long.valueOf(filter)) == null)
        {
            errorCollection.addError("filter","filter id is wrong",filter);
        }


        if(errorCollection.hasAnyErrors()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errorCollection)).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
