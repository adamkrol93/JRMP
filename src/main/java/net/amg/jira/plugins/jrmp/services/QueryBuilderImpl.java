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
package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import net.amg.jira.plugins.jrmp.listeners.PluginListener;

import java.util.Iterator;

/**
 * Created by jonatan on 03.06.15.
 */
public class QueryBuilderImpl implements QueryBuiler {

    private CustomFieldManager customFieldManager;

    public QueryBuilderImpl(CustomFieldManager customFieldManager) {
        this.customFieldManager = customFieldManager;
    }

    @Override
    public Query buildQuery(Query query) {
        Iterator<Clause> iterator = query.getWhereClause().getClauses().iterator();

        while(iterator.hasNext()) {

            Clause c = iterator.next();
            if(c.getName().contains("issuetype") || c.getName().contains(PluginListener.RISK_CONSEQUENCE_TEXT_CF) || c.getName().contains(PluginListener.RISK_PROBABILITY_TEXT_CF))
            {
                query.getWhereClause().getClauses().remove(c);
            }
        }

        JqlQueryBuilder builder = JqlQueryBuilder.newBuilder(query);


        builder.where().and().issueType(PluginListener.RISK_ISSUE_TYPE).and()
                .sub().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_CONSEQUENCE_TEXT_CF).getIdAsLong()).isNotEmpty()
                .or().customField(customFieldManager.getCustomFieldObjectByName(PluginListener.RISK_PROBABILITY_TEXT_CF).getIdAsLong()).isNotEmpty().endsub();

        return builder.buildQuery();
    }
}
