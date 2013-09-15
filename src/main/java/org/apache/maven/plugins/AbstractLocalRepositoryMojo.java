package org.apache.maven.plugins;

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

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.utils.Enumeres;
import org.apache.maven.plugins.utils.MavenUtils;
import org.apache.maven.plugins.utils.Tools;
import org.apache.maven.project.MavenProject;

/**
 * Abstract parent class of this plugin MOJOs.
 * 
 * The greater part of the plugin implementation is here because of the commons behavior 
 * of its different goals.
 * 
 * @version $Id$
 * 
 * @author sgu, pef, lab...
 */
public abstract class AbstractLocalRepositoryMojo extends AbstractMojo
{

	/**
	 * Controls whether the plugin tries to delete the current project snapshot versions from
	 * the local repository regarding to the snapshotRetentionDelay and
	 * snapshotVersionsRetention options.
	 * 
	 * @parameter expression="${clean-local-repository.deleteCurrentSnapshot}" default-value="false"
	 * @since 1.1
	 */
	private boolean deleteCurrentSnapshot;
	
	/**
	 * Controls whether the plugin tries to delete all the snapshot versions from
	 * the local repository regarding to the snapshotRetentionDelay and
	 * snapshotVersionsRetention options.
	 * 
	 * @parameter expression="${clean-local-repository.deleteAllSnapshots}" default-value="false"
	 * @since 1.1
	 */
	private boolean deleteAllSnapshots;

	/**
	 * Controls whether the plugin tries to delete the current project release versions from the
	 * local repository regarding to the releaseRetentionDelay and
	 * releaseVersionsRetention options.
	 * 
	 * @parameter expression="${clean-local-repository.deleteCurrentRelease}" default-value="false"
	 * @since 1.1
	 */
	private boolean deleteCurrentRelease;

	/**
	 * Controls the expiration delay (in days) before deleting a snapshot
	 * version.
	 * 
	 * @parameter expression="${clean-local-repository.snapshotRetentionDelay}" default-value="-1"
	 * @since 1.0
	 */
	private int snapshotRetentionDelay;

	/**
	 * Controls the number of different snapshot versions to keep without
	 * deletion.
	 * 
	 * @parameter expression="${clean-local-repository.snapshotVersionsRetention}" default-value="-1"
	 * @since 1.0
	 */
	private int snapshotVersionsRetention;

	/**
	 * Controls the expiration delay (in days) before deleting a release
	 * version.
	 * 
	 * @parameter expression="${clean-local-repository.releaseRetentionDelay}" default-value="-1"
	 * @since 1.0
	 */
	private int releaseRetentionDelay;

	/**
	 * Controls the number of different release versions to keep without deletion.
	 * 
	 * @parameter expression="${clean-local-repository.releaseVersionsRetention}" default-value="-1"
	 * @since 1.0
	 */
	private int releaseVersionsRetention;

	/**
	 * Delete all files from the local repository which match with the given
	 * regular expression (@see Pattern definition).
	 * 
	 * The selection pattern is applied on the file system path from the root
	 * of the maven local repository.
	 * 
	 * e.g. DeleteFromRegularExpression = ^com.* will delete all artifacts and
	 * metadata published on the ${localMavenRepository}/com path.
	 * 
	 * @parameter expression="${clean-local-repository.deleteFromRegularExpression}"
	 * @since 1.0
	 */
	private String deleteFromRegularExpression;
	
	/**
	 * Controls the number of different snapshot versions to keep without deletion.
	 * 
	 * @parameter expression="${clean-local-repository.deleteEmptyFolders}" default-value="true"
	 * @since 1.0
	 */
	private boolean deleteEmptyFolders;

	/**
	 * Controls whether the plugin have to purge the whole local maven repository.
	 * 
	 * @parameter expression="${clean-local-repository.deleteWholeLocalRepository}" default-value="false"
	 * @since 1.0
	 */
	private boolean deleteWholeLocalRepository;

	/**
	 * Controls whether delete command will be attempted only for normal termination of the virtual machine.
	 * 
	 * @parameter expression="${clean-local-repository.executeDeleteOnExit}" default-value="true"
	 * @since 1.0
	 */
	private boolean executeDeleteOnExit;

	/**
	 * Root location of the local maven repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository localMavenRepository;
	

	/** 
	 * Current project definition
	 * 
	 * @parameter expression="${project}" 
	 * @readonly 
	 */ 
	private MavenProject project; 
	
	
	
	
    /**
     * Main entry point for sub classes of this abstract implementation.
     * Execute clean local repository sub routine according to the given mojo goal and options.
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
	protected void executeCleanLocalRepositoryGoals() throws MojoExecutionException, MojoFailureException {
		
		
		final File localRepository = initializeAndCheckMojoContext();
		
		// First, controls whether the plugin have to purge the whole local maven repository. 

		if(deleteWholeLocalRepository){
			
			deleteAll(localRepository);
			
			return;
		}
		
		
		final String artefactPath = getLocalRepositoryPathForCurrentArtefact(localRepository);

		final List<File> folderList = Tools.listSubFoldersOrdered(new File(artefactPath));

		
		// Controls whether the plugin try to delete the "Snapshot" version from the local repository
		// Then list content and delete expired artifacts (based on snapshotRetentionVersion or snapshotRetentionDelay) 
		
		if(deleteCurrentSnapshot){
			
			final List<File> snapshotFoldersList = MavenUtils.getSnapshots(folderList);

			deleteArtifactOnVersionExpiration(snapshotFoldersList, snapshotVersionsRetention); 
			
			deleteArtifactOnDelayExpiration(snapshotFoldersList, snapshotRetentionDelay); 
		}
		
		// As describe before, controls whether the plugin try to delete the "Release" version from the local repository
		// Then list content and delete expired artifacts (based on releaseRetentionVersion or releaseRetentionDelay) 
		
		if(deleteCurrentRelease){

			final List<File> releaseFoldersList = MavenUtils.getReleases(folderList);
			
			deleteArtifactOnVersionExpiration(releaseFoldersList, releaseVersionsRetention); 
			
			deleteArtifactOnDelayExpiration(releaseFoldersList, releaseRetentionDelay); 
		}
		
		
		if(project.isExecutionRoot()){
 
			final List<File> filesList = Tools.listFiles(localRepository);
			
			// Delete all files from the local repository which match with the given regular expression.
			// The selection pattern is applied on the file system path from the root of the maven local repository.
	
			if(deleteFromRegularExpression != null){
				
				deleteFromRegularExpression(deleteFromRegularExpression, filesList);	
			}
			
			
			// Controls whether the plugin try to delete all the "Snapshot" version from the local repository
			// Then list content and delete expired artifacts (based on snapshotRetentionVersion or snapshotRetentionDelay) 
			
			if(deleteAllSnapshots){
					
				deleteAllSnapshots(filesList);
			}
	
		
			// Delete all the empty folders from the local repository.
	
			if(deleteEmptyFolders){
				
				deleteEmptyFolders(localRepository); 
			}
		}
		
	}

	
	/**
	 * Execute delete sub-routine according to the given retentionDelay options which controls the expiration delay 
	 * (in days) before deleting a version.
	 * 
	 * @param foldersList
	 * @param retentionDelay
	 */
	private void deleteArtifactOnDelayExpiration(final List<File> foldersList, final int retentionDelay) {
		
		for (int i = 0; retentionDelay >= 0 && i < foldersList.size(); i++) {

			final File artifactFolder = foldersList.get(i);

			if (getArtifactElapsedDays(artifactFolder) > retentionDelay) {

				getLog().info((isDeleteModeActivated() ? Enumeres.LOG.DELETE : Enumeres.LOG.LIST) + artifactFolder.getAbsolutePath());

				if (isDeleteModeActivated()) { Tools.deleteFolderQuietly(artifactFolder, executeDeleteOnExit, getLog()); }
			}
		}
	}


	/**
	 * Execute delete sub-routine according to the given snapshotVersionsRetention options which controls the number of different
	 * versions to keep without deletion.
	 * 
	 * @param foldersList
	 * @param retentionVersion
	 */
	private void deleteArtifactOnVersionExpiration(final List<File> foldersList, final int retentionVersion) {
		
		for (int i = retentionVersion; retentionVersion >= 0 && i < foldersList.size(); i++) {
			
			final File artifactFolder = foldersList.get(i);

			getLog().info( (isDeleteModeActivated() ? Enumeres.LOG.DELETE : Enumeres.LOG.LIST) + artifactFolder.getAbsolutePath());	 
			
			if(isDeleteModeActivated()){ Tools.deleteFolderQuietly(artifactFolder, executeDeleteOnExit, getLog()); }
		}
	}
	
	
	/**
	 * Execute delete sub-routine according to the deleteAllSnapshots options which controls whether the plugin
     * have to purge all the snapshots version from the whole local maven repository.
     * 
     * This implementation handle the snapshotVersionsRetention and snapshotVersionsRetention options.
     * 
	 * @param filesList
	 */
	private void deleteAllSnapshots(final List<File> filesList) {
		
		Set<File> artifact = MavenUtils.getSnapshotArtifacts(filesList);

		for (File artifactFoldersWithSnapshot : artifact) {
						
			deleteArtifactOnVersionExpiration(MavenUtils.getSnapshots(artifactFoldersWithSnapshot), snapshotVersionsRetention); 
			
			deleteArtifactOnDelayExpiration(MavenUtils.getSnapshots(artifactFoldersWithSnapshot), snapshotRetentionDelay); 
			
		}
	}

    
	/**
	 * Delete all files  from the local repository which match with the given regular expression (@see Pattern definition).
	 * The selection pattern is applied on the file system path from the root of the maven local repository.
	 * 
	 * @param deleteFromRegularExpression
	 * @param repositoryPath
	 */
    private void deleteFromRegularExpression(final String deleteFromRegularExpression, final List<File> filesList) {

    		final Pattern pattern = Pattern.compile(deleteFromRegularExpression);

	    	for (final File file : filesList) {
	
	        	if(Tools.matchPatternIgnoreCase(pattern, file.getAbsolutePath()))
	        	{
	        		getLog().info( (isDeleteModeActivated() ? Enumeres.LOG.DELETE : Enumeres.LOG.LIST) + file.getAbsolutePath());	 
	
	    			if(isDeleteModeActivated()){ Tools.deleteQuietly(file, executeDeleteOnExit, getLog()); }
	        	}
			}
    	
    }
    
    
	/**
	 * Execute delete sub-routine according to the deleteEmptyFolders options which controls whether the plugin have to 
	 * delete all the empty folders from the local maven repository
	 * 
	 * @param foldersList
	 * @param snapshotVersionsRetention
	 * @param isDeleteMode
	 * @param repositoryPath 
	 */
	private void deleteEmptyFolders(final File repositoryPath) {

		final List<File> foldersList = Tools.listFolders(repositoryPath);
				
		Collections.reverse(foldersList);
		
		for (int i = 0; i < foldersList.size(); i++) {

			final File folder = foldersList.get(i);

			if (Tools.isNullOrEmpty(Tools.listFiles(folder))) {

				getLog().info((isDeleteModeActivated() ? Enumeres.LOG.DELETE_EMPTY : Enumeres.LOG.LIST_EMPTY) + folder.getAbsolutePath());

				if (isDeleteModeActivated()) { Tools.deleteQuietly(folder, executeDeleteOnExit, getLog()); }
			}
		}
	}
    
    
    /**
     * Execute delete sub-routine according to the deleteEmptyFolders options which controls whether the plugin
     * have to purge the whole local maven repository.
     * 
     * @param localMavenRepositoryDirectory
     * @param isDeleteMode
     */
	private void deleteAll(final File localMavenRepositoryDirectory) {

    	if(project.isExecutionRoot()){
    	
			getLog().info((isDeleteModeActivated() ? Enumeres.LOG.DELETE_ALL : Enumeres.LOG.LIST_ALL) 
												   + localMavenRepositoryDirectory.getAbsolutePath());

			if(isDeleteModeActivated()){ Tools.deleteFolderQuietly(localMavenRepositoryDirectory, executeDeleteOnExit, getLog()); }
    	}
	}
	

	/**
	 * Get the elapsed days from the last modification date of a given artifact folder.
	 * 
	 * @param artifactFolder
	 * @return the number of days elapsed from the last modification date of the artifact.
	 */
	private int getArtifactElapsedDays(final File artifactFolder) {
		
		final File[] folderContent = artifactFolder.listFiles();
		
		final long lastModified = !Tools.isNullOrEmpty(folderContent) ? folderContent[0].lastModified() : artifactFolder.lastModified();
		
		final int elapsedDays = Tools.compareDaysBetweenDates(new Date(lastModified), Calendar.getInstance().getTime()) ;
		
		return elapsedDays;
	}
	
	
    /**
     * Get the local repository path for a the current module according to the artifact groupId, artifactId and version.
     * 
     * @param localRepositoryFolder
     * @return the current artifact path in the local maven repository
     */
	private String getLocalRepositoryPathForCurrentArtefact(final File localRepositoryFolder) {
		
		//TODO : @see and replace if possible by : new File(localRepository.getBasedir(), localRepository.pathOf(artifact)).getParentFile();
		
		final StringBuilder artefactPath = new StringBuilder(localRepositoryFolder.getAbsolutePath());
		
		artefactPath.append(File.separator).append(project.getGroupId().replace(".", File.separator));
		artefactPath.append(File.separator).append(project.getArtifactId());
		
		return artefactPath.toString();
	}

	
	/**
	 * Initialize and check the execution context of the Mojo :
	 * - read access to the local repository folder
	 * - unexpected parameter
	 * - pattern syntax exception
	 * 
	 * @return a file representing the current maven local repository
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	private File initializeAndCheckMojoContext() throws MojoExecutionException, MojoFailureException {
		
		final File localRepositoryFolder = new File(localMavenRepository.getBasedir());
		
		if(!localRepositoryFolder.exists())
		{
			throw new MojoExecutionException( Enumeres.EXCEPTION.LOCAL_MAVEN_REPOSITORY_UNAVAILABLE + localRepositoryFolder );
		}
				
		if(!localRepositoryFolder.canWrite())
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.LOCAL_MAVEN_REPOSITORY_PERMISSION_DENIED + localRepositoryFolder );
		}
		
		
		if(snapshotRetentionDelay < -1)
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.UNEXPECTED_PARAMETER + Enumeres.MOJO_OPTION.SNAPSHOT_RETENTION_DELAY 
										  + Enumeres.EXCEPTION.NEGATIVE_NUMBER + snapshotRetentionDelay);
		}
		
		if(snapshotVersionsRetention < -1)
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.UNEXPECTED_PARAMETER + Enumeres.MOJO_OPTION.SNAPSHOT_VERSIONS_RETENTION
										  + Enumeres.EXCEPTION.NEGATIVE_NUMBER + snapshotVersionsRetention);
		}
		
		if(releaseRetentionDelay < -1)
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.UNEXPECTED_PARAMETER + Enumeres.MOJO_OPTION.SNAPSHOT_RETENTION_DELAY 
										  + Enumeres.EXCEPTION.NEGATIVE_NUMBER + releaseRetentionDelay);
		}
		
		if(releaseVersionsRetention < -1)
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.UNEXPECTED_PARAMETER + Enumeres.MOJO_OPTION.SNAPSHOT_VERSIONS_RETENTION
										  + Enumeres.EXCEPTION.NEGATIVE_NUMBER + releaseVersionsRetention);
		}

		try {
			if(!Tools.isNullOrEmpty(deleteFromRegularExpression)){
				Pattern.compile(deleteFromRegularExpression);
			}
		}
		catch(PatternSyntaxException e) 
		{
			throw new MojoFailureException( Enumeres.EXCEPTION.UNEXPECTED_PARAMETER + Enumeres.MOJO_OPTION.DELETE_FROM_REGULAR_EXPRESSION
					  					  + Enumeres.EXCEPTION.PATTERN_SYNTAX_EXCEPTION + deleteFromRegularExpression, e);
		}
		
		// Initialization of a default plugin retention behavior if, and only if, all the retention options and the RegExp option are unvalued
		// So without any argument, the default behavior is to resolve the current project artifacts tree, then delete all version excepting current one from the local repository 
		
		if(deleteFromRegularExpression == null 
		&& snapshotRetentionDelay == -1 && snapshotVersionsRetention == -1  
		&& releaseRetentionDelay  == -1 && releaseVersionsRetention  == -1)
		{			
			snapshotRetentionDelay    = releaseRetentionDelay    = 7;
			snapshotVersionsRetention = releaseVersionsRetention = 1;
			
			deleteCurrentSnapshot = true;
			deleteCurrentRelease = true;
		}
		
		return localRepositoryFolder;
	}
	
	

	/**
	 * Define if the current goal allows deletion or only list artifact to delete.
	 * Implementation is delegated to the  sub classes representing "list" and "clean" goal, as defined by the Maven MOJO Specification.
	 * 
	 * @return true if the current goal is "clean", false otherwise.
	 */
	protected abstract boolean isDeleteModeActivated();

}
