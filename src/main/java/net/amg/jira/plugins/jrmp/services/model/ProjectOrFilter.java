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
package net.amg.jira.plugins.jrmp.services.model;

import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.query.Query;
import org.ofbiz.core.entity.GenericValue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jonatan on 31.05.15.
 */
@Service
public class ProjectOrFilter {
    public static final String PROJECT = "project";
    public static final String FILTER = "filter";
    private Query query;
    private String name;
    private String id;
    private boolean isFilter = false;
    private boolean isProject = false;
    private String projectOrFilter;
    private OfBizDelegator ofBizDelegator;

    private boolean valid = false;

    private ProjectOrFilter() {

    }


    public static ProjectOrFilter createProjectOrFilter(String projectOrFilter, OfBizDelegator ofBizDelegator) {
        ProjectOrFilter projectOrFilterObject = new ProjectOrFilter();
        projectOrFilterObject.projectOrFilter = projectOrFilter;
        projectOrFilterObject.ofBizDelegator = ofBizDelegator;
        projectOrFilterObject.initProjectOrFilter();
        return projectOrFilterObject;
    }


    private void initProjectOrFilter() {
        String type;
        if (projectOrFilter.contains("-")) {
            type = projectOrFilter.split("-")[0];
            id = projectOrFilter.split("-")[1];
        } else {
            return;
        }

        List<GenericValue> result = null;
        Map<String, Object> mapWithIdToFindProjectOrFilterName = new HashMap<String, Object>();
        mapWithIdToFindProjectOrFilterName.put("id", id);

        if (FILTER.equals(type)) {
            query = getQueryFilter(id);
            result = ofBizDelegator.findByAnd("SearchRequest", mapWithIdToFindProjectOrFilterName);
            isFilter = true;
        }
        if (PROJECT.equals(type)) {
            query = getQueryProject(id);
            result = ofBizDelegator.findByAnd("Project", mapWithIdToFindProjectOrFilterName);
            isProject = true;
        }

        if (result != null && !result.isEmpty()) {
            name = result.get(0).getString("name");
        } else {
            name = "";
        }

        this.valid = true;
    }

    private Query getQueryFilter(String filter) {
        JqlClauseBuilder subjectBuilder = JqlQueryBuilder.newClauseBuilder().savedFilter(filter);
        return subjectBuilder.buildQuery();
    }

    private Query getQueryProject(String project) {
        JqlClauseBuilder subjectBuilder = JqlQueryBuilder.newClauseBuilder().project(project);
        return subjectBuilder.buildQuery();
    }

    public Query getQuery() {
        return query;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isProject() {
        return isProject;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "ProjectOrFilter{" +
                "query=" + query +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", isFilter=" + isFilter +
                ", isProject=" + isProject +
                ", valid=" + valid +
                ", projectOrFilter='" + projectOrFilter + '\'' +
                '}';
    }
}
