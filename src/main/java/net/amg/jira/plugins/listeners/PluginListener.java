package net.amg.jira.plugins.listeners;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueConstant;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.issuetype.IssueType;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.extensions.annotation.ServiceReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Listenera, która ustawia odpowiednie typy pól oraz zgłoszeń aby można było działać na odpowiednich danych.
 * @author Adam Król
 */

public class PluginListener implements InitializingBean{


    public static final String RISK_CONSEQUENCE_TEXT_CF = "Risk Consequence";
    public static final String RISK_PROBABILITY_TEXT_CF = "Risk Probability";
    public static final String RISK_ISSUE_TYPE = "Risk";
    private CustomFieldManager customFieldManager;
    private FieldScreenManager fieldScreenManager;
    private ConstantsManager constantsManager;
    private IssueTypeSchemeManager issueTypeSchemeManager;
    private Logger logger = LoggerFactory.getLogger(getClass());

//    public PluginListener(CustomFieldManager customFieldManager, FieldScreenManager fieldScreenManager, ConstantsManager constantsManager, IssueTypeSchemeManager issueTypeSchemeManager) {
//        this.customFieldManager = customFieldManager;
//        this.fieldScreenManager = fieldScreenManager;
//        this.constantsManager = constantsManager;
//        this.issueTypeSchemeManager = issueTypeSchemeManager;
//    }


    @ServiceReference
    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    @ServiceReference
    public void setFieldScreenManager(FieldScreenManager fieldScreenManager) {
        this.fieldScreenManager = fieldScreenManager;
    }

    @ServiceReference
    public void setConstantsManager(ConstantsManager constantsManager) {
        this.constantsManager = constantsManager;
    }

    @ServiceReference
    public void setIssueTypeSchemeManager(IssueTypeSchemeManager issueTypeSchemeManager) {
        this.issueTypeSchemeManager = issueTypeSchemeManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        IssueType riskIssueType;
        IssueConstant risk = constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE);
        if(risk != null)
        {
            riskIssueType = constantsManager.getIssueTypeObject(risk.getId());
        }else {
            riskIssueType = constantsManager.insertIssueType(RISK_ISSUE_TYPE, 0L, null, "Risk in projects", "/images/icons/issuetypes/delete.png");
        }

        List<GenericValue> issueTypes = new ArrayList<GenericValue>();
        issueTypes.add(riskIssueType.getGenericValue());

        List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
        contexts.add(GlobalIssueContext.getInstance());

        issueTypeSchemeManager.addOptionToDefault(riskIssueType.getId());
        CustomField riskConsequenceCustomField = null;
        CustomField riskProbabilityCustomField = null;
        try {
            FieldScreen defaultScreen = fieldScreenManager.getFieldScreen(FieldScreen.DEFAULT_SCREEN_ID);
            if(customFieldManager.getCustomFieldObjectByName(RISK_CONSEQUENCE_TEXT_CF) == null) {
                riskConsequenceCustomField = this.customFieldManager.createCustomField(RISK_CONSEQUENCE_TEXT_CF, "Risk Consequence",
                        this.customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                        this.customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskConsequenceCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskConsequenceCustomField.getId());
                }
            }
            if(customFieldManager.getCustomFieldObjectByName(RISK_PROBABILITY_TEXT_CF) == null) {
                riskProbabilityCustomField = this.customFieldManager.createCustomField(RISK_PROBABILITY_TEXT_CF, "Risk Probability",
                        this.customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                        this.customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskProbabilityCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskProbabilityCustomField.getId());
                }
            }


        } catch (GenericEntityException e) {
            logger.info("Couldnt create risk Custom fields",e);
        }
    }
}
