package org.apache.maven.plugins.utils;

/*
 * Copyright 2013 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Enumeration of String constants classified under interface declarations.
 * It's helping to keep the source code clean, regardless of the corresponding and inconsistent PMD violation.
 *  
 * @author pef, sgu, lab ..
 */
public interface Enumeres {

	 
	/**
	 * Enumeration of the exception used in the project
	 */
	
	interface EXCEPTION {
 
		String FILE_DELETION_EXCEPTION = "An exception occured during deletion of ";
		
		String LOCAL_MAVEN_REPOSITORY_PERMISSION_DENIED = "Permission denied on the local maven repository : ";

		String LOCAL_MAVEN_REPOSITORY_UNAVAILABLE = "Cannot access to local maven repository : ";

		String UNEXPECTED_PARAMETER = "Unable to parse configuration of mojo clean-local-repository-plugin for parameter ";
		
		String NEGATIVE_NUMBER = ", negative number not allowed : ";
		
		String PATTERN_SYNTAX_EXCEPTION = ", pattern syntax exception : ";
	}

	/**
	 * Enumeration of the log Strings used in the project
	 */
	
	interface LOG {
		
		String STARTING_PLUGIN = "Starting clean-local-repository:";

		String DELETE = "Deleting artifact ";

		String LIST = "Artifact could be deleted ";
		
		String DELETE_ALL = "Erase all option activated : deleting M2_REPO folder : ";
		
		String LIST_ALL = "Erase all option activated : whole M2_REPO folder could be deleted : ";
		
		String DELETE_EMPTY = "Deleting empty folder ";

		String LIST_EMPTY   = "Empty folder could be deleted ";
	}
	
	/**
	 * Enumeration of the goals used in the MOJOs
	 */
	
	interface MVN_GOAL {
		
		String CLEAN = "clean";
		
		String LIST  = "list";
	}	
	
	/**
	 * Enumeration of the options used in the MOJOs
	 */
	
	interface MOJO_OPTION {
		
		String DELETE_SNAPSHOT = "deleteSnapshot";

		String DELETE_RELEASE = "deleteRelease";

		String SNAPSHOT_RETENTION_DELAY = "snapshotRetentionDelay";

		String SNAPSHOT_VERSIONS_RETENTION = "snapshotVersionsRetention";

		String RELEASE_RETENTION_DELAY = "releaseRetentionDelay";

		String RELEASE_VERSIONS_RETENTION = "releaseVersionsRetention";

		String DELETE_FROM_REGULAR_EXPRESSION = "deleteFromRegularExpression";

		String DELETE_EMPTY_FOLDERS = "deleteEmptyFolders";

		String DELETE_WHOLE_LOCAL_REPOSITORY = "deleteWholeLocalRepository";

		String EXECUTE_DELETE_ON_EXIT = "executeDeleteOnExit";

	}	
	
}
