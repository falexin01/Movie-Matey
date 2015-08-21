package com.alexin.address;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class MovieList implements Serializable
{
	public enum SearchParams
	{
		TITLE("Title"), 
		ACTOR("Actor"), 
		DIRECTOR("Director"), 
		WRITER("Writer"), 
		GENRE("Genre");
		
		private final String name;
		
		SearchParams(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public enum Genres
	{
		ACTION("Action"), ADVENTURE("Adventure"), ANIMATION("Animation"), BIOGRAPHY("Biography"), 
		COMEDY("Comedy"), CRIME("Crime"), DOCUMENTARY("Documentary"), DRAMA("Drama"),
		FAMILY("Family"), FANTASY("Fantasy"), FILM_NOIR("Film-Noir"), HISTORY("History"),
		HORROR("Horror"), MUSIC("Music"), MUSICAL("Musical"), MYSTERY("Mystery"),
		ROMANCE("Romance"), SCI_FI("Sci-Fi"), SPORT("Sport"), THRILLER("Thriller"),
		WAR("War"), WESTERN("Western");
		
		private final String name;
		
		Genres(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	private static final long serialVersionUID = 5134028280969948923L;
	public String listName;
	private ArrayList<MovieEntry> movies;
	
	public MovieList(String name)
	{
		listName = name;
		movies = new ArrayList<MovieEntry>();
	}
	
	public boolean addMovie(MovieEntry entry)
	{
		return movies.add(entry);
	}
	
	public MovieEntry getEntryAt(int index)
	{
		return movies.get(index);
	}
	
	public boolean removeEntry(MovieEntry entry)
	{
		return movies.remove(entry);
	}
	
	public ArrayList<MovieEntry> getMovieList()
	{
		return movies;
	}
	
	public void setMovieList(ArrayList<MovieEntry> ml)
	{
		movies = ml;
	}
	
	public void fillByDir(String dir, ProgressBar pb, Label sl, MainApp mainApp, HBox searchingBox)
	{
		final Task<Void> task = new Task<Void>(){
			@Override
			protected Void call() throws Exception 
			{
				try 
				{
					List<File> files = Utility.getFiles(dir);
					int totFiles = files.size();
					int curFiles = 0;
					
					for(File file : files)
					{
						updateMessage(file.getName());
						MovieEntry temp = new MovieEntry(file);
						movies.add(temp);
						updateProgress(curFiles++, totFiles);
					}
					
					updateMessage("Search complete, " + totFiles + " files added.");
					updateProgress(1,1);
					mainApp.saveMovieList();
					mainApp.refreshView();
					Thread.sleep(3000);
					Platform.runLater(new Runnable(){
						public void run() 
						{
							searchingBox.setVisible(false);
						}
						
					});
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				
				return null;
			}	
		};
		pb.progressProperty().bind(task.progressProperty());
		sl.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
	}
	
	public ArrayList<MovieEntry> syncLibraries(List<File> files, List<File> deletedFiles, MainApp mainApp)
	{
		ArrayList<MovieEntry> newMovies = new ArrayList<MovieEntry>();
		
		final Task<Void> task = new Task<Void>()
		{
			@Override
			protected Void call() throws Exception 
			{
				Iterator<File> it = files.iterator();
				while(it.hasNext())
				{
					File temp = it.next();
					for(MovieEntry mvEntry : movies)
					{
						if(temp.equals(mvEntry.getFile()))
						{
							it.remove();
							break;
						}
					}
					for(File deletedTemp : deletedFiles)
					{
						if(temp.equals(deletedTemp))
						{
							it.remove();
							break;
						}
					}
				}
				
				if(!files.isEmpty())
				{
					for(File file : files)
					{
						movies.add(new MovieEntry(file));
					}
					mainApp.refreshView();
					mainApp.saveMovieList();
				}
				return null;
			}	
		};
		new Thread(task).start();
		return newMovies;
	}
	
	public ArrayList<MovieEntry> searchMovieList(String search, SearchParams param)
	{
		ArrayList<MovieEntry> searchResults = new ArrayList<MovieEntry>();
		for(MovieEntry entry : movies)
		{
			if(entry.getTitle().toLowerCase().contains(search) && param == SearchParams.TITLE)
			{
				searchResults.add(entry);
			}
			else if(entry.getActors().toLowerCase().contains(search) && param == SearchParams.ACTOR)
			{
				searchResults.add(entry);
			}
			else if(entry.getDirector().toLowerCase().contains(search) && param == SearchParams.DIRECTOR)
			{
				searchResults.add(entry);
			}
			else if(entry.getWriter().toLowerCase().contains(search) && param == SearchParams.WRITER)
			{
				searchResults.add(entry);
			}
			else if(entry.getGenre().toLowerCase().replace("," , "").contains(search) && param == SearchParams.GENRE)
			{
				searchResults.add(entry);
			}
		}
		
		return searchResults;
	}
	
	public MovieEntry getHighestRated()
	{
		MovieEntry tempEntry = movies.get(0);
		for(MovieEntry mvEntry : movies)
		{
			if(mvEntry.getimdbRating() > tempEntry.getimdbRating())
				tempEntry = mvEntry;
		}
		return tempEntry;
	}
	
	public MovieEntry getLongest()
	{
		MovieEntry tempEntry = movies.get(0);
		for(MovieEntry mvEntry : movies)
		{
			if(mvEntry.getRuntime() > tempEntry.getRuntime())
				tempEntry = mvEntry;
		}
		return tempEntry;
	}
	
	public TreeMap<String, Integer> getGenreMap()
	{
		TreeMap<String, Integer> genreMap = new TreeMap<String, Integer>();
		for(Genres g : Genres.values())
		{
			genreMap.put(g.toString(), 0);
			for(MovieEntry mvEntry : movies)
			{
				if(mvEntry.getGenre().contains(g.toString()))
				{
					genreMap.put(g.toString(), genreMap.get(g.toString()) + 1);
				}
			}
		}
		return genreMap;
	}
	
	public TreeMap<String, Integer> getActorMap()
	{
		TreeMap<String, Integer> actorMap = new TreeMap<String, Integer>();
		for(MovieEntry mvEntry : movies)
		{
			for(String actor : mvEntry.getActorList())
			{
				if(actorMap.containsKey(actor))
				{
					actorMap.put(actor, actorMap.get(actor) + 1);
				}
				else
				{
					actorMap.put(actor, 1);
				}
			}
		}
		
		return actorMap;
	}
	
	public ArrayList<MovieEntry> missingFileCheck()
	{
		ArrayList<MovieEntry> missingFiles = new ArrayList<MovieEntry>();
		for(MovieEntry mvEntry : movies)
		{
			if(!mvEntry.getFile().exists())
				missingFiles.add(mvEntry);
		}
		
		return missingFiles;
	}
	
	public String getListName()
	{
		return listName;
	}
	
	public MovieEntry getMatchingEntry(MovieEntry mv)
	{
		for(MovieEntry tempEntry : movies)
		{
			if(mv.compareTo(tempEntry) == 0)
				return tempEntry;
		}
		return null;
	}
	
	public Boolean contains(MovieEntry mv)
	{
		for(MovieEntry tempEntry : movies)
		{
			if(mv.compareTo(tempEntry) == 0)
				return true;
		}
		return false;
	}
	
	public void resetAllFileSize()
	{
		for(MovieEntry mv : movies)
		{
			mv.setFileSize(mv.getFile());
		}
	}
	
	@Override
	public String toString() 
	{
		return listName;
	}
}
