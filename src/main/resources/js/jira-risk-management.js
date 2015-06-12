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
                            value: gadgets.util.unescapeString(this.getPref("Filter"))
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
                                       label: this.getMsg("risk.management.gadget.template.option.RiskManagementMatrix"),
                                        value: "matrix"
                                    }
                                ]
                            },
                            jQuery.extend(true, {}, searchParam, {
                                label: gadget.getMsg("risk.management.gadget.filter.label"),
                                description: gadget.getMsg("risk.management.gadget.filter.default"),
                                value: this.getPref("Filter")
                            }),
                            {
                                userpref: "Date",
                                value: this.getPref("Date"),
                                label: this.getMsg("risk.management.gadget.relativeDate.label"),
                                description: this.getMsg("risk.management.gadget.relativeDate.description"),
                                type: "select",
                                selected: this.getPref("Date"),
                                options:[
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.today"),
                                        value: "0"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.yesterday"),
                                        value: "1"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneWeekAgo"),
                                        value: "2"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.twoWeeksAgo"),
                                        value: "3"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneMonthAgo"),
                                        value: "4"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.threeMonthsAgo"),
                                        value: "5"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.sixMonthsAgo"),
                                        value: "6"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneYearAgo"),
                                        value: "7"
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
                    var matrix=args.matrix;
                    gadget.getView().html(matrix);

                },
                args: [{
                    key: "matrix",
                    ajaxOptions: function() {
                        return {
                            type: "POST",
                            url:"/rest/jira-risk-management/1.0/controller/matrix",
                            contentType: "application/json",
                            dataType: "html",
                            data: JSON.stringify({
                                filter: this.getPref("Filter"),
                                template: this.getPref("Template"),
                                title: this.getPref("Title"),
                                date: this.getPref("Date"),
                                refreshRate: this.getPref("refresh")
                            })

                        };
                    }
                }]
            }
        })
        ;
};