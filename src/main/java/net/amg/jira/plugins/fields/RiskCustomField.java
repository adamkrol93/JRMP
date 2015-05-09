package net.amg.jira.plugins.fields;

/**
 * Created by adam on 08.05.15.
 */

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.util.I18nHelper;
import com.opensymphony.module.propertyset.PropertySet;
import org.ofbiz.core.entity.GenericValue;

import java.util.Locale;

public class RiskCustomField implements IssueType {
    @Override
    public boolean isSubTask() {
        return false;
    }

    @Override
    public GenericValue getGenericValue() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String s) {

    }

    @Override
    public Long getSequence() {
        return null;
    }

    @Override
    public void setSequence(Long aLong) {

    }

    @Override
    public String getCompleteIconUrl() {
        return null;
    }

    @Override
    public String getIconUrl() {
        return null;
    }

    @Override
    public String getIconUrlHtml() {
        return null;
    }

    @Override
    public void setIconUrl(String s) {

    }

    @Override
    public String getNameTranslation() {
        return null;
    }

    @Override
    public String getDescTranslation() {
        return null;
    }

    @Override
    public String getNameTranslation(String s) {
        return null;
    }

    @Override
    public String getDescTranslation(String s) {
        return null;
    }

    @Override
    public String getNameTranslation(I18nHelper i18nHelper) {
        return null;
    }

    @Override
    public String getDescTranslation(I18nHelper i18nHelper) {
        return null;
    }

    @Override
    public void setTranslation(String s, String s1, String s2, Locale locale) {

    }

    @Override
    public void deleteTranslation(String s, Locale locale) {

    }

    @Override
    public PropertySet getPropertySet() {
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
