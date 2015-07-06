package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.velocity.DefaultVelocityManager;
import com.atlassian.velocity.VelocityManager;
import net.amg.jira.plugins.jrmp.services.model.ProjectOrFilter;
import net.amg.jira.plugins.jrmp.services.model.RiskIssuesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yahro01 on 7/6/15.
 */
public class RenderTemplateService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String MATRIX_SIZE_STRING = "matrixSize";
    public static final String PROJECT_NAME_STRING = "projectName";
    public static final String PROJECT_URL_STRING = "projectURL";
    public static final String PROJECT_LABEL_STRING = "projectLabel";
    public static final String RISK_DATE_LABEL_STRING = "riskDateLabel";
    public static final String UPDATED_LABEL = "updatedLabel";
    public static final String RED_TASKS_LABEL_STRING = "redTasksLabel";
    public static final String GREEN_TASKS_LABEL_STRING = "greenTasksLabel";
    public static final String YELLOW_TASKS_LABEL_STRING = "yellowTasksLabel";
    public static final String RED_TASKS_VALUE_STRING = "redTasksValue";
    public static final String GREEN_TASKS_VALUE_STRING = "greenTasksValue";
    public static final String YELLOW_TASKS_VALUE_STRING = "yellowTasksValue";
    public static final String DATE_STRING = "date";
    public static final String UPDATE_DATE_STRING = "updateDate";
    public static final String UPDATED_TASK_STRING = "updatedTask";
    public static final String UPDATED_TASK_URL_STRING = "updatedTaskUrl";
    public static final String OVERLOAD_COMMENT_MULTI_STRING = "overloadCommentMulti";
    public static final String OVERLOAD_COMMENT_MULTI_2_STRING = "overloadCommentMulti_2";
    public static final String OVERLOAD_COMMENT_SINGLE_STRING = "overloadCommentSingle";
    public static final String MATRIX_STRING = "matrix";
    public static final String CONSEQUENCE_LABEL_STRING = "consequenceLabel";
    public static final String PROBABILITY_LABEL_STRING = "probabilityLabel";
    public static final String MATRIX_TITLE = "matrixTitle";
    public static final String MATRIX_TEMPLATE = "matrixTemplate";
    public static final String TITLE_LABEL_STRING = "titleLabel";
    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat UPDATE_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private I18nResolver i18nResolver;
    private WebResourceUrlProvider webResourceUrlProvider;

    public String renderTemplate(ProjectOrFilter projectOrFilter, String matrixTitle, String matrixTemplate, RiskIssuesModel riskIssuesModel){

            Map<String, Object> params = new HashMap<String, Object>();

            String title = projectOrFilter.getName();
            String url = "";

            if(projectOrFilter.isFilter()){
                url = webResourceUrlProvider.getBaseUrl() + "/browse/?filter=" + projectOrFilter.getId();
            }

            if(projectOrFilter.isProject()){
                url = webResourceUrlProvider.getBaseUrl() + "/browse/?project=" + projectOrFilter.getId();
            }

            params.put(PROBABILITY_LABEL_STRING, i18nResolver.getText("risk.management.matrix.probability_label"));
            params.put(CONSEQUENCE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.consequence_label"));
            params.put(MATRIX_SIZE_STRING, RiskIssuesModel.MATRIX_SIZE);
            params.put(PROJECT_NAME_STRING, title);
            params.put(PROJECT_URL_STRING, url);
            params.put(UPDATED_LABEL, i18nResolver.getText("risk.management.matrix.updated_label"));
            params.put(RISK_DATE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.risk_date_label"));
            params.put(PROJECT_LABEL_STRING, i18nResolver.getText("risk.management.matrix.project_label"));
            params.put(RED_TASKS_VALUE_STRING, riskIssuesModel.getRedTasks());
            params.put(GREEN_TASKS_VALUE_STRING, riskIssuesModel.getGreenTasks());
            params.put(YELLOW_TASKS_VALUE_STRING, riskIssuesModel.getYellowTasks());
            params.put(RED_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.red_tasks_label"));
            params.put(GREEN_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.green_tasks_label"));
            params.put(YELLOW_TASKS_LABEL_STRING, i18nResolver.getText("risk.management.matrix.yellow_tasks_label"));
            params.put(DATE_STRING, DATE_FORMATTER.format(new Date()));
            params.put(OVERLOAD_COMMENT_MULTI_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_multi"));
            params.put(OVERLOAD_COMMENT_MULTI_2_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_multi_2"));
            params.put(OVERLOAD_COMMENT_SINGLE_STRING, i18nResolver.getText("risk.management.matrix.overload_comment_single"));
            params.put(MATRIX_STRING, riskIssuesModel.getListOfRows());
            params.put(MATRIX_TITLE,matrixTitle);
            params.put(MATRIX_TEMPLATE,matrixTemplate);
            params.put(TITLE_LABEL_STRING, i18nResolver.getText("risk.management.matrix.title_label"));
            Issue lastUpdatedIssue = riskIssuesModel.getLastUpdatedIssue();
            if(lastUpdatedIssue != null) {
                params.put(UPDATE_DATE_STRING, UPDATE_DATE_FORMATTER.format(new Date(lastUpdatedIssue.getUpdated().getTime())));
                params.put(UPDATED_TASK_STRING, lastUpdatedIssue.getKey());
                params.put(UPDATED_TASK_URL_STRING, webResourceUrlProvider.getBaseUrl() + "/browse/" + lastUpdatedIssue.getKey());
            }
            VelocityManager velocityManager = new DefaultVelocityManager();

            logger.info("generateMatrix: Everything went okay. Returnung velocity Template.");
            return velocityManager.getBody("templates/", "matrixTemplate.vm", "UTF-8", params);
    }

    public I18nResolver getI18nResolver() {
        return i18nResolver;
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public WebResourceUrlProvider getWebResourceUrlProvider() {
        return webResourceUrlProvider;
    }

    public void setWebResourceUrlProvider(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }
}
