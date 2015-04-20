jQuery.namespace("AMG.jrmp");
AMG.jrmp.init = function (args) {
    var gadget = AJS.Gadget({
        baseUrl: args.baseUrl,
        useOauth: "/rest/gadget/1.0/currentUser",
        config: {
            descriptor: function (args) {
                var gadget = this;
                return {
                    theme: "long-label",
                    //TODO: To jest tylko test pól trzeba je ogarnąć
                    fields: [
                        {
                            userpref: "Template",
                            value: gadget.getPref("Template"),
                            label: gadget.getMsg("risk.management.gadget.template.label"),
                            description: gadget.getMsg("risk.management.gadget.template.description"),
                            type: "text"
                        },
                        {
                            userpref: "Filter",
                            value: gadget.getPref("Filter"),
                            label: gadget.getMsg("risk.management.gadget.filter.label"),
                            description: gadget.getMsg("risk.management.gadget.filter.default"),
                            type: "text"
                        },
                        {
                            userpref: "Date",
                            label: gadget.getMsg("risk.management.gadget.relativeDate.label"),
                            description: gadget.getMsg("risk.management.gadget.relativeDate.description"),
                            type: "select",
                            options: [
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.today")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.yesterday")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.oneWeekAgo")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.twoWeeksAgo")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.oneMonthAgo")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.threeMonthsAgo")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.sixMonthsAgo")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.relativeDate.option.oneYearAgo")
                                }
                            ]
                        },
                        {
                            userpref: "Title",
                            value: gadget.getPref("Title"),
                            label: gadget.getMsg("risk.management.gadget.userTitle.label"),
                            description: gadget.getMsg("risk.management.gadget.userTitle.description"),
                            type: "text"
                        },
                        {
                            userpref: "RefreshInterval",
                            label: gadget.getMsg("risk.management.gadget.refreshInterval.label"),
                            description: gadget.getMsg("risk.management.gadget.refreshInterval.description"),
                            type: "select",
                            options: [
                                {
                                    label: gadget.getMsg("risk.management.gadget.refreshInterval.option.never")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.refreshInterval.option.fifteenMinutes")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.refreshInterval.option.thirtyMinutes")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.refreshInterval.option.oneHour")
                                },
                                {
                                    label: gadget.getMsg("risk.management.gadget.refreshInterval.option.twoHours")
                                }

                            ]
                        },
                        AJS.gadget.fields.nowConfigured()
                    ]

                }
            }
        },
        args: [
            {
                key: "user",
                ajaxOptions: function () {
                    return {
                        url: "/rest/gadget/1.0/currentUser"
                    };
                }
            },
            {
                key: "issuesData",
                ajaxOptions: function () {
                    return {
                        url: "/rest/RESTRiskManagementResource/1.0/issues.json"
                    };
                }
            },
            {
                key: "requestedIssuesData",
                ajaxOptions: function () {
                    return {
                        url: "/rest/RESTRiskManagementResource/1.0/issues/byProjectName/" + encodeURI(gadget.getPref("Project")),
                        error: function (msg) {
                            //Tutaj trzeba dać konkretną informację
                            gadget.showMessage("error", gadget.getMsg("risk.management.gadget.title"), true, true);
                        }
                    };
                }
            }
        ]


});
}