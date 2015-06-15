package net.amg.jira.plugins.jrmp.services.model;

/**
 * @author Adam Król
 */
public enum DateModel {
    TODAY("0"),YESTERDAY("-1d"),WEEK_AGO("-1w"),TWO_WEEKS_AGO("-2w"),MONTH_AGO("-1m"),THREE_MONTHS_AGO("-3m"),SIX_MONTHS_AGO("-6m"),YEAR_AGO("-1y");

    private String beforeValue;

    DateModel(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getBeforeValue() {
        return beforeValue;
    }
}
