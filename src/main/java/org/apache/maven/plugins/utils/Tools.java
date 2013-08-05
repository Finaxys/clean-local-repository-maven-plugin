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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.plugin.logging.Log;

/**
 * This utility class provides common operations on basic object types ( IO, String, List, Date, RegExp.. )
 * 
 * @author sgu, pef, lab...
 */
public final class Tools {
	
	/**
	 * Private constructor prevent the class from being explicitly instantiated
	 */
	private Tools() 
	{
		super();
	}

	/**
	 * Implementation of the isNullOrEmpty check for the String objects.
	 * 
	 * @param string
	 * @return true if the given String is null or is an empty String, false otherwise.
	 */
	public static boolean isNullOrEmpty(final String string){
		
		return string == null || string.isEmpty();
	}
	
	
	/**
	 * Implementation of the isNullOrEmpty check for the arrays.
	 * 
	 * @param array
	 * @return true if the given array is null or is an empty String, false otherwise.
	 */
	public static boolean isNullOrEmpty(final Object[] array){
		
		return array == null || array.length == 0;
	}
	
	
	/**
	 * Implementation of the isNullOrEmpty check for the Lists. 
	 * 
	 * @param list
	 * @return true if the given List is null or is an empty List, false otherwise.
	 */
	public static boolean isNullOrEmpty(final List<?> list){
		
		return list == null || list.isEmpty();
	}

	
	/**
	 * Deletes a directory and all sub-file or directories, never throwing an exception.
	 * If deleteOnExistMode option is activated, the deletion will be attempted only for normal termination of the virtual machine.
	 * A directory to be deleted does not have to be empty. No exceptions are thrown when a file or directory cannot be deleted.
	 * 
	 * @param file
	 * @param deleteOnExistMode
	 * @param log
	 */
	public static void deleteFolderQuietly(final File file, final boolean deleteOnExistMode, final Log log) {
		
		try {

			if(file.exists()){
					
				final Collection<File> files = listFilesAndFolders(file);
	
				deleteFileCollectionQuietly(files, deleteOnExistMode, log);
			}
		}
		catch(Exception e)
		{
			log.error( Enumeres.EXCEPTION.FILE_DELETION_EXCEPTION + file , e);
		}
	}

	
	/**
	 * Deletes a file collection, never throwing an exception.
	 * If deleteOnExistMode option is activated, the deletion will be attempted only for normal termination of the virtual machine.
	 * A directory to be deleted does not have to be empty. No exceptions are thrown when a file or directory cannot be deleted.
	 * 
	 * @param files
	 * @param deleteOnExistMode
	 * @param log
	 */
	public static void deleteFileCollectionQuietly(final Collection<File> files, final boolean deleteOnExistMode, final Log log) {
		
		for (final File file : files) {

			deleteQuietly(file, deleteOnExistMode, log);
		}
	}
	
	
	/**
	 * Deletes a file (or a directory), never throwing an exception.
	 * If deleteOnExistMode option is activated, the deletion will be attempted only for normal termination of the virtual machine.
	 * A directory to be deleted does not have to be empty. No exceptions are thrown when a file or directory cannot be deleted.
	 * 
	 * @param file
	 * @param deleteOnExistMode
	 * @param log
	 */
	public static void deleteQuietly(final File file, final boolean deleteOnExistMode, final Log log) {

		try {
			if(deleteOnExistMode)
			{
				file.deleteOnExit();
			}
			else {
				FileUtils.deleteQuietly(file);
			}	
		} 
		catch (SecurityException e) 
		{
			log.error( Enumeres.EXCEPTION.FILE_DELETION_EXCEPTION + file, e );
		}

	}
	
	
	/**
	 * List recursively all the files of a given directory.
	 * 
	 * @param file
	 * @return a list of the files and only files (folder are not listed) contained by the given directory
	 */
	public static List<File> listFiles(final File file) {
		
		return (List<File>) FileUtils.listFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
	}
	
	
	/**
	 * List recursively all the files and folder of a given directory.
	 * 
	 * @param file
	 * @return a list of the files and folders contained by the given directory
	 */
	public static List<File> listFilesAndFolders(final File file) {
		
		return (List<File>) FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
	}
	
	
	/**
	 * List recursively all folder of a given directory.
	 * 
	 * @param file
	 * @return file a list of the folders and only folders (files are not listed) contained by the given directory
	 */
	public static List<File> listFolders(final File file) {

		return (List<File>) FileUtils.listFilesAndDirs(file, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY);
	}
	
	
	/**
	 * List recursively all folder of a given directory, excluding the current directory.
	 * The result list is ordered on artifact last modification date.
	 * 
	 * @param file
	 * @return a list of the folders and only folders (files are not listed) contained by the given directory,
	 * 		   the result is ordered on the modification date (ascending, with the most recent first).
	 */
	public static List<File> listSubFoldersOrdered(final File file) {

		List<File> result = new ArrayList<File>();

		if (file.exists() && file.isDirectory()) {

			result = listFolders(file);

			result.remove(file);

			Collections.sort(result, new ArtifactFolderComparator());
		}

		return result;
	}
	
	
	/**
	 * Computation of the elapsed days between two dates.
	 * 
	 * @param date1
	 * @param date2
	 * @return the number of days between two given date
	 */
	public static int compareDaysBetweenDates(final Date date1, final Date date2)
	{
		return (int)((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	
	/**
	 * Return true if the input match the given pattern or if the pattern is null
	 * 
	 * @param pattern
	 * @param input
	 * @return true if the input match the given pattern or if the pattern is null; false otherwise.
	 */
	public static boolean matchPatternIgnoreCase(final Pattern pattern, final String input)
	{
		return pattern.matcher(input.toLowerCase(Locale.getDefault())).matches();
	}
	
	/**
	 * Return true if the input match the given pattern or if the pattern is null
	 * 
	 * @param pattern
	 * @param input
	 * @return true if the input match the given pattern or if the pattern is null; false otherwise.
	 */
	public static boolean testPattern(final String regExp)
	{
		try {
			Pattern.compile(regExp);
		}
		catch(Exception e) 
		{
			return false;
		}
				
		return true;
	}
	
}
