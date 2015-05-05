jQuery.namespace("AMG.jrmp");
AMG.jrmp.init = function (args) {
    var base = args.baseUrl;
    var gadget = AJS.Gadget({
            baseUrl: base,
            useOauth: "/rest/gadget/1.0/currentUser",
            config: {
                descriptor: function () {
                    var gadget = this;
                    var searchParam;
                    if (/^jql-/.test(this.getPref("Filter")) || this.getPref("isPopup") === "true"){
                        searchParam =
                        {
                            userpref: "Filter",
                            type: "hidden",
                            value: gadgets.util.unescapeString(gadget.getPref("Filter"))
                        };
                    }
                    else{
                        searchParam = AJS.gadget.fields.projectOrFilterPicker(gadget,"Filter");
                    }

                    return {
                        onResizeReload: true,
                        onResizeAdjustHeight: true,
                        action: "/rest/jira-risk-management/1.0/controller/validate",
                        theme: (function() { return gadgets.window.getViewportDimensions().width < 500 ? "top-label" : "long-label"; })(),
                        fields: [
                            {
                                userpref: "Template",
                                "class": "numField",
                                value: this.getPref("Template"),
                                label: this.getMsg("risk.management.gadget.template.label"),
                                description: this.getMsg("risk.management.gadget.template.description"),
                                type: "select",
                                options:[
                                    {
                                       label: this.getMsg("risk.management.gadget.template.option.RiskManegementMatrix")
                                    }
                                ]
                            },
                            jQuery.extend(true, {}, searchParam, {
                                label: gadget.getMsg("risk.management.gadget.filter.label"),
                                description: gadget.getMsg("risk.management.gadget.filter.default")
                            }),
                            {
                                userpref: "Date",
                                value: this.getPref("Date"),
                                label: this.getMsg("risk.management.gadget.relativeDate.label"),
                                description: this.getMsg("risk.management.gadget.relativeDate.description"),
                                type: "select",
                                options:[
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.today"),
                                        value: "1"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.yesterday"),
                                        value: "2"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneWeekAgo"),
                                        value: "3"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.twoWeeksAgo"),
                                        value: "4"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneMonthAgo"),
                                        value: "5"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.threeMonthsAgo"),
                                        value: "6"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.sixMonthsAgo"),
                                        value: "7"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneYearAgo"),
                                        value: "8"
                                    }
                                ]
                            },
                            {
                                userpref: "Title",
                                "class": "numField",
                                value: this.getPref("Title"),
                                label: this.getMsg("risk.management.gadget.userTitle.label"),
                                description: this.getMsg("risk.management.gadget.userTitle.description"),
                                type: "text"
                            },
                            AJS.gadget.fields.nowConfigured()
                        ]

                    }

                }

            },
            view: {
                enableReload: true,
                onResizeAdjustHeight: true,
                template: function (args) {
                    var gadget = this;
                    var filters;// = args.favFilters.filters;

                    if (!filters) {
                        gadget.getView().removeClass("loading").html("<p>__MSG_gadget.favourite.filters.no.favourites__</p>");
                    } else {
                        var list = AJS.$("<ul/>").attr("id", "filter-list");

                        AJS.$(filters).each(function () {
                            list.append(
                                AJS.$("<li/>").append(
                                    AJS.$("<div/>").addClass("filter-name").append(
                                        AJS.$("<a/>").attr({
                                            target: "_parent",
                                            title: gadgets.util.escapeString(this.description),
                                            href: "__ATLASSIAN_BASE_URL__/secure/IssueNavigator.jspa?mode=hide&requestId=" + this.value
                                        }).text(this.label)
                                    )
                                ).append(
                                    AJS.$("<div/>").addClass("filter-count").text(this.count)
                                ).click(function () {
                                        if (window.parent) {
                                            window.parent.location = "__ATLASSIAN_BASE_URL__/secure/IssueNavigator.jspa?mode=hide&requestId=" + this.value;
                                        } else {
                                            window.location = "__ATLASSIAN_BASE_URL__/secure/IssueNavigator.jspa?mode=hide&requestId=" + this.value;
                                        }
                                    })
                            );
                        });
                        gadget.getView().html(list);
                    }
                },
                args: [{

                }]
            }
        })
        ;
};