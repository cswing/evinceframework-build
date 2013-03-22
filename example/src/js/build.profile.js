 var profile = (function(){

    var coreRequires = [
        "dojo/_base/declare",
        "dojo/_base/lang",
        "dijit/dijit",
        "evf-example/CustomWidget"
    ];

    return {
        basePath:       "${basePath}",
        releaseDir:     "${releaseDir}",
        action:         "release",
        cssOptimize:    "comments",
        selectorEngine: "acme",
 
        packages:[{
            name: "dojo",
            location: "dojo"
        },{
            name: "dijit",
            location: "dijit"
        },{
            name: "dojox",
            location: "dojox"
        },{
            name: "evf-example",
            location: "../${sourceDestination}/evf-example"
        }],
 
        layers: {
            "evf-example/core": {
                include:    coreRequires
            }
        }
    };
})();
