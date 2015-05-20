package net.amg.jira.plugins.jrmp.services;

import com.atlassian.jira.issue.Issue;
import com.atlassian.query.Query;
import net.amg.jira.plugins.jrmp.exceptions.NoIssuesFoundException;

import java.util.List;

/**
 * Usługa znajdująca wszystkie potrzebne taski w jire. Najczęściej na podstawie Query.
 * @author Adam Król
 */
public interface JRMPSearchService {

    List<Issue> getAllQualifiedIssues(Query query) throws NoIssuesFoundException;
}
