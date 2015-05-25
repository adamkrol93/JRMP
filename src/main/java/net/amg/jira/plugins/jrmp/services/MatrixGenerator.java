package net.amg.jira.plugins.jrmp.services;

import com.atlassian.query.Query;

/**
 * @author Adam Król
 */
public interface MatrixGenerator {
    String generateMatrix(Query query);
}
