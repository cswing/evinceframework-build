 var profile = (function(){
	/* Generated profile built by the DojoBuilder Gradle Plugin */
    return {
        basePath:       "${profile.basePath}",
        releaseDir:     "${profile.releaseDir}",
        action:         "release",
        releaseName:	"${profile.releaseName}",
        
        layerOptimize:	"${profile.layerOptimize}",
        optimize:		"${profile.optimize}",
        cssOptimize:    "${profile.cssOptimize}",
        mini:			"${profile.mini}",
        stripConsole:	"${profile.stripConsole}",
        selectorEngine: "${profile.selectorEngine}",
        
        packages:[
			<% profile.packages.each{ pkg -> %>{name: '$pkg.name', location: '$pkg.location'}<% if(pkg != profile.packages.last()) {%>,
			<%}} %>
		],
 
        layers: {
        	<% if (profile.bootLayer.layerName!=null){%>'${profile.bootLayer.layerName}':{
        		boot: true,
        		customBase: ${profile.bootLayer.customBase},
        		include:[
        		    <% profile.bootLayer.includes.each{ inc -> %>'$inc'<% if(inc != profile.bootLayer.includes.last()) {%>,
        		    <%}} %>
        		]
        	}<% if (profile.layers.size()>0){%>,<%}}%>
        	<% profile.layers.each{ layer -> %>'$layer.layerName':{
        		include:[
         		    <% layer.includes.each{ inc -> %>'$inc'<% if(inc != layer.includes.last()) {%>,
         		    <%}} %>
         		],
         		exclude:[
          		    <% layer.excludes.each{ exc -> %>'$exc'<% if(exc != layer.excludes.last()) {%>,
          		    <%}} %>
          		]
        	}<% if(layer != profile.layers.last()) {%>,
        	<%}} %>
        }
    };
})();