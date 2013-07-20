define(["dojo/_base/declare", "dijit/_Widget"], function(declare, Widget){
    
    return declare("evf-example/CustomWidget", [Widget], {
        postCreate: function() {
            this.inherited(arguments);
            console.debug("CustomWidget.postCreate();");
        }
    });
});


