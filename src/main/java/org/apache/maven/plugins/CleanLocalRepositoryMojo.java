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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Implementation of the clean-local-repository:clean goal.
 * 
 * @goal clean
 * 
 * @phase process-sources
 * 
 * @requiresProject false
 * 
 * @version $Id$
 * 
 * @author sgu, pef, lab...
 */
public class CleanLocalRepositoryMojo extends AbstractLocalRepositoryMojo
{

    /**
     * Implementation of the execute() method for the clean-local-repository:list goal.
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException 
     * 
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
    	super.executeCleanLocalRepositoryGoals();
    }

    
	/**
	 * This abstract method implementation define the deletion mode associated with the current goal.
	 * 
	 * @return true in the current "clean" goal context
	 */
	protected boolean isDeleteModeActivated()
	{
		return true;
	}
   
}
