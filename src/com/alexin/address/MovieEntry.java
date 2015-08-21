package com.alexin.address;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

public class MovieEntry implements Serializable, Comparable<MovieEntry>
{
	//private static final String posterApiKey = "f27d49b3";
	private static final String posterApiURL = "http://img.omdbapi.com/?i=";
	private static final long serialVersionUID = 5134028280969948923L;
	private static final String[] keys = {"Title","Year","Rated","Released","Runtime","Genre","Director","Writer","Actors","Plot","Awards","Poster","imdbRating","imdbVotes","imdbID"};
	private static final String[] search_keys = {"Title","Year","imdbID","Type"};
	private Map<String, String> movieData = new HashMap<String, String>();
	File file;
	String fileSize;
	String posterSaveLoc;
	String posterSaveDir = MainApp.posterSaveLoc;
	
	public MovieEntry(File file)
	{
		this.file = file;
		fileSize = Utility.getFileSize(file);
		String[] titleYear = null;
		
		if(file.getName() != null)
			titleYear = Utility.fileNameClean(file.getName());
		if(titleYear != null)
		{
			if(titleYear[1] != null)
			{
				InfoRequest request = new InfoRequest(titleYear[0],titleYear[1]);
				if(request.sendRequest())
				{
					JSONObject data = request.getJSONResponse();
					if(Boolean.parseBoolean(data.get("Response").toString()) && data.get("Type").toString().equals("movie"))
					{
						for(int i = 0; i < keys.length; i++)
						{
							movieData.put(keys[i], data.get(keys[i]).toString());
						}
						imageSave(data);
					}
					else
						setDataNull();	
				}
				else
					setDataNull();
			}
			
			else if(titleYear[0] != null)
			{
				InfoRequest request = new InfoRequest(titleYear[0],"");
				if(request.sendRequest())
				{
					JSONObject data = request.getJSONResponse();
					if(Boolean.parseBoolean(data.get("Response").toString()) && data.get("Type").toString().equals("movie"))
					{
						for(int i = 0; i < keys.length; i++)
						{
							movieData.put(keys[i], data.get(keys[i]).toString());
						}
						imageSave(data);
					}
					else
						setDataNull();
				}
				else
					setDataNull();
			}
			else
				setDataNull();
		}
		else
			setDataNull();
	}
	
	public MovieEntry(JSONObject data, Boolean search)
	{
		this.file = null;
		this.fileSize = "0";
		if(search)
		{
			for(int i = 0; i < keys.length; i++)
			{
				movieData.put(keys[i], null);
			}
			for(int i = 0; i < search_keys.length; i++)
			{
				movieData.put(search_keys[i], data.get(search_keys[i]).toString());
			}
		}
		else
		{
			for(int i = 0; i < keys.length; i++)
			{
				movieData.put(keys[i], data.get(keys[i]).toString());
			}
			imageSave(data);
		}
	}
	
	public MovieEntry()
	{
		this.fileSize = "0";
		setDataNull();
	}
	
	public boolean setMovieDataByTitle(String title, String year)
	{
		InfoRequest request = new InfoRequest(title, year);
		if(request.sendRequest())
		{
			JSONObject data = request.getJSONResponse();
			if(Boolean.parseBoolean(data.get("Response").toString()) && data.get("Type").toString().equals("movie"))
			{
				for(int i = 0; i < keys.length; i++)
				{
					movieData.put(keys[i], data.get(keys[i]).toString());
				}
				imageSave(data);
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public boolean setMovieDataByID(String id)
	{
		InfoRequest request = new InfoRequest(id, false);
		if(request.sendRequest())
		{
			JSONObject data = request.getJSONResponse();
			if(Boolean.parseBoolean(data.get("Response").toString()) && data.get("Type").toString().equals("movie"))
			{
				for(int i = 0; i < keys.length; i++)
				{
					movieData.put(keys[i], data.get(keys[i]).toString());
				}
				imageSave(data);
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	private void setDataNull()
	{
		for(int i = 0; i < keys.length; i++)
		{
			movieData.put(keys[i], "N/A");
		}
		movieData.put(keys[0], file.getName());
		movieData.put(keys[1], "1800");
		movieData.put(keys[4], "0 min");
		movieData.put(keys[12], "0");
		movieData.put(keys[13], "0");
		posterSaveLoc = null;
	}
	
	private void imageSave(final JSONObject data)
	{
		posterSaveLoc = posterSaveDir + movieData.get("imdbID") + ".jpg";
		if(!new File(posterSaveLoc).exists())
			Utility.saveImage(data.get("Poster").toString(), posterSaveLoc);
	}
	
	public void switchHQPoster(int imageHeight, String apiKey)
	{
		String imgURL = posterApiURL + getimdbID() + "&apikey=" + apiKey + "&h=" + imageHeight;
		FileUtils.deleteQuietly(new File(posterSaveLoc));
		Utility.saveImage(imgURL, posterSaveLoc);
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public String getTitle()
	{
		return movieData.get(keys[0]);
	}
	
	public int getYear()
	{
		try
		{
			return Integer.parseInt(movieData.get(keys[1]));
		}
		catch(Exception e)
		{
			return 1800;
		}
	}
	
	public String getRated()
	{
		return movieData.get(keys[2]);
	}
	
	public String getRelease()
	{
		return movieData.get(keys[3]);
	}
	
	public int getRuntime()
	{
		try
		{
			return Integer.parseInt(movieData.get(keys[4]).substring(0, movieData.get(keys[4]).indexOf(" ")));
		}
		catch(Exception e)
		{
			return 0;
		}
		
	}
	
	public void setFileSize(File file)
	{
		this.fileSize = Utility.getFileSize(file);
	}
	
	public String getfileSize()
	{
		return fileSize;
	}
	
	public String getGenre()
	{
		return movieData.get(keys[5]);
	}
	
	public String getDirector()
	{
		return movieData.get(keys[6]);
	}
	
	public String getWriter()
	{
		return movieData.get(keys[7]);
	}
	
	public String getActors()
	{
		return movieData.get(keys[8]);
	}
	
	public List<String> getActorList()
	{
		List<String> actors = Arrays.asList(getActors().split(","));
		for (int i = 0; i < actors.size(); i++) 
		{
		    actors.set(i, actors.get(i).trim());
		}
		return actors;
	}
	
	public String getPlot()
	{
		return movieData.get(keys[9]);
	}
	
	public String getAwards()
	{
		return movieData.get(keys[10]);
	}
	
	public float getimdbRating()
	{
		if(movieData.get(keys[12]).equals("N/A"))
			return 0;
		else
			return Float.parseFloat(movieData.get(keys[12]));
	}
	
	public int getimdbVotes()
	{
		if(movieData.get(keys[13]).equals("N/A"))
			return 0;
		else
			return Integer.parseInt(movieData.get(keys[13]).toString().replace(",", ""));
	}
	
	public String getimdbID()
	{
		return movieData.get(keys[14]);
	}
	
	public String getPoster()
	{
		return movieData.get(keys[11]);
	}
	
	public String getPosterSaveLoc()
	{
		return posterSaveLoc;
	}
	
	public void setPosterSaveLoc(String posterSaveLoc)
	{
		this.posterSaveLoc = posterSaveLoc;
	}
	
	public Map<String, String> getMovieData()
	{
		return movieData;
	}

	@Override
	public String toString() 
	{
		return getTitle() + " (" + getYear() +")";
	}

	@Override
	public int compareTo(MovieEntry mv) 
	{
		return this.file.compareTo(mv.getFile());
	}
}