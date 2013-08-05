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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;

/**
 * Test class for the clean-local-repository MOJO
 * 
 * @author sgu, pef, lab..
 */
public class TestLocalRepositoryMojo extends AbstractMojoTestCase {

	/** Path to the test local maven repository **/
	
	private static final String TEST_M2_REPO_PATH = "/target/testLocalMavenRepository";
	
	/**  High value parameter allows to disable parameters in this test case context **/
	
	private static final int DESACTIVATIVED = 10000;
	
	
	private File testM2Repo;

	private CleanLocalRepositoryMojo cleanLocalRepositoryMojo;
	
	private File pluginArtifact;
	private File releaseArtifact1;
	private File releaseArtifact2;
	private File releaseArtifact3;
	private File snapshotArtifact1;
	private File snapshotArtifact2;
	private File snapshotArtifact3;
	
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

		super.setUp();

		testM2Repo = initializeTestMavenRepository();
		
		cleanLocalRepositoryMojo = initializeCleanLocalRepositoryMojo();
		
        assertTrue(pluginArtifact.exists());
		assertTrue(releaseArtifact1.exists());
		assertTrue(releaseArtifact2.exists());
		assertTrue(releaseArtifact3.exists());
		assertTrue(snapshotArtifact1.exists());
		assertTrue(snapshotArtifact2.exists());
		assertTrue(snapshotArtifact3.exists());
    }


    /**
     * Test the deleteWholeLocalRepository option and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testDeleteWholeLocalRepository() throws Exception
    {
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteWholeLocalRepository", true);
        
        assertTrue(testM2Repo.exists());
        
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertFalse(testM2Repo.exists());
    }
    
    
    /**
     * Test the snapshotRetentionDelay option and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testSnapshotRetentionDelay() throws Exception
    {        
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteSnapshot", true);
        setVariableValueToObject(cleanLocalRepositoryMojo, "snapshotRetentionDelay", 1);
        setVariableValueToObject(cleanLocalRepositoryMojo, "snapshotVersionsRetention", DESACTIVATIVED);
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertTrue(pluginArtifact.exists());
        
		assertTrue(releaseArtifact1.exists());
		assertTrue(releaseArtifact2.exists());
		assertTrue(releaseArtifact3.exists());
		
		assertTrue(snapshotArtifact1.exists()); 
		assertFalse(snapshotArtifact2.exists()); // Deleted, this snapshot file was older than 1 days
		assertFalse(snapshotArtifact3.exists()); // Deleted, this snapshot file was older than 1 days
    }
    
    
    /**
     * Test the snapshotVersionsRetention option and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testSnapshotVersionsRetention() throws Exception
    {
    	
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteSnapshot", true);
        setVariableValueToObject(cleanLocalRepositoryMojo, "snapshotRetentionDelay", DESACTIVATIVED);
        setVariableValueToObject(cleanLocalRepositoryMojo, "snapshotVersionsRetention", 2);
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertTrue(pluginArtifact.exists());
        
		assertTrue(releaseArtifact1.exists());
		assertTrue(releaseArtifact2.exists());
		assertTrue(releaseArtifact3.exists());
		
		assertTrue(snapshotArtifact1.exists());  
		assertTrue(snapshotArtifact2.exists());
		assertFalse(snapshotArtifact3.exists()); // Deleted, keep only 2 snapshot version
    }
    
    
    /**
     * Test the deleteWholeLocalRepository and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testReleaseRetentionDelay() throws Exception
    {
    	
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteRelease", true);
        setVariableValueToObject(cleanLocalRepositoryMojo, "releaseRetentionDelay", 2);
        setVariableValueToObject(cleanLocalRepositoryMojo, "releaseVersionsRetention", DESACTIVATIVED);
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertTrue(pluginArtifact.exists());
        
        assertTrue(releaseArtifact1.exists());
        assertTrue(releaseArtifact2.exists());
        assertFalse(releaseArtifact3.exists()); // Deleted, this release file is older than 2 days
        
		assertTrue(snapshotArtifact1.exists()); 
		assertTrue(snapshotArtifact2.exists()); 
		assertTrue(snapshotArtifact3.exists()); 
    }
    
    
    /**
     * Test the deleteWholeLocalRepository and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testReleaseVersionsRetention() throws Exception
    {

        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteRelease", true);
        setVariableValueToObject(cleanLocalRepositoryMojo, "releaseRetentionDelay", DESACTIVATIVED);
        setVariableValueToObject(cleanLocalRepositoryMojo, "releaseVersionsRetention", 0);
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertTrue(pluginArtifact.exists());
        
        assertFalse(releaseArtifact1.exists()); // releaseVersionsRetention set to 0, all the release versions are deleted
        assertFalse(releaseArtifact2.exists()); // releaseVersionsRetention set to 0, all the release versions are deleted
        assertFalse(releaseArtifact3.exists()); // releaseVersionsRetention set to 0, all the release versions are deleted
		
		assertTrue(snapshotArtifact1.exists());  
		assertTrue(snapshotArtifact2.exists());		
		assertTrue(snapshotArtifact3.exists()); 
    }
    
    
    
    /**
     * Test the deleteFromRegularExpression and delegated implementations of the plugin
     * 
     * @throws Exception
     */
    public void testDeleteFromRegularExpression() throws Exception
    {
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteFromRegularExpression", ".*plugin-example.*");
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertFalse(pluginArtifact.exists()); // Deleted, this artifact match with the given RegExp
        
        assertTrue(releaseArtifact1.exists());
        assertTrue(releaseArtifact2.exists());
        assertTrue(releaseArtifact3.exists());
		
		assertTrue(snapshotArtifact1.exists());  
		assertTrue(snapshotArtifact2.exists());		
		assertTrue(snapshotArtifact3.exists()); 
    }
    
    
    /**
     * Test the deleteFromRegularExpression and delegated implementations of the plugin
     * The given RegExp is the negation of an artifact exemple (delete all except 'plugin-exemple')
     * 
     * @throws Exception
     */
    public void testDeleteFromRegularExpressionNegation() throws Exception
    {
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteFromRegularExpression", "^((?!.*plugin-exemple.*).)*");
		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertTrue(pluginArtifact.exists()); 
        
        assertFalse(releaseArtifact1.exists()); // Deleted, this artifact doesn't match with the given RegExp
        assertFalse(releaseArtifact2.exists()); // Deleted, this artifact doesn't match with the given RegExp
        assertFalse(releaseArtifact3.exists()); // Deleted, this artifact doesn't match with the given RegExp
		
        assertFalse(snapshotArtifact1.exists()); // Deleted, this artifact doesn't match with the given RegExp
        assertFalse(snapshotArtifact2.exists()); // Deleted, this artifact doesn't match with the given RegExp	
        assertFalse(snapshotArtifact3.exists()); // Deleted, this artifact doesn't match with the given RegExp
    }
    
    
    /**
     * Test the deleteEmptyFolders and delegated implementations of the plugin
     * In this test case, the option is enabled
     * 
     * @throws Exception
     */
    public void testDeleteEmptyFoldersTrue() throws Exception
    {
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteFromRegularExpression", ".*plugin-example.*");
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteEmptyFolders", true);
        		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertFalse(pluginArtifact.exists()); 
        assertFalse(pluginArtifact.getParentFile().exists()); // Empty folder was deleted 
    }
    
    /**
     * Test the deleteEmptyFolders and delegated implementations of the plugin
     * In this test case, the option is disabled
     * 
     * @throws Exception
     */
    public void testDeleteEmptyFoldersFalse() throws Exception
    {
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteFromRegularExpression", ".*plugin-example.*");
        setVariableValueToObject(cleanLocalRepositoryMojo, "deleteEmptyFolders", false);
        		
        cleanLocalRepositoryMojo.executeCleanLocalRepositoryGoals();
        
        assertFalse(pluginArtifact.exists()); 
        assertTrue(pluginArtifact.getParentFile().exists()); // Empty folder was not deleted 
    }
    

    /**
     * Common initialization of the mojo parameter
     *  
     * @return
     * @throws MalformedURLException
     * @throws IllegalAccessException
     */
	private CleanLocalRepositoryMojo initializeCleanLocalRepositoryMojo() throws MalformedURLException, IllegalAccessException {
		
		CleanLocalRepositoryMojo cleanLocalRepositoryMojo = new CleanLocalRepositoryMojo();

    	DefaultArtifactRepository localMavenRepository = new DefaultArtifactRepository("testLocalRepo", testM2Repo.toURI().toURL().toString(), new DefaultRepositoryLayout(), true);
        
    	setVariableValueToObject(cleanLocalRepositoryMojo, "executeDeleteOnExit", false);
    	
    	setVariableValueToObject(cleanLocalRepositoryMojo, "localMavenRepository", localMavenRepository);
         
    	MavenProject mavenProject =  new MavenProject();
    	
    	mavenProject.setGroupId("org.maven.test");
    	
    	mavenProject.setArtifactId("test-example");
    	
    	mavenProject.setVersion("3.0-SNAPSHOT");
    	
    	mavenProject.setExecutionRoot(true);
    	
    	setVariableValueToObject(cleanLocalRepositoryMojo, "project", mavenProject);
    	
		return cleanLocalRepositoryMojo;
	}
	
	/**
	 * Common initialization of the test local repository with empty artifact
	 * 
	 * @return 
	 * @throws IOException
	 */
	private File initializeTestMavenRepository() throws IOException {
		
		File testM2Repo = new File(getBasedir() + TEST_M2_REPO_PATH);
		 
		testM2Repo.mkdirs();
		
		pluginArtifact    = new File(testM2Repo, "org/maven/plugins/plugin-example/1.0/plugin-exemple-1.0.jar");
		releaseArtifact1  = new File(testM2Repo, "org/maven/test/test-example/1.0/test-example-1.0.jar");
		releaseArtifact2  = new File(testM2Repo, "org/maven/test/test-example/2.0/test-example-2.0.jar");
		releaseArtifact3  = new File(testM2Repo, "org/maven/test/test-example/3.0/test-example-3.0.jar");
		snapshotArtifact1 = new File(testM2Repo, "org/maven/test/test-example/1.0-SNAPSHOT/test-example-1.0-SNAPSHOT.jar");
		snapshotArtifact2 = new File(testM2Repo, "org/maven/test/test-example/2.0-SNAPSHOT/test-example-2.0-SNAPSHOT.jar");
		snapshotArtifact3 = new File(testM2Repo, "org/maven/test/test-example/3.0-SNAPSHOT/test-example-3.0-SNAPSHOT.jar");
 
		createArtifact(pluginArtifact,    -7);
		createArtifact(releaseArtifact1,  -1);
		createArtifact(releaseArtifact2,  -2);
		createArtifact(releaseArtifact3,  -3);
		createArtifact(snapshotArtifact1, -1);
		createArtifact(snapshotArtifact2, -2);
		createArtifact(snapshotArtifact3, -3);
		
		return testM2Repo;
		
	}

	/**
	 * Create an empty artifact with creation date modification (for delay testing purpose)
	 * @param file
	 * @param creationDateSinceToday
	 * @throws IOException
	 */
	private void createArtifact(File file, int creationDateSinceToday) throws IOException {

		FileUtils.openOutputStream(file , false).close();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, creationDateSinceToday);
		file.setLastModified(calendar.getTimeInMillis());
	}

}
