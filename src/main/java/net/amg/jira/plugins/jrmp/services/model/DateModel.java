package net.amg.jira.plugins.jrmp.services.model;

/**
 * @author Adam Król
 */
public enum DateModel {
    TODAY("0"),YESTERDAY("-1d"),WEEK_AGO("-1w"),TWO_WEEKS_AGO("-2w"),MONTH_AGO("-4w"),THREE_MONTHS_AGO("-12w"),SIX_MONTHS_AGO("-180d"),YEAR_AGO("-365d");

    private String beforeValue;

    DateModel(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getBeforeValue() {
        return beforeValue;
    }
}
