package com.alexin.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class InfoRequest 
{
	private final static String USER_AGENT = "Mozilla/5.0";
	private static String dbURL = "http://www.omdbapi.com/?";
	private String url;
	private StringBuffer response;

	public InfoRequest(String title, String year)
	{	
		title = title.replace(" ", "%20");
		if(year.length() == 4)
			this.url = dbURL + "t=" + title + "&y=" + year;
		else
			this.url = dbURL + "t=" + title;
	}

	public InfoRequest(String titleID, boolean search)
	{
		titleID = titleID.replace(" ", "%20");
		if(search)
			this.url = dbURL + "s=" + titleID;
		else
			this.url = dbURL + "i=" + titleID;
	}
	
	public boolean sendRequest()
	{
		URL obj;
		try 
		{
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(5000);
			
			int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
			}
			in.close();
			
			if(responseCode != 200)
			{
				return false;
			}
			else if(response == null)
			{
				return false;
			}
			else
				return true;
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (java.net.SocketTimeoutException e) 
		{
			e.printStackTrace();
			response.append("{\"Error\":\"Server timed out...\"}");
			System.out.println(response);
			return false;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		} 
	}
	
	public JSONObject getJSONResponse()
	{
		JSONParser parser = new JSONParser();
		try 
		{
			JSONObject JSONResponse = (JSONObject) parser.parse(response.toString());
			return JSONResponse;
			
		} catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONArray getSearchResponse()
	{
		JSONParser parser = new JSONParser();
		try 
		{
			JSONObject JSONResponse = (JSONObject) parser.parse(response.toString());
			JSONArray searchResponse = (JSONArray) JSONResponse.get("Search");
			return searchResponse;
			
		} catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<MovieEntry> getSearchArray()
	{
		ArrayList<MovieEntry> searchResults = new ArrayList<MovieEntry>();
		JSONArray jsonArray = getSearchResponse();
		Iterator<?> it = jsonArray.iterator();
		while(it.hasNext())
		{
			JSONObject obj = (JSONObject) it.next();
			searchResults.add(new MovieEntry(obj, true));
		}
		return searchResults;
	}
	
	public String getStringResponse() 
	{
		return response.toString();
	}
}

