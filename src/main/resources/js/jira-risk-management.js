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
                    if (/^jql-/.test(this.getPref("filter")) || this.getPref("isPopup") === "true"){
                        searchParam =
                        {
                            userpref: "filter",
                            type: "hidden",
                            value: gadgets.util.unescapeString(this.getPref("filter"))
                        };
                    }
                    else{
                        searchParam = AJS.gadget.fields.projectOrFilterPicker(gadget,"filter");
                    }

                    return {
                        onResizeReload: true,
                        onResizeAdjustHeight: true,
                        action: "/rest/jira-risk-management/1.0/controller/validate",
                        theme: (function() { return gadgets.window.getViewportDimensions().width < 500 ? "top-label" : "long-label"; })(),
                        fields: [
                            {
                                userpref: "template",
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
                                value: this.getPref("filter")
                            }),
                            {
                                userpref: "date",
                                value: this.getPref("Date"),
                                label: this.getMsg("risk.management.gadget.relativeDate.label"),
                                description: this.getMsg("risk.management.gadget.relativeDate.description"),
                                type: "select",
                                selected: this.getPref("Date"),
                                options:[
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.today"),
                                        value: "TODAY"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.yesterday"),
                                        value: "YESTERDAY"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneWeekAgo"),
                                        value: "WEEK_AGO"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.twoWeeksAgo"),
                                        value: "TWO_WEEKS_AGO"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneMonthAgo"),
                                        value: "MONTH_AGO"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.threeMonthsAgo"),
                                        value: "THREE_MONTHS_AGO"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.sixMonthsAgo"),
                                        value: "SIX_MONTHS_AGO"
                                    },
                                    {
                                        label:this.getMsg("risk.management.gadget.relativeDate.option.oneYearAgo"),
                                        value: "YEAR_AGO"
                                    }
                                ]
                            },
                            {
                                userpref: "title",
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
                            type: "GET",
                            url:"/rest/jira-risk-management/1.0/controller/matrix",

                            dataType: "html",
                            data: {
                                filter: this.getPref("filter"),
                                template: this.getPref("template"),
                                title: this.getPref("title"),
                                date: this.getPref("date"),
                                refresh: this.getPref("refresh")
                            }

                        };
                    }
                }]
            }
        })
        ;
};