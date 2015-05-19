package net.amg.jira.plugins.velocity;

import com.atlassian.query.Query;

/**
 * @author Adam Król
 */
public interface MatrixGenerator {
    String generateMatrix(int size, Query query);
}
