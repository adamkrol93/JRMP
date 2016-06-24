/*
 * Licensed to author under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * Author licenses this file to you under the Apache License,
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

import com.atlassian.jira.issue.Issue;
import net.amg.jira.plugins.jrmp.velocity.Row;
import net.amg.jira.plugins.jrmp.velocity.Task;

import java.util.List;

/**
 * Created by yahro01 on 7/6/15.
 */
public class RiskIssues {
    public static final int MATRIX_SIZE = 5;
    private List<Issue> issues;
    private List<Task> tasks;
    private List<Row> listOfRows;
    private Issue lastUpdatedIssue;
    private int redTasks = 0;
    private int yellowTasks = 0;
    private int greenTasks = 0;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Row> getListOfRows() {
        return listOfRows;
    }

    public void setListOfRows(List<Row> listOfRows) {
        this.listOfRows = listOfRows;
    }

    public Issue getLastUpdatedIssue() {
        return lastUpdatedIssue;
    }

    public void setLastUpdatedIssue(Issue lastUpdatedIssue) {
        this.lastUpdatedIssue = lastUpdatedIssue;
    }

    public int getRedTasks() {
        return redTasks;
    }

    public void setRedTasks(int redTasks) {
        this.redTasks = redTasks;
    }

    public int getYellowTasks() {
        return yellowTasks;
    }

    public void setYellowTasks(int yellowTasks) {
        this.yellowTasks = yellowTasks;
    }

    public int getGreenTasks() {
        return greenTasks;
    }

    public void setGreenTasks(int greenTasks) {
        this.greenTasks = greenTasks;
    }
}
