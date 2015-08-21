package com.alexin.address;

import java.util.Comparator;

public class YearComparator implements Comparator<MovieEntry>
{

	public int compare(MovieEntry mv1, MovieEntry mv2) 
	{
		if((mv2.getYear() - mv1.getYear()) == 0)
			return mv1.getTitle().compareTo(mv2.getTitle());
		else
			return mv2.getYear() - mv1.getYear();
	}
	
	public String toString() 
	{
		return "Year";
	}
	
}
