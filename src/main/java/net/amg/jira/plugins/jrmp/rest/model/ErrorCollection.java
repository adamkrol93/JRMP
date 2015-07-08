/*
 * Licensed to AMG.net under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 *
 * AMG.net licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.amg.jira.plugins.jrmp.rest.model;

import com.atlassian.jira.rest.api.util.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Król
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
