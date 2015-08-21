package com.alexin.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Libraries
{
	private List<File> libraries;
	private List<File> deletedFiles;
	
	public Libraries()
	{
		libraries = new ArrayList<File>();
		deletedFiles = new ArrayList<File>();
	}
	
	
	public List<File> getLibraries()
	{
		return libraries;
	}
	
	public boolean addLibrary(File file)
	{
		return libraries.add(file);
	}
	
	public boolean removeLibrary(File file)
	{
		return libraries.remove(file);
	}
	
	public List<File> getAllFiles()
	{
		List<File> allFiles = new ArrayList<File>();
		for(File dir : libraries)
		{
			try 
			{
				allFiles.addAll(Utility.getFiles(dir.getPath()));
			} 
			catch (Exception e) 
			{
				allFiles.clear();
				return allFiles;
			}
		}
		return allFiles;
	}
	
	public boolean saveLibrary(String saveLoc)
	{
		try 
		{
			PrintWriter pw = new PrintWriter(new FileWriter(saveLoc));
			for(File file : libraries)
			{
				pw.println(file.getAbsolutePath());
			}
			pw.close();
			return true;
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
	
	public void clearLibrary()
	{
		libraries.clear();
	}
	
	public boolean loadLibrary(String saveLoc)
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(saveLoc)));
			String line = null;
			while((line = br.readLine()) != null)
			{
				libraries.add(new File(line));
			}
			br.close();
			return true;
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
	
	public List<File> getDeletedFiles()
	{
		return deletedFiles;
	}
	
	public boolean addDeletedFile(File deletedFile)
	{
		return deletedFiles.add(deletedFile);
	}
	
	public boolean removeDeletedFile(File deletedFile)
	{
		return deletedFiles.remove(deletedFile);
	}
	
	public void clearDeletedFiles()
	{
		deletedFiles.clear();
	}
	
	public boolean saveDeletedFiles(String saveLoc)
	{
		try 
		{
			PrintWriter pw = new PrintWriter(new FileWriter(saveLoc));
			for(File file : deletedFiles)
			{
				pw.println(file.getAbsolutePath());
			}
			pw.close();
			return true;
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
	
	public boolean loadDeletedFiles(String saveLoc)
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(saveLoc)));
			String line = null;
			while((line = br.readLine()) != null)
			{
				deletedFiles.add(new File(line));
			}
			br.close();
			return true;
		} 
		catch (IOException e) 
		{
			return false;
		}
	}
}