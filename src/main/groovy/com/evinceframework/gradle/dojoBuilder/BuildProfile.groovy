/*
 * Copyright 2013 Craig Swing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.gradle.dojoBuilder


/**
 * Defines settings for the build.  These settings are used to build a profile to use when building.  More 
 * information about the settings can be found at http://dojotoolkit.org/documentation/tutorials/1.8/build/ 
 * 
 * @author Craig Swing
 * @since 0.1.0
 */
public class BuildProfile {

	/**
	 * Default constructor.  Properties from the {@link DojoBuilderConvention} are required for calculated fields.
	 * 
	 * @param convention
	 */
	BuildProfile(DojoBuilderConvention convention) {
		super
		
		this.convention = convention
	}
	
	/**
	 * Properties from the {@link DojoBuilderConvention} are required for calculated fields.
	 */
	private DojoBuilderConvention convention
	
	/**
	 * The filename to write the build profile to.
	 *
	 * The default value is <pre>build.profile.js</pre>
	 */
	String filename = 'build.profile.js'
	
	/**
	 * This is the "root" of the build, from where the rest of the build will be calculated from. This is relative
	 * to where the build profile is located.
	 */
	public String getBasePath() {
		return "../${convention.dojoSourcePath}"
	}
		
	/**
	 * This is the root directory where the build should go. The builder will attempt to create this directly and
	 * will overwrite anything it finds there. It is relative to the basePath
	 *
	 * @see #getBasePath()
	 */
	String getReleaseDir() {
		return convention.outputPath.replaceAll('\\\\', '/')
	}
	
	/**
	 * This provides a name to a particular release when outputting it. This is appended to the releaseDir. For 
	 * example, if you are going to release your code in release/prd you could set your releaseDir to release and 
	 * your releaseName to prd. 
	 * 
	 * @see #getReleaseDir()
	 */
	String releaseName = 'release'

	/**
	 * Sets the minification setting for layers. This defaults to "shrinksafe". A value of false turns off 
	 * minification and the other valid values are "shrinksafe.keeplines", "closure", "closure.keeplines", "comment", 
	 * and "comment.keeplines". 
	 */
	def layerOptimize = 'shrinksafe'
	
	/**
	 * Sets the minification for modules that aren't part of a layer. This defaults to false and takes the same 
	 * values as layerOptimize.
	 * 
	 * @see #layerOptimize
	 */
	def optimize = false
	
	/**
	 * Deals with how CSS is optimized. Defaults to "comments"
	 * A value of "comments" will strip out comments and extra lines and inline any @import commands. 
	 * A value of "comments.keepLines" strips the comments and inlines the @imports, but preserves any line breaks.
	 */
	String cssOptimize = 'comments'
	
	/**
	 * This determines if the build is a "mini" build or not. If true it will exclude files that are tagged as 
	 * miniExclude which is typically things like tests, demos and other items not required for the build to work. 
	 * This defaults to false.
	 */
	boolean mini = false
		
	/**
	 * This determines how console handling is dealt with in the output code. This defaults to "normal" which strips 
	 * all console messages except console.error and console.warn. It is important to note though, this feature only 
	 * applies when there is a level of optimization going on, otherwise it is ignored. Other possible values are 
	 * "none", "warn" and "all"
	 */
	String stripConsole = 'normal'
	
	/**
	 * This identifies the default selector engine for the build and builds it into the code. While this does not 
	 * directly make the code smaller, it ensure that a selector engine won't require another call to be loaded. It 
	 * defaults to "acme" and the two engines included with Dojo are "lite" and "acme".
	 */
	String selectorEngine = 'acme'
	
	
	/**
	 * This is an array of hashes of package information which the builder uses when mapping modules. This provides 
	 * flexibility in locating in different places and the pulling it together when you build. 
	 */
	def packages = []	

	/**
	 * The external packages that will be added to the build release. 
	 * 
	 * @since 0.2.0
	 * @see BuildProfile#extpkg(Object)
	 */
	def externalPackages = []
	
	/**
	 * The default boot layer.  If defined this will appear as the first layer in the build profile. 
	 */
	def bootLayer = [:]
	
	/**
	 * This allows you to create different "layer" modules as part of a build that contain discreet functionality 
	 * all built into single file. 
	 */
	def layers = [] 
	
	/**
	 * Adds a package to the build profile.
	 *
	 * @param obj
	 */
	def pkg(Object obj) {
		if(['dojo','dijit','dojox'].contains(obj.name)) {
			packages.add(name: obj.name, location: obj.name)
		
		} else {
			// if no location is passed, then the assumption is that this is the package
			// for the current project.
			def location = obj.location ?: "${convention.sourceDestination}/${obj.name}"
			packages.add(name: obj.name, location: "../${location}")
		}
	}
	
	/**
	 * Define an external package that should be added to the release.  External packages 
	 * are javascript artifacts that are not designed for an AMD build.  External packages 
	 * should exist in the source repository and will be copied into the release.  They 
	 * will not be modified by the build.
	 * 
	 * @param obj
	 * @since 0.2.0
	 * @see DojoBuilderConvention#getSourceRepository()
	 * @see DojoBuilderConvention#getOutputPath()
	 */
	def extpkg(Object obj) {
		externalPackages.add(name: obj.name, location: obj.location)
	}
	
	/**
	 * Define the boot layer for the custom build.
	 * 
	 * @param obj
	 */
	def boot(Object obj) {
		
		if(layers.size()>0)
			throw new Exception("boot needs to be defined before adding layers.")
		
		bootLayer.layerName = obj?.name ?: 'dojo/dojo' 
		bootLayer.customBase = obj?.customBase ?: false
		bootLayer.includes = obj?.includes ?: []
		
		if(bootLayer.includes.size()==0){
			bootLayer.includes.add('dojo/dojo')
		}
	}
	
	/**
	 * Add a non boot layer to the build profile.  At least one module must be included.  If a boot layer 
	 * is defined, the includes of the boot layer are automatically added to the excludes of the layer.
	 * 
	 * @param obj
	 */
	def layer(Object obj) {
		
		def layer = [:]		
		layer.layerName = obj.name
		
		layer.includes = obj?.includes ?: []
		if(layer.includes.size()==0)
			throw new Exception("at least one include must be defined.")
		
		def excludes = bootLayer?.includes?:[]
		(obj?.excludes ?: []).each{
			exc -> if(!excludes.contains(exc)){ excludes.add(exc) }
		}
		layer.excludes = excludes
		
		layers.add(layer)
	}
	
	/**
	 * Allow for properties to be defined in a build script using a closure. Automatically adds the dojo and dijit 
	 * packages to the profile.
	 *
	 * Usage:
	 *
	 *	dojo {
	 *	    dojoVersion='1.8.3'
	 *	    sourceRepository=javascriptRepo
	 *	    buildWithNode=project.hasProperty('useNode').toBoolean() && project.useNode.toBoolean()
	
	 *		profile.configure {
	 *			pkg name: 'dojox'
	 *			pkg name: 'evf-example', location: "${sourceDestination}"
	 *			
	 *			boot()		
	 *			layer name: 'evf-example/app', includes: ['evf-example/CustomWidget']
	 *		}
	 *	}
	 *
	 * @param closure
	 * @return
	 */
	def configure(Closure closure) {
		pkg name: 'dojo'
		pkg name: 'dijit'

		closure.delegate = this
		closure()
	}
}
