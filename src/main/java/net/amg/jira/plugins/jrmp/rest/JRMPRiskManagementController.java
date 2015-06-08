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
package net.amg.jira.plugins.jrmp.rest;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.gson.Gson;
import net.amg.jira.plugins.jrmp.rest.model.GadgetFieldEnum;
import net.amg.jira.plugins.jrmp.rest.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.MatrixGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controller used for validations and other useful things
 *
 * @author Adam Król
 */
@Path("/controller")
@Controller
public class JRMPRiskManagementController {

    Logger logger = LoggerFactory.getLogger(getClass());

    private I18nResolver i18nResolver;

    private SearchService searchService;

    private JiraAuthenticationContext authenticationContext;


    private MatrixGenerator matrixGenerator;

    @Path("/validate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response doValidation(@Context HttpServletRequest request) {
        ErrorCollection errorCollection = new ErrorCollection();
        errorCollection.setParameters(request.getParameterMap());
        String filter = request.getParameter(GadgetFieldEnum.FILTER.toString());
        String relativeDate = request.getParameter(GadgetFieldEnum.DATE.toString());
        String title = request.getParameter(GadgetFieldEnum.TITLE.toString());
        String refreshRate = request.getParameter(GadgetFieldEnum.REFRESH.toString());
        Gson gson = new Gson();
        if(StringUtils.isBlank(filter))
        {
            errorCollection.addError(GadgetFieldEnum.FILTER.toString(),i18nResolver.getText("risk.management.validation.error.empty_filter"));
        }else {

            ProjectOrFilter projectOrFilter = new ProjectOrFilter();
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

        if(StringUtils.isBlank(relativeDate))
        {
            errorCollection.addError(GadgetFieldEnum.DATE.toString(), i18nResolver.getText("risk.management.validation.error.empty_date"));
        }

        if(StringUtils.isBlank(refreshRate))
        {
                errorCollection.addError(GadgetFieldEnum.REFRESH.toString(), i18nResolver.getText("risk.management.validation.error.empty_refresh"));
        }


        if(errorCollection.hasAnyErrors()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(gson.toJson(errorCollection)).build();
        }
        return Response.ok().build();
    }

    @Path("/matrix")
    @POST
    @Produces({MediaType.TEXT_HTML})
    @Consumes("application/json")
    public Response getMatrix(MatrixRequest request) {

        String filter = request.getFilter();
        String title = request.getTitle();
        String template = request.getTemplate();
        String date = request.getDate();
        if(filter == null || filter.isEmpty() || template == null || template.isEmpty() || date == null || date.isEmpty()){
            return Response.ok(i18nResolver.getText("risk.management.gadget.matrix.error.empty_list_of_issues"), MediaType.TEXT_HTML).build();
        }

        ProjectOrFilter projectOrFilter = new ProjectOrFilter();
        if (!projectOrFilter.initProjectOrFilter(request.getFilter())){
            logger.error("The Matrix couldnt be generated bacause of bad filter from request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            return Response.ok(matrixGenerator.generateMatrix(projectOrFilter,title,template), MediaType.TEXT_HTML).build();
        } catch(Exception e){
            logger.error("The Matrix couldnt be generated bacause of: " + e.getMessage(),e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    public void setMatrixGenerator(MatrixGenerator matrixGenerator) {
        this.matrixGenerator = matrixGenerator;
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public void setAuthenticationContext(JiraAuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }


    @GET
    public Response emptyGETResponse()
    {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    public Response emptyPOSTResponse()
    {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    public Response emptyPUTResponse()
    {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    public Response emptyDELETEResponse()
    {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
