package net.amg.jira.plugins.jrmp.services;

import com.atlassian.query.Query;

/**
 * Created by adam on 08.05.15.
 */
public interface ImpactProbability {

    double getMaxProbability(Query query);
}
