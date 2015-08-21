package com.alexin.address;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


public class Utility 
{
	public static boolean saveImage(String imageUrl, String fileLoc)
	{
		URL url;
		try 
		{
			url = new URL(imageUrl);
		
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(fileLoc);
	
			byte[] b = new byte[2048];
			int length;
	
			while ((length = is.read(b)) != -1) 
			{
				os.write(b, 0, length);
			}
	
			is.close();
			os.close();
			return true;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}
	
	public static List<File> getFiles(String directory) throws IOException
	{
		File dir = new File(directory);
		String[] extensions = new String[] { "avi", "mkv", "mp4", "mpg", "m2ts", "m4v"};
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		
		Iterator<File> it = files.iterator();
		while(it.hasNext())
		{
			File file = it.next();
			if(file.length() < 104857600)
				it.remove();
		}
		
		return files;
	}
	
	public static String[] fileNameClean(String fileName)
	{	
		String[] titleAndYear = new String[2];
		Pattern pattern = Pattern.compile("[2][0][\\d][\\d]|[1][9][\\d][\\d]");
		Matcher matcher = pattern.matcher(fileName);
		
		if(matcher.find())
		{
			try
			{
			    titleAndYear[1] = matcher.group();
				String temp = fileName.substring(0, matcher.start()-1);
				temp = temp.replace(".", " ");
				temp.trim();
				titleAndYear[0] = temp;
				return titleAndYear;
			}
			catch(Exception e)
			{
				titleAndYear = null;
			}
		}
		
		try
		{
			titleAndYear[0] = FilenameUtils.removeExtension(fileName);
			titleAndYear[1] = null;
			return titleAndYear;
		}
		catch(Exception e)
		{
			titleAndYear = null;
			return titleAndYear;
		}
	}
	
	public static String getFileSize(File file)
	{
		String fileSize = "N/A";
		if(file.exists())
		{
			long bytes = file.length();
			if(bytes > 1073741824)
			{
				fileSize = Long.toString(bytes / 1073741824);
				fileSize = fileSize + " GB";
			}
			else
			{
				fileSize = Long.toString(bytes / 1048576);
				fileSize = fileSize + " MB";
			}
		}
		return fileSize;
	}
}
