package net.amg.jira.plugins.services;

import com.atlassian.query.Query;

/**
 * Created by adam on 08.05.15.
 */
public interface ImpactPropability {

    double getMaxPropability(Query query);
}
