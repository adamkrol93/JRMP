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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Listenera, która ustawia odpowiednie typy pól oraz zgłoszeń aby można było działać na odpowiednich danych.
 * @author Adam Król
 */

public class PluginListener implements DisposableBean, InitializingBean{


    public static final String RISK_CONSEQUENCE_TEXT_CF = "Risk Consequence";
    public static final String RISK_PROBABILITY_TEXT_CF = "Risk Probability";
    public static final String RISK_ISSUE_TYPE = "Risk";
    private final CustomFieldManager customFieldManager;
    private final FieldScreenManager fieldScreenManager;
    private final ConstantsManager constantsManager;
    private final IssueTypeSchemeManager issueTypeSchemeManager;

    public PluginListener(CustomFieldManager customFieldManager, FieldScreenManager fieldScreenManager, ConstantsManager constantsManager, IssueTypeSchemeManager issueTypeSchemeManager) {
        this.customFieldManager = customFieldManager;
        this.fieldScreenManager = fieldScreenManager;
        this.constantsManager = constantsManager;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
    }

    @Override
    public void destroy() throws Exception {

        CustomField riskConsequenceCustomField = this.customFieldManager.getCustomFieldObjectByName(RISK_CONSEQUENCE_TEXT_CF);

        if (riskConsequenceCustomField != null) {
            this.customFieldManager.removeCustomField(riskConsequenceCustomField);
        }

        CustomField riskProbabilityCustomField = this.customFieldManager.getCustomFieldObjectByName(RISK_PROBABILITY_TEXT_CF);

        if (riskProbabilityCustomField != null) {
            this.customFieldManager.removeCustomField(riskProbabilityCustomField);
        }

        IssueConstant constant = constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE);
        if(constant != null)
        {
            issueTypeSchemeManager.removeOptionFromAllSchemes(constant.getId());
            constantsManager.removeIssueType(constant.getId());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        IssueType riskIssueType = constantsManager.insertIssueType(RISK_ISSUE_TYPE, 0L, null, "Risk in projects", "/images/icons/issuetypes/delete.png");

        List<GenericValue> issueTypes = new ArrayList<GenericValue>();
        issueTypes.add(riskIssueType.getGenericValue());

        List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
        contexts.add(GlobalIssueContext.getInstance());

        issueTypeSchemeManager.addOptionToDefault(riskIssueType.getId());
        CustomField riskConsequenceCustomField = null;
        CustomField riskProbabilityCustomField = null;
        try {
            riskConsequenceCustomField = this.customFieldManager.createCustomField(RISK_CONSEQUENCE_TEXT_CF, "Risk Consequence",
                    this.customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                    this.customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                    contexts, issueTypes);
            riskProbabilityCustomField = this.customFieldManager.createCustomField(RISK_PROBABILITY_TEXT_CF, "Risk Probability",
                    this.customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:float"),
                    this.customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:exactnumber"),
                    contexts, issueTypes);


        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        FieldScreen defaultScreen = fieldScreenManager.getFieldScreen(FieldScreen.DEFAULT_SCREEN_ID);
        if (!defaultScreen.containsField(riskConsequenceCustomField.getId())) {
            FieldScreenTab firstTab = defaultScreen.getTab(0);
            firstTab.addFieldScreenLayoutItem(riskConsequenceCustomField.getId());
        }
        if (!defaultScreen.containsField(riskProbabilityCustomField.getId())) {
            FieldScreenTab firstTab = defaultScreen.getTab(0);
            firstTab.addFieldScreenLayoutItem(riskProbabilityCustomField.getId());
        }


    }
}
