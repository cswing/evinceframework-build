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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

/**
 * Provides a Gradle Plugin for a project that will take a set of javascript files and
 * build using the Dojo Toolkit SDK.
 * 
 * @author Craig Swing
 * @since 0.1.0
 * @see Plugin
 * @see Project
 */
public class DojoBuilderPlugin implements Plugin<Project> {

	/**
	 * Apply the plugin to the project
	 * 
	 * @see Plugin#apply(Object)
	 */
	@Override
	public void apply(Project project) {
		
		def convention = new DojoBuilderConvention(project)
		project.convention.plugins.dojoBuilder = convention
		
		project.task('uninstallSource') << {
			project.delete "${convention.sourceRepository}/${convention.sourceDestination}"
		}
		
		project.task('deleteOutput') << {
			project.delete convention.outputPath
		}

		project.task('installSource', dependsOn: 'uninstallSource') << {
			project.copy {
				into "${convention.sourceRepository}/${convention.sourceDestination}"
				from "${convention.source}"
				exclude(convention.profile)
			}
			project.copy {
				into "${convention.sourceRepository}/${convention.sourceDestination}"
				from "${convention.source}/${convention.profile}"
				expand(basePath: '../' + convention.dojoSourcePath, 
					releaseDir: convention.outputPath.replaceAll('\\\\', '/'), 
					sourceDestination: convention.sourceDestination)
			}
		}
		
		project.task('clean', dependsOn:['uninstallSource', 'deleteOutput']){}
		
		project.task('build', dependsOn: ['installSource', 'deleteOutput']) << {
			
			if(convention.buildWithNode){
				println('Building using Node')
			
				def command = "node ${convention.dojoSourcePath}/dojo/dojo.js load=build --profile ${convention.sourceDestination}/${convention.profile} --release --version=${project.version}".toString()
				println(command)
				def proc = command.execute(null, new File(convention.sourceRepository))
				proc.in.eachLine {line -> println line}
				proc.err.eachLine {line -> println 'ERROR: ' + line}
				proc.waitFor()
				
			} else {
				println('Building using Java')
				
				project.javaexec {
					main='org.mozilla.javascript.tools.shell.Main'
					classpath=project.files("${convention.sourceRepository}/dojo-release-${convention.dojoVersion}-src/util/shrinksafe/js.jar", "${convention.sourceRepository}/dojo-release-${convention.dojoVersion}-src/util/closureCompiler/compiler.jar", "${convention.sourceRepository}/dojo-release-${convention.dojoVersion}-src/util/shrinksafe/shrinksafe.jar")
					args=['../../dojo/dojo.js', 'baseUrl=../../dojo', 'load=build', "profile=../../../${convention.sourceDestination}/${convention.profile}", 'action=release', 'releaseName=output', 'copyTests=false', 'optimize=comments', 'cssOptimize=comments']
					workingDir="${convention.sourceRepository}/${convention.dojoSourcePath}/util/buildscripts"
				}
			}
		}
	}
}
