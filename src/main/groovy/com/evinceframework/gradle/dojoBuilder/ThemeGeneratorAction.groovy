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

import java.security.MessageDigest

import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.lesscss.LessCompiler
import org.lesscss.LessSource

/**
 * Builds a custom Dojo claro theme based on the parameters that are contained in a variables file.  Also, provides
 * the ability to add additional less files that are not part of the Dojo Toolkit claro release.
 * 
 * @author Craig Swing
 * @since 0.2.0
 */
public class ThemeGeneratorAction {

	/**
	 * The owning project
	 */
	private Project project;
	
	/**
	 * The Dojo builder convention
	 */
	private DojoBuilderConvention convention;
	
	private int hashOperationCount = 1000
	
	private String algorithm = 'SHA-256'
	
	private String encoding = 'UTF-8'
	
	private Base64 encoder = new Base64()
	
	/**
	 * Default constructor
	 * 
	 * @param project
	 * @param convention
	 */
	ThemeGeneratorAction(Project project, DojoBuilderConvention convention){
		this.project = project
		this.convention = convention
	}
	
	public def themeName = 'custom'
	
	public def variableFile = 'variables.less'
	
	public def outputDirectory
	
	public def force = false
	
	public def resetCache = false
	
	def sources = []
	
	/**
	 * Calculate a hash that identifies the particular custom theme.
	 * 
	 * - Dojo Version
	 * - Base Theme Name
	 * - New Theme Name
	 *  
	 * @return
	 */
	protected String calculateHash() {
		
		MessageDigest digest = MessageDigest.getInstance(algorithm)
		digest.reset()
		
		def key = "${convention.dojoVersion}:${themeName}"
		sources.each{ source ->
			key = "${key}:${source.location}"	
		}
		
		return new String(encoder.encode(digest.digest(key.getBytes(encoding))))
	}
	
	/**
	 * Moves the source less files and image artifacts from the source directories 
	 * into a single directory that will be used to compile.
	 */
	protected void prepareInputDirectory(File includeDir) {
		
		sources.each{ source ->
			
			new File("${includeDir}/${source.name}").mkdirs();
			
			project.copy {
				from("${convention.sourceRepository}/${source.location}"){
				   include '**/*.less'
				   exclude 'variables.less'
				}
				into "${includeDir}/${source.name}"
				filter { String line -> line.replace('claro', themeName) }
			}
			
			// copy image artifacts
			project.copy {
				from("${convention.sourceRepository}/${source.location}"){
				   include '**/*.jpg'
				   include '**/*.png'
				   include '**/*.gif'
				   include '**/*.ico'
				}
				into "${includeDir}/${source.name}"
			}
		}
	}
	
	/**
	 * Creates and configures a LessCompiler for use.
	 * 
	 * @return the LessCompiler
	 */
	protected LessCompiler configureCompiler() {
		
		LessCompiler compiler = new LessCompiler()
		compiler.setCompress(false)
		
		//compiler.setEncoding(getEncoding())
		//if (getCustomJs() != null) {
		//	compiler.setCustomJs(getCustomJs())
		//}
		
		return compiler
	}
	
	/**
	 * - Determine if the base has exists in the cache
	 * 		hash dojo version, baseTheme, themeName, variableFile Size, variableFile LastUpdate
	 * - If not 
	 * 		- copy the theme out of source and move the variable file into a caching directory
	 * 		- rename the theme name to the new theme name
	 * 
	 * 
	 * - run less and cache the output
	 * - Move the output to the source repository directory of current project
	 *
	 */
	def execute() {
		
		def hash = calculateHash()
			.replace("\\", "~")
			.replace("/", "~")
			
		def includesDirPath = "${convention.userBuildHomeDirectory}/dojoplugin/cache/less/${hash}"
		def includeDir = new File(includesDirPath)
		
		// clear cache if asked to
		if(includeDir.exists() && resetCache) {
			FileUtils.deleteDirectory(includeDir)
		} 
		
		if(!includeDir.exists()) {
			prepareInputDirectory(includeDir)
		}
		
		// TODO evaluate if the variable file has changed vs. force parameter
		
		sources.each{ source ->
			project.copy {
				from variableFile
				into "${includeDir}/${source.name}"
				rename { String fileName ->
					return 'variables.less'
				}
			}
		}
		
		LessCompiler compiler = configureCompiler()
		def imports = []
		
		FileUtils.deleteDirectory(new File("${outputDirectory}"))
		
		// compile less files to css
		project.fileTree(includeDir) {
			include '**/*.less'
			exclude 'variables.less'
		}.each { File inputFile ->
			
			def newFileName = includeDir.toURI().relativize(inputFile.toURI()).getPath().replace('.less', '.css');
			
			imports.add(newFileName)
				
			def outputFile = new File("${outputDirectory}/${newFileName}")
			if (!outputFile.parentFile.exists() && !outputFile.parentFile.mkdirs()) {
				throw new GradleException("Cannot create output directory ${outputFile.parentFile}")
			}
			
			compiler.compile(new LessSource(inputFile), outputFile, force)
		}
		
		// create core css that imports all css files
		new File("${outputDirectory}/${themeName}.css").withWriter { out ->
			imports.each { out.println "@import url(\"${it}\");" }
		}
		
		// copy image artifacts
		project.copy {
			from("${includesDirPath}"){
		       include '**/*.jpg'
			   include '**/*.png'
			   include '**/*.gif'
			   include '**/*.ico'
		    }
			into "${outputDirectory}"
		}
	}
	
}
