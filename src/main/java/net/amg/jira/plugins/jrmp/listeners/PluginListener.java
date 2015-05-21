package net.amg.jira.plugins.jrmp.listeners;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
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
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
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
public class PluginListener implements LifecycleAware  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //Constants:
    public static final String CUSTOMFIELDTYPES_FLOAT = "com.atlassian.jira.plugin.system.customfieldtypes:float";
    public static final String CUSTOMFIELDTYPES_EXACTNUMBER = "com.atlassian.jira.plugin.system.customfieldtypes:exactnumber";

    public static final String RISK_CONSEQUENCE_TEXT_CF = "Risk Consequence";
    public static final String RISK_PROBABILITY_TEXT_CF = "Risk Probability";
    public static final String RISK_ISSUE_TYPE = "Risk";

    @Override
    public void onStart() {
        logger.info("Starting JIRA Risk Management plugin configuration!");
        final IssueTypeSchemeManager issueTypeSchemeManager = ComponentAccessor.getIssueTypeSchemeManager();
        final CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        final FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager();
        final ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
        IssueType riskIssueType = null;
        IssueConstant risk = constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE);
        if(risk != null)
        {
            riskIssueType = constantsManager.getIssueTypeObject(risk.getId());
        } else {
            try {
                riskIssueType = constantsManager.insertIssueType(RISK_ISSUE_TYPE, 0L, null, "Risk in projects", "/images/icons/issuetypes/delete.png");
            } catch (CreateException e) {
                logger.info("Couldn't create Risk Issue type: " + e.getMessage(), e);
            }
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
                riskConsequenceCustomField = customFieldManager.createCustomField(RISK_CONSEQUENCE_TEXT_CF, RISK_CONSEQUENCE_TEXT_CF,
                        customFieldManager.getCustomFieldType(CUSTOMFIELDTYPES_FLOAT),
                        customFieldManager.getCustomFieldSearcher(CUSTOMFIELDTYPES_EXACTNUMBER),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskConsequenceCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskConsequenceCustomField.getId());
                }
            }
            if(customFieldManager.getCustomFieldObjectByName(RISK_PROBABILITY_TEXT_CF) == null) {
                riskProbabilityCustomField = customFieldManager.createCustomField(RISK_PROBABILITY_TEXT_CF, RISK_PROBABILITY_TEXT_CF,
                        customFieldManager.getCustomFieldType(CUSTOMFIELDTYPES_FLOAT),
                        customFieldManager.getCustomFieldSearcher(CUSTOMFIELDTYPES_EXACTNUMBER),
                        contexts, issueTypes);
                if (!defaultScreen.containsField(riskProbabilityCustomField.getId())) {
                    FieldScreenTab firstTab = defaultScreen.getTab(0);
                    firstTab.addFieldScreenLayoutItem(riskProbabilityCustomField.getId());
                }
            }
        } catch (GenericEntityException e) {
            logger.info("Couldn't create risk Custom fields",e);
        } catch (NullPointerException e) {
            logger.info("Couldn't create risk Custom fields:" + e.getMessage(),e);
        }
    }
}
