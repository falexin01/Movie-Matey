package com.alexin.address;

import java.util.Comparator;

public class RuntimeComparator implements Comparator<MovieEntry>
{

	public int compare(MovieEntry mv1, MovieEntry mv2) 
	{
		if((mv2.getRuntime() - mv1.getRuntime()) == 0)
			return mv1.getTitle().compareTo(mv2.getTitle());
		else
			return mv2.getRuntime() - mv1.getRuntime();
	}
	
	public String toString() 
	{
		return "Runtime";
	}
}
