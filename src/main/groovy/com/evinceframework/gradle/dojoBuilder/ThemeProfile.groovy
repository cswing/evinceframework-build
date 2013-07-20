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
package com.evinceframework.gradle.dojoBuilder;

import org.gradle.api.Project

/**
 * Used to define custom themes that should be built using a less compiler. 
 * 
 * @author Craig Swing
 * @since 0.2.0
 */
public class ThemeProfile {

	/**
	 * The parent project.
	 */
	private Project project
	
	/**
	 * Properties from the {@link DojoBuilderConvention} are required for calculated fields.
	 */
	private DojoBuilderConvention convention
	
	/**
	 * Default constructor.  Properties from the {@link DojoBuilderConvention} are required for calculated fields.
	 * 
	 * @author Craig Swing
	 * @param convention
	 * @since 0.2.0
	 */
	ThemeProfile(Project project, DojoBuilderConvention convention) {
		super
		
		this.project = project
		this.convention = convention
	}
	
	/**
	 * The custom themes to build.
	 */
	def customThemes = []
	
	/**
	 * Customizes the Dojo claro theme using a custom variables file to build the css.
	 * 
	 * @param obj
	 * @return
	 */
	def customize(Object obj) {
		def theme = new ThemeGeneratorAction(this.project, this.convention);
		  
		theme.themeName = obj.themeName
		theme.variableFile = obj.variableFile
		theme.outputDirectory = obj.outputDirectory
		theme.resetCache = this.convention.resetThemeCache
		
		// Add dojo claro theme.
		theme.sources.add(
			name: 'dojo', 
			location: "dojo-release-${convention.dojoVersion}-src/dijit/themes/claro"
		)
		theme.sources.addAll(obj.addlSources ?: [])
		
		customThemes.add(theme)
	}
	
	/**
	 * Allow for custom themes to be defined in a build script using a closure.
	 * 
	 * @param closure
	 * @return
	 */
	def define(Closure closure) {
		closure.delegate = this
		closure()
	}
}