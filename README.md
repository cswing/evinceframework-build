evinceframework build kit
=====================

A set of plugins for building software using Gradle.

Dojo Toolkit Plugin
---------------------

Configure and execute a Dojo Toolkit build in your Gradle script.

The plugin expects the Dojo Toolkit SDK and any other source external modules (dgrid, etc) to exist in a directory on the machine.  
This directory is known as the source repository.  The plugin will copy the source from the project to the 
source repository and perform a Dojo Toolkit build.  The output of the build will be placed into the project's
`build/release` directory.

A complete working example can be found in the example directory.

*source repository layout example*

    c:\JS_SOURCE\
      dgrid-0.3.6
      dojo-release-1.7.3-src
      dojo-release-1.8.3-src
      put-selector-0.3.2
      xstyle-0.0.5

*build.gradle*

    buildscript {
      repositories {
        mavenLocal()
      }

      dependencies {
        classpath 'com.evinceframework:evf-buildTools:0.1.0'
      }
    }
        
    apply plugin: 'dojo'
    
    dojo {
      dojoVersion='1.8.3'
      sourceRepository='${javascriptRepo}'
    }

*usage*

    > gradle buildDojo -PjavascriptRepo=C:/JS_SOURCE/

### Tasks

|Task               |Description                                                      |
|-------------------|-----------------------------------------------------------------|
|installSource      |Installs the source from the project into the source repository. |
|uninstallSource    |Removes the source from the source reporitory.                   |
|deleteOutput       |Removes the output from previous build.                          |
|clean              |Performs the uninstallSource and deleteOutput tasks.             |
|buildUsingJava     |Performs a Dojo Toolkit build using a Java process and Rhino.    |
|buildUsingNode     |Performs a Dojo Toolkit build using Node.                        |
|buildDojo          |Performs a Dojo Toolkit build using either Java or Node.         | 

