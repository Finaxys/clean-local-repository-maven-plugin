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

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator implementation for a folder containing artifacts
 * 
 * @author sgu, pef, lab..
 */
public class ArtifactFolderComparator implements Comparator<File>, Serializable {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 7029110639678254352L;

	/**
	 * Order two artifact folders on there last modified date.
	 * If possible, this implementation analyze first the content of the folder, the folder date otherwise.
	 */
	@Override
	public int compare(File folder0, File folder1) {
		
		int returnCode = 1;
		
		File[] files0 = folder0.listFiles();
		File[] files1 = folder1.listFiles();
		
		if (!Tools.isNullOrEmpty(files0) && !Tools.isNullOrEmpty(folder1.listFiles())) 
		{
			returnCode = files0[0].lastModified() < files1[0].lastModified() ? 1 : -1;
		} 
		else if (!Tools.isNullOrEmpty(files0)) 
		{
			returnCode = files0[0].lastModified() < folder1.lastModified() ? 1 : -1;
		} 
		else if (!Tools.isNullOrEmpty(files1)) 
		{
			returnCode = folder0.lastModified() < files1[0].lastModified() ? 1 : -1;
		} 
		else {
			returnCode = folder0.lastModified() < folder1.lastModified() ? 1 : -1;
		}
			
		return returnCode;
	}

}
