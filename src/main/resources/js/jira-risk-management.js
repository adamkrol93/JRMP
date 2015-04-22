jQuery.namespace("AMG.jrmp");
AMG.jrmp.init = function (args) {
    AJS.log("Startuję");
    AJS.log(args.baseUrl);
    var base=args.baseUrl;
    var gadget = AJS.Gadget({
            baseUrl: base,
            useOauth: "/rest/gadget/1.0/currentUser",
            config:{
                descriptor:function(args){
                    return{
                        action: args.projects,
                        theme: "long-label",
                        fields:[{userpref: "Template",
                            "class": "numField",
                            value: gadget.getPref("Template"),
                            label: gadget.getMsg("risk.management.gadget.template.label"),
                            description: gadget.getMsg("risk.management.gadget.template.description"),
                            type: "text"},
                            AJS.gadget.fields.nowConfigured(),
                            AJS.log("Koniec konfigurowania")
                        ]
                    }
                },
                args:[{}]

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
                    key: "favFilters",
                    ajaxOptions: function () {
                        return {
                            url: "/rest/gadget/1.0/favfilters",
                            data: {
                                showCounts: this.getPref("showCounts")
                            }
                        };
                    }
                }]
            }
        })
        ;
    AJS.log("Pomyślnie skończyłem");
}
