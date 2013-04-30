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

import org.gradle.api.Project

/**
 * Convention specifying defaults for the DojoBuilder plugin.
 * 
 * @author Craig Swing
 * @see DojoBuilderPlugin
 * @since 0.1.0
 */
public class DojoBuilderConvention {
	
	/**
	 * Default constructor.  Project is required to set default values.
	 *
	 * @param project The Gradle project.
	 */
	public DojoBuilderConvention(Project project) {
		super
		
		profile = new BuildProfile(this)
		themes = new ThemeProfile(project, this)
		source = "${project.projectDir}/src/js"
		sourceDestination = "${project.name}-${project.version}"
		outputPath = "${project.projectDir}/build"
		userBuildHomeDirectory = "${System.properties['user.home']}/.evf"
		sourceRepository = "${userBuildHomeDirectory}/dojoplugin/js_source"
	}
	
	/**
	 * The user's home directory for building software using the plugin.
	 * 
	 * Defaults to ${user.home}/.evf
	 * @since 0.2.0
	 */
	def userBuildHomeDirectory
	
	/**
	 * Configuration for the build profile
	 */
	def profile
	
	/**
	 * Configuration for building custom themes.
	 * 
	 * @since 0.2.0
	 */
	def themes
	
	/**
	 * Whether or not the theme cache should be reset when generating themes.
	 *
	 * @since 0.2.0
	 */
	def resetThemeCache = true
	
	/**
	 * Tells the DojoBuilder plugin to use Node or Java to perform the build.
	 * Defaults to false, which means Java will be used.
	 */
	boolean buildWithNode = false
	
	/**
	 * The DojoBuilder plugin relies on a repository of javascript source code to build from.
	 * This field identifies location of the source repository.
	 */
	String sourceRepository
	
	/**
	 * The source repository may contain multiple versions of the Dojo Toolkit.  This field
	 * identifies the version of the Dojo Toolkit to use.
	 *
	 * Within the source repository, the Dojo Toolkit SDK must exist in the <pre>/dojo-release-${dojoVersion}-src</pre>
	 * directory , where <pre>${dojoVersion}</pre> is the value specified by this property.
	 *
	 * <pre>dojo-release-XXX-src</pre> is the directory that is packaged in the Dojo Toolkit SDK and can be downloaded
	 * from http://dojotoolkit.org/download/
	 */
	String dojoVersion
	
	/**
	 * The relative path to the Dojo Toolkit SDK in the repository. See {@link DojoBuilderConvention#dojoVersion}
	 * for more details.
	 */
	String getDojoSourcePath() {
		return "dojo-release-${dojoVersion}-src"
	}
	
	/**
	 * The name of the directory in the project that contains the source.  The content in this directory
	 * will be copied to the {@link DojoBuilderConvention#sourceDestination} directory.
	 *
	 * The default value is the <pre>src/js</pre> directory within the project.
	 */
	String source
	
	/**
	 * The name of the directory where the {@link DojoBuilderConvention#source} is copied to.
	 * The {@link DojoBuilderPlugin} will build using the {@link DojoBuilderConvention#profile} and source javascript
	 * from this directory.
	 *
	 * The default value is <pre>${project.name}-${project.version}</pre> directory within the
	 * {@link DojoBuilderConvention#sourceRepository}, where <pre>${project.name}</pre> is the project's name and
	 * <pre>${project.version}</pre> is the current project version.
	 */
	String sourceDestination
	
	/**
	 * The directory where the output of the build should be placed.
	 *
	 * The default is the <pre>/build/release</pre> in the project directory.
	 */
	String outputPath
	
	/**
	 * Allow for properties to be defined in a build script using a closure.
	 * 
	 * Usage: 
	 * 
	 * 	dojo {
	 * 	 	dojoVersion='1.8.3'
	 * 	}
	 * 
	 * @param closure
	 * @return
	 */
	def dojo(Closure closure) {
		closure.delegate = this		
		closure()
	}

}
