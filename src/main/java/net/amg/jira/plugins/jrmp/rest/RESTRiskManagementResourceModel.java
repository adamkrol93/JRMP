package net.amg.jira.plugins.jrmp.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class RESTRiskManagementResourceModel {

    @XmlElement(name = "value")
    private String message;

    public RESTRiskManagementResourceModel() {
    }

    public RESTRiskManagementResourceModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}