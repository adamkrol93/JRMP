/*
 * Licensed to AMG.net under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * AMG.net licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.amg.jira.plugins.jrmp.listeners;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueConstant;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.PluginException;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import net.amg.jira.plugins.jrmp.services.model.RiskIssues;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Klasa Listenera, która ustawia odpowiednie typy pól oraz zgłoszeń aby można było działać na odpowiednich danych.
 * @author Adam Król
 */
@Component
public class PluginStartupListener implements LifecycleAware  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //Constants:
    private static final String CUSTOMFIELDTYPES_FLOAT = "com.atlassian.jira.plugin.system.customfieldtypes:select";
    private static final String CUSTOMFIELDTYPES_EXACTNUMBER = "com.atlassian.jira.plugin.system.customfieldtypes:multiselectsearcher";

    public static final String RISK_ISSUE_TYPE = "Risk";
    public static final String RISK_CONSEQUENCE_TEXT_CF = "Risk Consequence";
    public static final String RISK_PROBABILITY_TEXT_CF = "Risk Probability";
    //Dummy constructor required for Spring dependency injection.
    public PluginStartupListener() {};
    @Autowired private ConstantsManager constantsManager;
    @Autowired private CustomFieldManager customFieldManager;
    @Autowired private FieldScreenManager fieldScreenManager;
    @Autowired private IssueTypeSchemeManager issueTypeSchemeManager;
    @Autowired private OptionsManager optionsManager;

    public void addOptionToCustomField(CustomField customField, String value) {
        if (customField != null) {
            List<FieldConfigScheme> schemes = customField
                    .getConfigurationSchemes();
            if (schemes != null && !schemes.isEmpty()) {
                FieldConfigScheme sc = schemes.get(0);
                Map configs = sc.getConfigsByConfig();
                if (configs != null && !configs.isEmpty()) {
                    FieldConfig config = (FieldConfig) configs.keySet().iterator().next();
                    optionsManager.createOption(config, null, Long.valueOf(value), value);
                }
            }
        }
    }

    @Override
    public void onStart() {
        logger.info("Starting JIRA Risk Management plugin configuration!");

        final IssueType riskIssueType;

        IssueConstant risk = constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE);

        if(risk != null) {
            riskIssueType = constantsManager.getIssueTypeObject(risk.getId());
        } else {
            try {
                riskIssueType = constantsManager.insertIssueType(RISK_ISSUE_TYPE, 0L, null, "Risk in projects", "/images/icons/issuetypes/delete.png");
                issueTypeSchemeManager.addOptionToDefault(riskIssueType.getId());
            } catch (CreateException e) {
                logger.error("Couldn't create Risk Issue type: " + e.getMessage(), e);
                throw new PluginException("Couldn't create IssueType, stopping plugin creation",e);
            }
        }

        final List<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(riskIssueType);

        final List<JiraContextNode> contexts = new ArrayList<JiraContextNode>();
        contexts.add(GlobalIssueContext.getInstance());

        try {
            FieldScreen defaultScreen = fieldScreenManager.getFieldScreen(FieldScreen.DEFAULT_SCREEN_ID);
            createRiskField(RISK_CONSEQUENCE_TEXT_CF, issueTypes, contexts, defaultScreen);
            createRiskField(RISK_PROBABILITY_TEXT_CF, issueTypes, contexts, defaultScreen);
        } catch (GenericEntityException e) {
            logger.error("Couldn't create risk Custom fields : " + e.getMessage(), e);
            throw new PluginException("GenericEntityException. Failed plugin startup configuration",e);
        } catch (NullPointerException e) {
            logger.error("Couldn't create risk Custom fields:" + e.getMessage(), e);
            throw new PluginException("NullPointerException. Failed plugin startup configuration",e);
        }
    }

    /**
     * Configures {@link #RISK_CONSEQUENCE_TEXT_CF} or {@link #RISK_PROBABILITY_TEXT_CF}, by adding their default values.
     * @param riskField - risk customfield name.
     * @param issueTypes - List of IssueTypes to be associated with. {@link #RISK_ISSUE_TYPE} by default.
     * @param contexts - JIRA Context
     * @param defaultScreen - default screen name. It is screen that CustomFields will be configured for.
     * @throws GenericEntityException
     */
    private void createRiskField(final String riskField, List<IssueType> issueTypes, List<JiraContextNode> contexts, FieldScreen defaultScreen) throws GenericEntityException {
        final CustomField riskCustomField;
        if(customFieldManager.getCustomFieldObjectByName(riskField) == null) {
            riskCustomField = customFieldManager.createCustomField(riskField, riskField,
                    customFieldManager.getCustomFieldType(CUSTOMFIELDTYPES_FLOAT),
                    customFieldManager.getCustomFieldSearcher(CUSTOMFIELDTYPES_EXACTNUMBER),
                    contexts, convertToGenericValue(issueTypes));
            if (!defaultScreen.containsField(riskCustomField.getId())) {
                FieldScreenTab firstTab = defaultScreen.getTab(0);
                firstTab.addFieldScreenLayoutItem(riskCustomField.getId());
            }
            for(int i = 1; i <= RiskIssues.MATRIX_SIZE; i++) {
                addOptionToCustomField(riskCustomField,String.valueOf(i));
            }
        }
    }

    private List<GenericValue> convertToGenericValue(List<IssueType> issueTypes) {
        final IssueType issueTypeObject = constantsManager.getIssueTypeObject(constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, RISK_ISSUE_TYPE).getId());
        List<GenericValue> genericList = new ArrayList<>();
        genericList.add(constantsManager.getConstantByNameIgnoreCase(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, issueTypeObject.getName()).getGenericValue());
        return genericList;
    }

}
