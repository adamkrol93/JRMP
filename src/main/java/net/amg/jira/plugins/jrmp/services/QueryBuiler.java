package net.amg.jira.plugins.jrmp.services;

import com.atlassian.query.Query;

/**
 * Created by jonatan on 03.06.15.
 */
public interface QueryBuiler {
    Query buildQuery(Query query);
}
