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
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import net.amg.jira.plugins.jrmp.listeners.PluginStartupListener;
import net.amg.jira.plugins.jrmp.services.model.DateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jonatan on 03.06.15.
 * @author Jonatan Gostyński
 * @author Adam Król
 * @author augustynwilk@gmail.com
 */
@Service
public class QueryBuilderImpl implements QueryBuilder {
    private static final List<String> QUERY_CLAUSES = Arrays.asList("created", "issuetype", PluginStartupListener.RISK_CONSEQUENCE_TEXT_CF,
            PluginStartupListener.RISK_PROBABILITY_TEXT_CF);

    @Autowired
    private CustomFieldManager customFieldManager;

    QueryBuilderImpl(){}

    @Override
    public Query buildQuery(Query query,DateModel dateModel) {
        JqlQueryBuilder builder = getJqlQueryBuilder(query);


        builder.where().and().issueType(PluginStartupListener.RISK_ISSUE_TYPE).and()
                .sub().customField(customFieldManager.getCustomFieldObjectByName(PluginStartupListener.RISK_CONSEQUENCE_TEXT_CF).getIdAsLong()).isNotEmpty()
                .or().customField(customFieldManager.getCustomFieldObjectByName(PluginStartupListener.RISK_PROBABILITY_TEXT_CF).getIdAsLong()).isNotEmpty().endsub();

        if(!dateModel.equals(DateModel.TODAY))
        {
            builder.where().and().created().ltEq(dateModel.getBeforeValue());
        }
        return builder.buildQuery();
    }

    private JqlQueryBuilder getJqlQueryBuilder(Query query) {

        for (Clause clause : query.getWhereClause().getClauses()) {
            if (QUERY_CLAUSES.contains(clause.getName())) {
                query.getWhereClause().getClauses().remove(clause);
            }
        }

        return JqlQueryBuilder.newBuilder(query);
    }

    @Override
    public Query buildFilterQuery(int riskProbability, int riskConsequence,Query query, DateModel dateModel) {


        JqlQueryBuilder builder = getJqlQueryBuilder(query);

        builder.where().and().issueType(PluginStartupListener.RISK_ISSUE_TYPE)
                .and().customField(customFieldManager.getCustomFieldObjectByName(PluginStartupListener.RISK_CONSEQUENCE_TEXT_CF).getIdAsLong()).eq((long) riskConsequence)
                .and().customField(customFieldManager.getCustomFieldObjectByName(PluginStartupListener.RISK_PROBABILITY_TEXT_CF).getIdAsLong()).eq((long) riskProbability);

        if(!dateModel.equals(DateModel.TODAY))
        {
            builder.where().and().created().ltEq(dateModel.getBeforeValue());
        }
        return builder.buildQuery();
    }

    public void setCustomFieldManager(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }
}
