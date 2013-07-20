 var profile = (function(){
	/* Generated profile built by the DojoBuilder Gradle Plugin */
    return {
        basePath:       "../dojo-release-1.8.3-src",
        releaseDir:     "C:/Source/evinceframework-build/examples/simpleBuild/build",
        action:         "release",
        releaseName:	"release",
        
        layerOptimize:	"shrinksafe",
        optimize:		false,
        cssOptimize:    "comments",
        mini:			false,
        stripConsole:	"normal",
        selectorEngine: "acme",
        
        packages:[
			{name: 'dojo', location: 'dojo'},
			{name: 'dijit', location: 'dijit'},
			{name: 'dojox', location: 'dojox'},
			{name: 'evf-example', location: '../simpleBuild-0.1.0/evf-example'}
		],
 
        layers: {
        	'dojo/dojo':{
        		boot: true,
        		customBase: false,
        		include:[
        		    'dojo/dojo'
        		]
        	},
        	'evf-example/app':{
        		include:[
         		    'evf-example/CustomWidget'
         		],
         		exclude:[
          		    'dojo/dojo'
          		]
        	}
        }
    };
})();