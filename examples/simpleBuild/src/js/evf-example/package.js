var profile = (function(){

    // checks if mid is in app/tests directory
    var testResourceRe = /^app\/tests\//,
    
    copyOnly = function(filename, mid){
        // specific files to copy only
        var list = {
            "evf-example/package.js": true,
            "evf-example/package.json": true
        };
        
        // Check if it is one of the special files, 
        // if it is in app/resource (but not CSS) or is an image
        return (mid in list) ||
            (/^app\/resources\//.test(mid)
                && !/\.css$/.test(filename)) ||
            /(png|jpg|jpeg|gif|tiff)$/.test(filename);
    };
 
    return {
        resourceTags: {
            // identify the tests
            test: function(filename, mid){
                return testResourceRe.test(mid) || mid=="evf-example/tests";
            },
            
            copyOnly: function(filename, mid){
                return copyOnly(filename, mid);
                // Tag our copy only files
            },
            
            amd: function(filename, mid){
                return !testResourceRe.test(mid)
                    && !copyOnly(filename, mid)
                    && /\.js$/.test(filename);
                // If it isn't a test resource, copy only,
                // but is a .js file, tag it as AMD
            }
        }
    };
})();