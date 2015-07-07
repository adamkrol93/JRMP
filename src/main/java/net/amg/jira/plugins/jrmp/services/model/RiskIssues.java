package net.amg.jira.plugins.jrmp.services.model;

import com.atlassian.jira.issue.Issue;
import net.amg.jira.plugins.jrmp.velocity.Row;
import net.amg.jira.plugins.jrmp.velocity.Task;

import java.util.List;

/**
 * Created by yahro01 on 7/6/15.
 */
public class RiskIssues {
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
