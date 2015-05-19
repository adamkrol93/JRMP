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
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Listenera, która ustawia odpowiednie typy pól oraz zgłoszeń aby można było działać na odpowiednich danych.
 * @author Adam Król
 */
@Component
public class PluginListener implements org.osgi.framework.BundleActivator  {


    public static final String RISK_CONSEQUENCE_TEXT_CF = "Risk Consequence";
    public static final String RISK_PROBABILITY_TEXT_CF = "Risk Probability";
    public static final String RISK_ISSUE_TYPE = "Risk";

    private IssueTypeSchemeManager issueTypeSchemeManager;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void start(BundleContext context) throws Exception {
        logger.info("Starting Jira Risk Management plugin!");

        final CustomFieldManager customFieldManager = (CustomFieldManager) context.getService(context.getServiceReference("com.atlassian.jira.issue.CustomFieldManager"));
        final FieldScreenManager fieldScreenManager = (FieldScreenManager) context.getService(context.getServiceReference("com.atlassian.jira.issue.fields.screen.FieldScreenManager"));
        final ConstantsManager constantsManager = (ConstantsManager) context.getService(context.getServiceReference("com.atlassian.jira.config.ConstantsManager"));
        final IssueTypeSchemeManager issueTypeSchemeManager = (IssueTypeSchemeManager) context.getService(context.getServiceReference("com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager"));
        final IssueType riskIssueType;
        IssueConstant risk = constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE);
        if(risk != null)
        {
            riskIssueType = constantsManager.getIssueTypeObject(risk.getId());
        }else {
            riskIssueType = constantsManager.insertIssueType(RISK_ISSUE_TYPE, 0L, null, "Risk in projects", "/images/icons/issuetypes/delete.png");
        }

        final List<GenericValue> issueTypes = new ArrayList<GenericValue>();
        issueTypes.add(riskIssueType.getGenericValue());

        final List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
        contexts.add(GlobalIssueContext.getInstance());

        issueTypeSchemeManager.addOptionToDefault(riskIssueType.getId());
        final CustomField riskConsequenceCustomField;
        final CustomField riskProbabilityCustomField;
        try {
            FieldScreen defaultScreen = fieldScreenManager.getFieldScreen(FieldScreen.DEFAULT_SCREEN_ID);
            if(customFieldManager.getCustomFieldObjectByName(RISK_CONSEQUENCE_TEXT_CF) == null) {
                riskConsequenceCustomField = customFieldManager.createCustomField(RISK_CONSEQUENCE_TEXT_CF, "Risk Consequence",
                        customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                        customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskConsequenceCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskConsequenceCustomField.getId());
                }
            }
            if(customFieldManager.getCustomFieldObjectByName(RISK_PROBABILITY_TEXT_CF) == null) {
                riskProbabilityCustomField = customFieldManager.createCustomField(RISK_PROBABILITY_TEXT_CF, "Risk Probability",
                        customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                        customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskProbabilityCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskProbabilityCustomField.getId());
                }
            }


        } catch (GenericEntityException e) {
            logger.info("Couldn't create risk Custom fields",e);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //Do nothing. Trzeba spytać właściciela biznesowego czy usuwać customfield czy nie.
    }
}
