package net.amg.jira.plugins.rest;

import com.atlassian.jira.rest.api.util.ValidationError;
import org.apache.commons.collections.KeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam on 24.04.15.
 */
public class ErrorCollection {
    private Collection<String> errorMessages;
    private Collection<ValidationError> errors;
    private Map<String,String> parameters;

    public ErrorCollection() {
        errorMessages = new ArrayList<String>(1);
        errors = new ArrayList<ValidationError>(7);
        parameters = new HashMap<String, String>(4);
    }

    public Collection<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Collection<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Collection<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<ValidationError> errors) {
        this.errors = errors;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addError(String field, String errorMsg) {
        errors.add(new ValidationError(field, errorMsg));
    }
    public void addError(String field, String errorMsg, String param) {
        errors.add(new ValidationError(field, errorMsg, param));
    }

    public boolean hasAnyErrors() {
        return !errors.isEmpty() || !errorMessages.isEmpty();
    }

    public void addErrorMessage(String errorMsg) {
        errorMessages.add(errorMsg);
    }

    public boolean hasErrorForField(String fieldId) {
        if (errors.isEmpty()) {
            return false;
        } else {
            for (ValidationError error : errors) {
                if (error.getField().equals(fieldId))
                    return true;
            }
        }
        return false;
    }
}
