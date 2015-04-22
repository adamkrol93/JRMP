jQuery.namespace("AMG.jrmp");
AMG.jrmp.init = function (args) {
    AJS.log("Startuję");
    var base = args.baseUrl;
    var gadget = AJS.Gadget({
            baseUrl: base,
            useOauth: "/rest/gadget/1.0/currentUser",
            config: {
                descriptor: function () {
                    return {
                        theme: "long-label",
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
                            {
                                userpref: "Filter",
                                value: this.getPref("Filter"),
                                label: this.getMsg("risk.management.gadget.filter.label"),
                                description: this.getMsg("risk.management.gadget.filter.default"),
                                type: "text"
                            },
                            {
                                userpref: "Date",
                                value: this.getPref("Date"),
                                label: this.getMsg("risk.management.gadget.relativeDate.label"),
                                description: this.getMsg("risk.management.gadget.relativeDate.description"),
                                type: "select",
                                options:[
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.today")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.yesterday")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneWeekAgo")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.twoWeeksAgo")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneMonthAgo")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.threeMonthsAgo")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.sixMonthsAgo")
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneYearAgo")
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
            // AJS.log("Przechodzę do view")

            view: {
                enableReload: true,
                onResizeAdjustHeight: true,
                template: function (args) {
                    var gadget = this;
                    var filters = args.favFilters.filters;

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
    AJS.log("Pomyślnie skończyłem");
}